package com.yang.face.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.constant.enums.PhotoType;
import com.yang.face.constant.enums.UserTypeEnum;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.middle.ByteFile;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatureShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.FaceEngineService;
import com.yang.face.service.FaceService;
import com.yang.face.service.UserInfoService;
import com.yang.face.util.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author yangyuyang
 */
@Service
public class FaceServiceImpl implements FaceService {

    private static final Logger logger = LoggerFactory.getLogger(FaceServiceImpl.class);

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private FaceEngineService faceEngineService;

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
            List<UserInfo> userInfoList = new ArrayList<>();

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

    /**
     * 批量导入人脸库，并更新
     *
     * @param list
     * @return
     */
    @Override
    @Transactional
    public List<ImportFeatureShow> importFeatures(List<ImportFeaturePost> list) {

        List<ImportFeatureShow> res = new ArrayList<>();
        int rowCount = 0;

        try {

            // 0. 获取所有已存在的用户
            //List<UserInfo> users = userInfoMapper.selectAll();
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


                // 2. 人脸特征获取
                String filePath = Properties.SERVER_RESOURCE_IMAGE_FEATRUE + byteFile.getFileName();
                byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
                if (bytes == null) {
                    res.add(new ImportFeatureShow(o.getUserId(), o.getUserId(), o.getType(), "", "", false, "未检测到人脸"));
                    continue;
                } else {

                    // 3. 保存图片
                    if (!NetUtil.byte2File(byteFile.getBytes(), filePath)) {
                        filePath = "";
                    }
                    res.add(new ImportFeatureShow(o.getUserId(), o.getUserId(), o.getType(), PathUtil.getUrl(filePath), PathUtil.getRelPath(filePath), true, ""));
                }

                // 4.0 更新特征
                if (!userMap.containsKey(o.getUserId())) {
                    usersAdd.add(new UserInfo(null, o.getUserId(), o.getUserId(), UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                            PathUtil.getRelPath(filePath), FaceFeatureTypeEnum.ARC_SOFT.getKey(), bytes, "", 0, DateUtil.date(), DateUtil.date()));
                } else {
                    Example example = new Example(UserInfo.class);

                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId", o.getUserId());

                    UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                            PathUtil.getRelPath(filePath), FaceFeatureTypeEnum.ARC_SOFT.getKey(), bytes, null, null, null, DateUtil.date());
                    rowCount += userInfoMapper.updateByExampleSelective(userInfo, example);
                }
            }

            // 4.1 批量插入特征, 每次插入100条
            if (!usersAdd.isEmpty()) {
                int index = usersAdd.size() / 100;
                for (int i = 0; i <= index; i++) {
                    //stream流表达式，skip表示跳过前i*100条记录，limit表示读取当前流的前100条记录
                    rowCount += userInfoMapper.insertList(usersAdd.stream().skip(i * 100).limit(100).collect(Collectors.toList()));
                }
            }

