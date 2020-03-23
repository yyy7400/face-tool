package com.yang.face.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.constant.enums.PhotoTypeEnum;
import com.yang.face.constant.enums.UserTypeEnum;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.middle.ActionFaceRecognitionImage;
import com.yang.face.entity.middle.ByteFile;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatureShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.FaceEngineService;
import com.yang.face.service.FaceService;
import com.yang.face.service.SystemSettingService;
import com.yang.face.service.UserInfoService;
import com.yang.face.util.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 虹软人脸识别
 *
 * @author yangyuyang
 */
@Service
public class FaceArcServiceImpl implements FaceService {

    private static final Logger logger = LoggerFactory.getLogger(FaceArcServiceImpl.class);

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private FaceEngineService faceEngineService;
    @Resource
    SystemSettingService systemSettingService;

    @Override
    public Integer faceType() {
        return FaceFeatureTypeEnum.ARC_SOFT.getKey();
    }

    @Override
    public List<FaceRecoShow> recoImageRoom(Integer type, String photo, List<String> userIds) {
        return new ArrayList<>();
    }

    /**
     * 人脸识别, userIds.isEmpty() 时对比全部
     *
     * @param type
     * @param photo   目前只支持base64，url地址需要下载
     * @param userIds
     * @return
     */
    @Override
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {

        List<FaceRecoShow> list = new ArrayList<>();
        try {

            // 1. 图片转转byte
            ByteFile byteFile = getPhotoByteFile(type, photo);
            if (byteFile == null) {
                return list;
            }
            ImageInfo imageInfo = ImageFactory.getRGBData(byteFile.getBytes());

            // 2. 人脸特征获取
            byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
            if (bytes == null) {
                return list;
            }

            // 3. 从数据库中取出人脸库
            List<UserInfo> userInfoList;

            // 缓存
            if (userIds.isEmpty()) {
                userInfoList = userInfoService.selectAll();
            } else {
                List<UserInfo> tmp = userInfoService.selectAll();
                userInfoList = userInfoService.selectByUserIds(userIds, tmp);
            }

            if (userInfoList.isEmpty()) {
                return list;
            }

            // 4. 识别到的人脸列表
            List<FaceUserInfo> faces = faceEngineService.compareFaceFeature(bytes, userInfoList);

            for (FaceUserInfo o : faces) {
                list.add(new FaceRecoShow(o.getUserId(), o.getSimilarityScore(), PathUtil.getUrl(o.getPhotoUrl())));
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return list;
    }

    /**
     * 清理人脸库特征
     *
     * @param userIds
     * @return
     */
    @Override
    public MessageVO cleanFeature(List<String> userIds) {


        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria();

            if (userIds.isEmpty()) {
                userInfoMapper.deleteByExample(example);
            } else {
                criteria.andIn("userId", userIds);
                userInfoMapper.deleteByExample(example);
            }

            userInfoService.clearSelectAllCache();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new MessageVO(false, "删除失败");
        }

        return new MessageVO(true, "");
    }

    @Override
    public MessageVO cleanFeatureUpdate(List<String> userIds) {

        MessageVO messageVO = new MessageVO(false, "");

        Example example = new Example(UserInfo.class);
        example.createCriteria().andIn("userId", userIds);

        UserInfo userInfo = new UserInfo();
        userInfo.setPhotoUrl("");
        userInfo.setFaceFeatureByte(new byte[0]);
        userInfo.setFaceFeatureFile("");
        userInfo.setScore(0);
        userInfo.setUpdateTime(new DateTime());
        int res = userInfoMapper.updateByExampleSelective(userInfo, example);

        if(res > 0) {
            messageVO.setState(true);
        }

        return messageVO;
    }

    @Override
    public List<ImportFeatureShow> importFeaturesNoUpadte(List<ImportFeaturePost> list) {
        List<ImportFeatureShow> res = new ArrayList<>();

        try {

            // 0. 获取所有已存在的用户
            // 缓存
            List<UserInfo> users = userInfoService.selectAll();
            Map<String, UserInfo> userMap = users.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

            List<UserInfo> usersAdd = new ArrayList<>();
            for (ImportFeaturePost o : list) {

                // 1. 图片转转byte
                ByteFile byteFile = getPhotoByteFile(o.getType(), o.getPhoto());
                if (byteFile == null) {
                    continue;
                }
                ImageInfo imageInfo = ImageFactory.getRGBData(byteFile.getBytes());
                List<FaceInfo> faceInfos = faceEngineService.detectFaces(imageInfo);

                String userName = userMap.containsKey(o.getUserId()) ? userMap.get(o.getUserId()).getUserName() : o.getUserId();
                // 2. 人脸特征获取
                String filePath = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE + byteFile.getFileName();
                String fileIconPath = "";

                byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
                if (bytes == null) {
                    res.add(new ImportFeatureShow(o.getUserId(), userName, o.getType(), PathUtil.getUrl(filePath), "", false, "未检测到人脸"));
                    continue;
                } else {

                    // 3. 保存图片
                    if (!NetUtil.byte2File(byteFile.getBytes(), filePath)) {
                        filePath = "";
                    }
                    fileIconPath = IamgeUtil.getFaceIcon(filePath);
                    res.add(new ImportFeatureShow(o.getUserId(), userName, o.getType(), PathUtil.getUrl(fileIconPath), PathUtil.getRelPath(fileIconPath), true, ""));
                }

                // 4.0 更新特征
                if (!userMap.containsKey(o.getUserId())) {
                    usersAdd.add(new UserInfo(null, o.getUserId(), userName, UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                            PathUtil.getRelPath(fileIconPath), FaceFeatureTypeEnum.ARC_SOFT.getKey(), bytes, "", 80, DateUtil.date(), DateUtil.date()));
                } else {
                    Example example = new Example(UserInfo.class);

                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId", o.getUserId());

                    UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                            PathUtil.getRelPath(fileIconPath), FaceFeatureTypeEnum.ARC_SOFT.getKey(), bytes, null, 80, null, DateUtil.date());
                    userInfoMapper.updateByExampleSelective(userInfo, example);
                }
            }

            // 4.1 批量插入特征, 每次插入100条
            if (!usersAdd.isEmpty()) {
                int index = usersAdd.size() / 100;
                for (int i = 0; i <= index; i++) {
                    //stream流表达式，skip表示跳过前i*100条记录，limit表示读取当前流的前100条记录
                    userInfoMapper.insertList(usersAdd.stream().skip(i * 100).limit(100).collect(Collectors.toList()));
                }
            }

        } catch (Exception e) {
            logger.error("", e);
        }

        return res;
    }

    /**
     * 批量导入人脸库，并更新
     *
     * @param list
     * @return
     */
    @Override
    @Transactional
    public List<ImportFeatureShow> importFeatures(List<ImportFeaturePost> list) {

        List<ImportFeatureShow> res = importFeaturesNoUpadte(list);
        // 删除缓存
        userInfoService.clearSelectAllCache();

        return res;
    }

    @Override
    public List<ImportFeatureShow> importFeatures(String zipPath) {
        List<ImportFeatureShow> res = new ArrayList<>();

        zipPath = PathUtil.getRelPath(zipPath);

        /* 1、解压压缩包 */
        String timeStr = DateTimeUtil.getTimeStamp();
        String zipFilePath = Properties.SERVER_RESOURCE + zipPath;
        String unzipFilePath = Properties.SERVER_RESOURCE + Constants.Dir.UPLOAD + timeStr + "/";
        boolean state = new ZipUtil().unzip(zipFilePath, unzipFilePath, false);
        if (!state) {
            return res;
        }

        List<ImportFeatureShow> featureFails = new ArrayList<>();// 提取特征失败
        List<ImportFeatureShow> sameNameFails = new ArrayList<>();// 名称相同
        List<ImportFeatureShow> scoreFails = new ArrayList<>();// 分数低相同


        try {
            /* 2. 筛选出合格的图片 */
            List<UserInfo> userInfoAll = userInfoService.selectAll();
            Map<String, UserInfo> mapDB = userInfoAll.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            Map<String, ImportFeatureShow> userMap = new ConcurrentHashMap<>();

            // 建立userId和上传图片文件对应关系
            List<File> files = FileUtil.getFilesAll(Properties.SERVER_RESOURCE + Constants.Dir.UPLOAD + timeStr);
            for (File f : files) {
                if (!f.isFile()) {
                    continue;
                }

                String photoUrl = PathUtil.getUrl(f.getAbsolutePath());
                // String photoUrl = "http://192.168.3.4:8081/" +
                String picUserId = f.getName().substring(0, f.getName().lastIndexOf('.'));
                String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1);

                if (!FaceService.supportImage(ext)) {
                    continue;
                }

                boolean sameNameFlag = false;
                if (userMap.containsKey(picUserId)) {
                    sameNameFlag = true;
                } else {
                    if (mapDB.containsKey(picUserId)) {
                        userMap.put(picUserId, new ImportFeatureShow(picUserId, mapDB.get(picUserId).getUserName(), photoUrl, f.getName()));
                    } else {
                        userMap.put(picUserId, new ImportFeatureShow(picUserId, picUserId, photoUrl, f.getName()));
                    }
                }

                if (sameNameFlag) {
                    String fileName = f.getName();
                    String photoAbsNew = f.getParent() + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "_c"
                            + fileName.substring(fileName.lastIndexOf("."));

                    Thumbnails.Builder<File> fileBuilder = Thumbnails.of(f.getAbsolutePath()).scale(1.0)
                            .outputQuality(1.0);
                    BufferedImage src = fileBuilder.asBufferedImage();
                    int size = Math.min(src.getWidth(), src.getHeight());

                    Thumbnails.of(f.getAbsolutePath()).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0)
                            .size(300, 300).toFile(photoAbsNew);

                    sameNameFails.add(new ImportFeatureShow(picUserId, "", PhotoTypeEnum.IMAGE.getKey(),
                            PathUtil.getUrl(photoAbsNew), f.getName(), false,
                            picUserId + " 存在多张照片,不再导入"));

                }
            }