            // 删除缓存
            if (rowCount > 0) {
                userInfoService.clearSelectAllCache();
            }

        } catch (Exception e) {
            logger.error("", e);
        }

        return res;
    }

    @Override
    public List<ImportFeatureShow> importFeatures(String fileZip) {
        List<ImportFeatureShow> list = new ArrayList<>();

        fileZip = PathUtil.getRelPath(fileZip);

        // 1、解压
        String timeStr = DateTimeUtil.getTimeStamp();
        String zipFilePath = Properties.SERVER_RESOURCE + fileZip;
        String unzipFilePath = Properties.SERVER_RESOURCE + Constants.Dir.UPLOAD + timeStr + "/";
        boolean state = new ZipUtil().unzip(zipFilePath, unzipFilePath, false);
        if (!state) {
            return list;
        }

        List<ImportFeatureShow> fails = new ArrayList<>();// 不在人脸库中
        List<ImportFeatureShow> featureFails = new ArrayList<>();// 提取特征失败
        List<ImportFeatureShow> sameNameFails = new ArrayList<>();// 名称相同
        List<ImportFeatureShow> scoreFails = new ArrayList<>();// 分数低相同

        // 2、提取特征，3、插入数据库
        try {
            List<UserInfo> userInfoAll = userInfoService.selectAll();
            Map<String, ImportFeatureShow> userMap = new ConcurrentHashMap<String, ImportFeatureShow>();

            // 建立userId和上传图片文件对应关系
            List<File> files = FileUtil.getFilesAll(Properties.SERVER_RESOURCE + Constants.Dir.UPLOAD + timeStr);
            for (File f : files) {
                if (!f.isFile()) {
                    continue;
                }

                String photoUrl = PathUtil.getUrl(f.getAbsolutePath());
                // String photoUrl = "http://192.168.3.4:8081/" +
                String picUserId = f.getName().substring(0, f.getName().lastIndexOf('.'));
                String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length());

                if (!supportImage(ext)) {
                    continue;
                }

                Boolean flag = false;
                Boolean sameNameFlag = false;
                if (userMap.containsKey(picUserId)) {
                    sameNameFlag = true;
                } else {
                    for (UserInfo o : userInfoAll) {
                        if (o.getUserId().equals(picUserId)) {
                            userMap.put(o.getUserId(), new ImportFeatureShow(o.getUserName(), photoUrl, f.getName()));
                            flag = true;
                            break;
                        }
                    }
                }

                if (!flag) {
                    String fileName = f.getName();
                    String photoAbsNew = f.getParent() + "/" + fileName.substring(0, fileName.lastIndexOf(".")) + "_c"
                            + fileName.substring(fileName.lastIndexOf("."), fileName.length());

                    Thumbnails.Builder<File> fileBuilder = Thumbnails.of(f.getAbsolutePath()).scale(1.0)
                            .outputQuality(1.0);
                    BufferedImage src = fileBuilder.asBufferedImage();
                    int size = src.getWidth() < src.getHeight() ? src.getWidth() : src.getHeight();

                    Thumbnails.of(f.getAbsolutePath()).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0)
                            .size(300, 300).toFile(photoAbsNew);

                    if (sameNameFlag) {
                        sameNameFails.add(new ImportFeatureShow(picUserId, "", PhotoType.IMAGE.getKey(),
                                PathUtil.getUrl(photoAbsNew), f.getName(), false,
                                picUserId + " 存在多张照片,不再导入"));
                    } else {
                        fails.add(new ImportFeatureShow(picUserId, "", PhotoType.IMAGE.getKey(),
                                PathUtil.getUrl(photoAbsNew), f.getName(), false,
                                picUserId + " 不在人脸库中"));
                    }

                }
            }

            /* 同步特征列表
            Map<String, List<IdFile>> syncMap = new ConcurrentHashMap<>();
            for (String userId : userMap.keySet()) {
                ImportFeatureShow user = userMap.get(userId);
                String photoUrl = user.getPhoto();

                // 移动到image/face中，压缩图片，最大不超过300*300
                String photoAbs = PathUtil.getAbsPath(photoUrl);
                Thumbnails.Builder<File> fileBuilder = Thumbnails.of(photoAbs).scale(1.0).outputQuality(1.0);
                BufferedImage src = fileBuilder.asBufferedImage();
                int size = src.getWidth() < src.getHeight() ? src.getWidth() : src.getHeight();
                fileBuilder.toFile(photoAbs); // 重置照片，放正

                String fileName = new File(photoAbs).getName();
                String PhotoAbsNew = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE
                        + fileName.substring(0, fileName.lastIndexOf(".")) + "_c"
                        + fileName.substring(fileName.lastIndexOf("."), fileName.length());

                Thumbnails.of(photoAbs).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0).size(300, 300)
                        .toFile(PhotoAbsNew);

                // 评分
                String imageUrl = PathUtil.getUrl(photoUrl);
                ScoreCopy score = null;
                score = new FaceExtralSender().faceScoreCopyImage(imageUrl);
                if (score == null || !score.getState()) {
                    StateMsg stateMsg = ScoreCopy2StateMsg(score);
                    scoreFails.add(new ImportFeatureShow(userId, user.getUserName(), PhotoType.IMAGE.getKey(),
                            PathUtil.getTotalUrl(PathUtil.getRelativePath(PhotoAbsNew)), user.getPhotoName(), false,
                            stateMsg.getMsg()));
                    continue;
                }

                List<FeatureSend> featureSends = new ArrayList<>();
                FeatureSend featureSend = new FeatureSend(userId, 1, photoUrl);
                featureSends.add(featureSend);

                List<IdFile> features = new ArrayList<>();
                //FeatureAddr featureAddr = that.faceSyncSender.faceFeatureAddr(featureSends);
                FeatureAddr featureAddr = new FaceExtralSender().faceFeatureAddr(featureSends);
                if (featureAddr != null) {
                    features.addAll(featureAddr.getList());

                    if (!syncMap.containsKey(featureAddr.getAddr())) {
                        syncMap.put(featureAddr.getAddr(), featureAddr.getList());
                    } else {
                        List<IdFile> temp = syncMap.get(featureAddr.getAddr());
                        temp.addAll(featureAddr.getList());
                        syncMap.put(featureAddr.getAddr(), temp);
                    }
                }

                if (features == null || features.size() == 0) {
                    featureFails.add(new ImportFeatureShow(userId, user.getUserName(), PhotoType.IMAGE.getKey(),
                            PathUtil.getUrl(PhotoAbsNew), user.getPhotoName(), false,
                            "特征提取失败"));
                } else {
                    // 移动到Face文件夹下
                    FileUtil.moveFile(photoAbs, Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE);
                    list.add(new ImportFeatureShow(userId, user.getUserName(), PhotoType.IMAGE.getKey(),
                            PathUtil.getUrl(PhotoAbsNew), user.getPhotoName(), true,
                            score.getScore().toString()));

                    Integer res = mapper.updatePhoto2ByUserId(userId,
                            PathUtil.getUrl(hotoAbsNew),
                            PhotoSourceType.UPLOAD.getKey(), features.get(0).getFeatureFile(), score.getScore());
                }
            }

            // 同步
            for (String key : syncMap.keySet()) {
                new FaceExtralSender().featureSyncByIdFile(key, syncMap.get(key));
            }
*/

            list.addAll(featureFails);
            list.addAll(scoreFails);
            list.addAll(sameNameFails);
            list.addAll(fails);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // 4、统计提取特征结果

        return list;
    }

    /**
     * 更新人脸库特征
     *
     * @return
     */
    @Override
    public MessageVO updateFeatures() {
        return null;
    }

    public ByteFile getPhotoByteFile(int type, String srcPhoto) throws IOException {
        // url
        ByteFile byteFile = new ByteFile();
        if (type == PhotoType.IMAGE.getKey()) {
            byteFile.setBytes(NetUtil.image2byte(srcPhoto));
            byteFile.setExt(srcPhoto.substring(srcPhoto.lastIndexOf('.')));
            byteFile.setFileName(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + byteFile.getExt());
            return byteFile;
        } else if (type == PhotoType.BASE64.getKey()) {
            String dstPhotoExt = ".jpg";

            byteFile.setBytes(Base64.decode(Base64Util.base64Process(srcPhoto)));
            byteFile.setExt(".jpg");
            byteFile.setFileName(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + dstPhotoExt);
            return byteFile;
        }

        return null;
    }

    public boolean supportImage(String ext) {
        String[] exts = { "JPG", "JPEG", "PNG", "BMP" };

        boolean state = false;
        for (String string : exts) {
            if (string.equalsIgnoreCase(ext)) {
                state = true;
                break;
            }
        }

        return state;
    }
}