            /* 3.提取特征，并插入数据库*/
            List<UserInfo> usersAdd = new ArrayList<>();
            /* 同步特征列表 */
            for (String userId : userMap.keySet()) {
                ImportFeatureShow user = userMap.get(userId);
                String photoUrl = user.getPhoto();

                // 1. 图片转转byte
                ByteFile byteFile = getPhotoByteFile(PhotoTypeEnum.IMAGE.getKey(), PathUtil.getUrl(photoUrl));
                if (byteFile == null) {
                    continue;
                }
                ImageInfo imageInfo = ImageFactory.getRGBData(byteFile.getBytes());
                List<FaceInfo> faceInfos = faceEngineService.detectFaces(imageInfo);

                String userName = mapDB.containsKey(user.getUserId()) ? mapDB.get(user.getUserId()).getUserName() : user.getUserId();
                // 2. 人脸特征获取
                String filePath = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE + byteFile.getFileName();
                String fileIconPath = "";

                byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
                if (bytes == null) {
                    res.add(new ImportFeatureShow(user.getUserId(), userName, user.getType(), PathUtil.getUrl(filePath), "", false, "未检测到人脸"));
                    continue;
                } else {

                    // 3. 保存图片
                    if (!NetUtil.byte2File(byteFile.getBytes(), filePath)) {
                        filePath = "";
                    }
                    fileIconPath = IamgeUtil.getFaceIcon(filePath);
                    res.add(new ImportFeatureShow(user.getUserId(), userName, user.getType(), PathUtil.getUrl(fileIconPath), PathUtil.getRelPath(fileIconPath), true, ""));
                }

                // 4.0 更新特征
                if (!mapDB.containsKey(user.getUserId())) {
                    usersAdd.add(new UserInfo(null, user.getUserId(), userName, UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                            PathUtil.getRelPath(fileIconPath), FaceFeatureTypeEnum.ARC_SOFT.getKey(), bytes, "", 80, DateUtil.date(), DateUtil.date()));
                } else {
                    Example example = new Example(UserInfo.class);

                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId", user.getUserId());

                    UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                            PathUtil.getRelPath(fileIconPath), FaceFeatureTypeEnum.ARC_SOFT.getKey(), bytes, null, 80, null, DateUtil.date());
                    userInfoMapper.updateByExampleSelective(userInfo, example);
                }

            }

            // 4.1 批量插入特征, 每次插入100条
            if (!usersAdd.isEmpty()) {
                int index = usersAdd.size() / 100;
                for (int i = 0; i <= index; i++) {
                    //stream流表达式，skip表示跳过前i*100条记录，limit表示读取当前流的前100条记录
                    userInfoMapper.insertList(usersAdd.stream().skip(i * 100).limit(100).collect(Collectors.toList()));
                }
            }

            // 删除缓存
            userInfoService.clearSelectAllCache();

            res.addAll(featureFails);
            res.addAll(scoreFails);
            res.addAll(sameNameFails);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 4、统计提取特征结果
        return res;
    }

    /**
     * 更新人脸库特征
     */
    @Override
    public MessageVO updateFeatures() {
        return new MessageVO(true, "");
    }

    @Override
    public MessageVO getPhotoScore(String photo) {

        try {

            photo = PathUtil.getAbsPath(photo);

            // 1. 图片转转byte
            ByteFile byteFile = getPhotoByteFile(PhotoTypeEnum.IMAGE.getKey(), photo);
            if (byteFile == null) {
                return new MessageVO(false, "图片错误");
            }
            ImageInfo imageInfo = ImageFactory.getRGBData(byteFile.getBytes());
            List<?> list = faceEngineService.detectFaces(imageInfo);
            if (list.isEmpty()) {
                return new MessageVO(false, "未检测到人脸");
            } else if (list.size() > 1) {
                return new MessageVO(false, "图片中有多张人脸");
            } else {
                return new MessageVO(true, 90);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new MessageVO(false, "内部错误");
        }
    }

    @Override
    public MessageVO startDetectionVideo(String rtspUrl) {
        return new MessageVO(false, "此版本不支持");
    }

    @Override
    public MessageVO stopDetectionVideo(String rtspUrl) {
        return new MessageVO(false, "此版本不支持");
    }

    @Override
    public MessageVO startDetectionVideoAction(String rtspUrl) {
        return new MessageVO(false, "此版本不支持");
    }

    @Override
    public MessageVO stopDetectionVideoAction(String rtspUrl) {
        return new MessageVO(false, "此版本不支持");
    }

    @Override
    public List<ActionFaceRecognitionImage> faceRecognitionImageAction(Integer type, String photo, String scheduleId, List<String> userIds) {
        return new ArrayList<>();
    }


    public ByteFile getPhotoByteFile(int type, String srcPhoto) throws IOException {
        // url
        ByteFile byteFile = new ByteFile();
        if (type == PhotoTypeEnum.IMAGE.getKey()) {
            byteFile.setBytes(NetUtil.image2byte(srcPhoto));
            byteFile.setExt(srcPhoto.substring(srcPhoto.lastIndexOf('.')));
            byteFile.setFileName(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + byteFile.getExt());
            return byteFile;
        } else if (type == PhotoTypeEnum.BASE64.getKey()) {
            String dstPhotoExt = ".jpg";

            byteFile.setBytes(Base64.decode(Base64Util.base64Process(srcPhoto)));
            byteFile.setExt(".jpg");
            byteFile.setFileName(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + dstPhotoExt);
            return byteFile;
        }

        return null;
    }

}
