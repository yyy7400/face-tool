package com.yang.face.service.impl;

import cn.hutool.core.date.DateUtil;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.constant.enums.PhotoTypeEnum;
import com.yang.face.constant.enums.UserTypeEnum;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.middle.FaceRecognitionImage;
import com.yang.face.entity.middle.FaceScoreImageMod;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatureShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.FaceService;
import com.yang.face.service.PythonApiService;
import com.yang.face.service.UserInfoService;
import com.yang.face.util.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yangyuyang
 * @date 2020/3/13 16:59
 */
@Service()
public class FacePythonServiceImpl implements FaceService {

    private final static Logger logger = LoggerFactory.getLogger(FacePythonServiceImpl.class);

    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    UserInfoService userInfoService;
    @Resource
    PythonApiService pythonApiService;

    @Autowired
    public Integer faceType() {
        return FaceFeatureTypeEnum.OPENVINO.getKey();
    }

    @Override
    public List<FaceRecoShow> recoImageRoom(Integer type, String photo, List<String> userIds) {
        // String url = photo;
        List<FaceRecoShow> faces = new ArrayList<>();

        String photoUrl = getPhoto(type, photo);
        if (photoUrl.isEmpty())
            return new ArrayList<>();

        List<FaceRecognitionImage> list = pythonApiService.faceRecognitionImage(type, photo, userIds);
        list.forEach(o ->
                faces.add(new FaceRecoShow(o.getUserId(), resetSimilarityScore(o.getSimilarityScore()), o.getHeadPhoto()))
        );

        return faces;
    }

    @Override
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {
        // String url = photo;
        List<FaceRecoShow> faces = new ArrayList<>();

        String photoUrl = getPhoto(type, photo);
        if (photoUrl.isEmpty())
            return new ArrayList<>();

        List<FaceRecognitionImage> list = pythonApiService.faceRecognitionImageEC(type, photo, userIds);
        list.forEach(o ->
                faces.add(new FaceRecoShow(o.getUserId(), resetSimilarityScore(o.getSimilarityScore()), o.getHeadPhoto()))
        );

        return faces;
    }

    @Override
    public MessageVO cleanFeature(List<String> userIds) {

        Example example = new Example(UserInfo.class);

        try {
            if (userIds.isEmpty()) {
                userInfoMapper.deleteByExample(example);

                FileUtil.deleteDir(Properties.SERVER_RESOURCE + Constants.Dir.FACE_FEATRUE);

            } else {
                example.createCriteria().andIn("userId", userIds);
                List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
                userInfoMapper.deleteByExample(example);

                userInfos.forEach(o -> {
                    if ("".equals(o.getFaceFeatureFile())) {
                        return;
                    }
                    try {
                        File file = new File(PathUtil.getAbsPath(o.getFaceFeatureFile()));
                        file.delete();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                });
            }

            // 通知 client 更新特征
            updateFeatures();

            // 更新缓存
            userInfoService.clearSelectAllCache();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new MessageVO(false, "删除失败");
        }

        return new MessageVO(true, "");

    }

    /**
     * 待完善，批量特征提取加入多线程处理会快一些
     * @param list
     * @return
     */
    @Override
    public List<ImportFeatureShow> importFeaturesNoUpadte(List<ImportFeaturePost> list) {
        List<ImportFeatureShow> res = new ArrayList<>();

        // 0. 获取所有已存在的用户
        // 缓存
        List<UserInfo> users = userInfoService.selectAll();
        Map<String, UserInfo> userMap = users.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

        List<UserInfo> usersAdd = new ArrayList<>();

        for (ImportFeaturePost o : list) {
            String photoUrl = getPhoto(o.getType(), o.getPhoto());
            // logger.info(photoUrl);
            if (photoUrl.isEmpty()) {
                ImportFeatureShow obj1 = new ImportFeatureShow(o.getUserId(), "", o.getType(), "", "", false, "图片保存失败");
                res.add(obj1);
                continue;
            }

            // 移动到image/face中，压缩图片，最大不超过300*300, 应该加入线程池队列中多线程处理
            IamgeUtil.getFaceIcon(photoUrl);

            // 评分
            String imageUrl = PathUtil.getUrl(photoUrl);
            FaceScoreImageMod score = pythonApiService.faceScoreIamgeMod(PhotoTypeEnum.IMAGE.getKey(), imageUrl);


            if (score == null || !score.getState()) {
                MessageVO msg = ScoreCopy2MessageVO(score);
                ImportFeatureShow obj11 = new ImportFeatureShow(o.getUserId(), "", o.getType(), imageUrl, "", false,
                        msg.getMsg());
                res.add(obj11);
                continue;
            }

            String userId = null;
            String featureFile = null;
            // userId, featureFile
            Map<String, String> map = pythonApiService.getFaceFeature(o.getUserId(), PhotoTypeEnum.IMAGE.getKey(), imageUrl);
            for (String key : map.keySet()) {
                userId = key;
                featureFile = map.get(key);
            }

            // 下载文件
            String featureFileLocal = FileUtil.downloadUrl(featureFile, Properties.SERVER_RESOURCE + Constants.Dir.FACE_FEATRUE, userId);

            if (map.isEmpty()) {
                ImportFeatureShow obj1 = new ImportFeatureShow(o.getUserId(), o.getUserId(), o.getType(), imageUrl, "", false,
                        "特征提取失败");
                res.add(obj1);

            } else {
                String userName = userMap.containsKey(o.getUserId()) ? userMap.get(o.getUserId()).getUserName() : o.getUserId();
                ImportFeatureShow obj1 = new ImportFeatureShow(o.getUserId(), userName, o.getType(), imageUrl, "", true, "");
                res.add(obj1);

                // 4.0 更新特征
                if (!userMap.containsKey(o.getUserId())) {
                    usersAdd.add(new UserInfo(null, o.getUserId(), userName, UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                            PathUtil.getRelPath(imageUrl), FaceFeatureTypeEnum.ARC_SOFT.getKey(), new byte[0], PathUtil.getRelPath(featureFileLocal), resetSimilarityScore(score.getScore()), DateUtil.date(), DateUtil.date()));
                } else {
                    Example example = new Example(UserInfo.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId", o.getUserId());

                    UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                            PathUtil.getRelPath(imageUrl), FaceFeatureTypeEnum.ARC_SOFT.getKey(), null, PathUtil.getRelPath(featureFileLocal), resetSimilarityScore(score.getScore()), null, DateUtil.date());
                    userInfoMapper.updateByExampleSelective(userInfo, example);
                }

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

        return res;
    }

    /**
     * 不支持站外的Url文件
     * @param list
     * @return
     */
    @Override
    public List<ImportFeatureShow> importFeatures(List<ImportFeaturePost> list) {

        List<ImportFeatureShow> res = importFeaturesNoUpadte(list);
        // 删除缓存
        userInfoService.clearSelectAllCache();

        // 通知client更新特征
        pythonApiService.updateFaceFeature();

        return res;
    }

    /**
     * 1. 解压文件
     * 2. 筛选出重复的图片，截取缩略图返回
     * 3. 人脸评分、提取特征、下载npy文件
     * 4. 更新和批量插入数据库
     * 5. 放回结果
     *
     * @param zipPath
     */
    @Override
    public List<ImportFeatureShow> importFeatures(String zipPath) {

        List<Long> times = new ArrayList<>();
        times.add(System.currentTimeMillis());

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
        times.add(System.currentTimeMillis());

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

            times.add(System.currentTimeMillis());

            /* 3.提取特征，并插入数据库*/
            List<UserInfo> usersAdd = new ArrayList<>();
            /* 同步特征列表 */
            for (String userId : userMap.keySet()) {
                ImportFeatureShow user = userMap.get(userId);
                String photoUrl = user.getPhoto();

                // 移动到image/face中，压缩图片，最大不超过300*300, 应该加入线程池队列中多线程处理
                IamgeUtil.getFaceIcon(photoUrl);

                // 评分
                String imageUrl = PathUtil.getUrl(photoUrl);
                // mq
                FaceScoreImageMod score = pythonApiService.faceScoreIamgeMod(PhotoTypeEnum.IMAGE.getKey(), imageUrl);

                if (score == null || !score.getState()) {
                    MessageVO msg = ScoreCopy2MessageVO(score);
                    ImportFeatureShow obj11 = new ImportFeatureShow(userId, "", user.getType(), imageUrl, "", false,
                            msg.getMsg());
                    res.add(obj11);
                    continue;
                }

                String featureFile = null;
                // userId, featureFile
                Map<String, String> map = pythonApiService.getFaceFeature(userId, PhotoTypeEnum.IMAGE.getKey(), imageUrl);
                for (String key : map.keySet()) {
                    featureFile = map.get(key);
                }

                // 下载文件
                String featureFileLocal = FileUtil.downloadUrl(featureFile, Properties.SERVER_RESOURCE + Constants.Dir.FACE_FEATRUE, userId);

                if (map.isEmpty()) {
                    ImportFeatureShow obj1 = new ImportFeatureShow(userId, userId, user.getType(), imageUrl, "", false,
                            "特征提取失败");
                    res.add(obj1);

                } else {
                    String userName = userMap.containsKey(userId) ? userMap.get(userId).getUserName() : userId;
                    ImportFeatureShow obj1 = new ImportFeatureShow(userId, userName, user.getType(), imageUrl, "", true, "");
                    res.add(obj1);

                    // 4.0 更新特征
                    if (!mapDB.containsKey(userId)) {
                        usersAdd.add(new UserInfo(null, userId, userName, UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                                PathUtil.getRelPath(imageUrl), FaceFeatureTypeEnum.ARC_SOFT.getKey(), new byte[0], PathUtil.getRelPath(featureFileLocal), resetSimilarityScore(score.getScore()), DateUtil.date(), DateUtil.date()));
                    } else {
                        Example example = new Example(UserInfo.class);
                        Example.Criteria criteria = example.createCriteria();
                        criteria.andEqualTo("userId", userId);

                        UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                                PathUtil.getRelPath(imageUrl), FaceFeatureTypeEnum.ARC_SOFT.getKey(), null, PathUtil.getRelPath(featureFileLocal), resetSimilarityScore(score.getScore()), null, DateUtil.date());
                        userInfoMapper.updateByExampleSelective(userInfo, example);
                    }

                }

            }

            times.add(System.currentTimeMillis());
            // 4.1 批量插入特征, 每次插入100条
            if (!usersAdd.isEmpty()) {
                int index = usersAdd.size() / 100;
                for (int i = 0; i <= index; i++) {
                    //stream流表达式，skip表示跳过前i*100条记录，limit表示读取当前流的前100条记录
                    userInfoMapper.insertList(usersAdd.stream().skip(i * 100).limit(100).collect(Collectors.toList()));
                }
            }
            times.add(System.currentTimeMillis());
            // 删除缓存
            userInfoService.clearSelectAllCache();
            times.add(System.currentTimeMillis());
            // 通知client更新特征
            pythonApiService.updateFaceFeature();
            times.add(System.currentTimeMillis());
            res.addAll(featureFails);
            res.addAll(scoreFails);
            res.addAll(sameNameFails);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        times.forEach(o -> System.out.println(o));
        return res;
    }

    @Override
    public MessageVO updateFeatures() {
        // 通知 client 更新特征
        pythonApiService.updateFaceFeature();
        try {
            // 等待2秒，all client 下载可能需要一些时间
            Thread.sleep(2000);
        } catch (Exception e) {
        }


        return new MessageVO(true, "");
    }

    @Override
    public MessageVO getPhotoScore(String photo) {

        photo = getPhoto(PhotoTypeEnum.IMAGE.getKey(), photo);
        FaceScoreImageMod faceScoreImageMod = pythonApiService.faceScoreIamgeMod(PhotoTypeEnum.IMAGE.getKey(), photo);
        return ScoreCopy2MessageVO(faceScoreImageMod);
    }

    public String getPhoto(int type, String photo) {
        String url = "";

        // url
        if (type == PhotoTypeEnum.IMAGE.getKey())
            try {
                url = URLDecoder.decode(photo, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        else if (type == PhotoTypeEnum.BASE64.getKey()) {
            String path = Properties.SERVER_RESOURCE + Constants.Dir.TEMP + DateTimeUtil.getTimeStamp() + ".jpg";

            String filePath = PathUtil.getAbsPath(path);
            boolean flag = FileUtil.base64ToImage(photo, filePath);
            if (flag)
                url = PathUtil.getUrl(path);
        }
        // 智慧考场，批量导入图片
        else if (type == PhotoTypeEnum.FTPURL_TEST.getKey()) {
            String ftpUrl = "FTP://LangeFtpUser_r1rvi:LangeFtpPwd*_!_Pwd@192.168.129.24:10302/LGFTP/StuSignUpPhoto/T20190220202524682/493FBEE7-E6E9-415B-A172-9F0B2795987C/G13.png";
            ftpUrl = photo;

            String[] strings = ftpUrl.split("@");
            int startStrLen = "FTP://".length();

            String userName = strings[0].substring(startStrLen).split(":")[0]; // LangeFtpUser_r1rvi
            String passWord = strings[0].substring(startStrLen).split(":")[1]; // LangeFtpPwd*_!_Pwd

            String ip = strings[1].split("/")[0].split(":")[0]; // 192.168.129.24
            String port = strings[1].split("/")[0].split(":")[1]; // 10302

            String filename = strings[1].split("/")[strings[1].split("/").length - 1]; // G13.png
            String pathname = strings[1].substring((ip + ":" + port + "/").length(),
                    strings[1].length() - filename.length() - 1); // LGFTP/StuSignUpPhoto/T20190220202524682/493FBEE7-E6E9-415B-A172-9F0B2795987C
            String localpath = Properties.SERVER_RESOURCE + Constants.Dir.TEMP + "ectest/";

            FtpUtil ftpUtil = new FtpUtil(ip, Integer.parseInt(port), userName, passWord);
            boolean state = ftpUtil.downloadFile(pathname, filename, localpath);
            ftpUtil.close();
            if (state) {
                url = localpath + filename;
            }

        }

        return url;
    }

    /**
     * 将余弦值映射到[0, 100]上
     */
    public static Integer resetSimilarityScore(double similarityScore) {
        double temp = Math.cos(similarityScore * Math.PI / 2);
        return (int) Math.round(temp * 100);
    }

    /**
     * face score state 说明
     *
     * @param score
     * @return
     */
    public static MessageVO ScoreCopy2MessageVO(FaceScoreImageMod score) {

        if (score == null) {
            return new MessageVO(false, "无法获取分数");
        } else if (!score.getState()) {
            if (!score.getState()) {
                return new MessageVO(false, " 照片不够清晰");
            }
            if (!score.getFaceState()) {
                return new MessageVO(false, " 识别不到人脸");
            }
            if (!score.getPxState()) {
                return new MessageVO(false, "人脸大小至少要150px*150px");
            }
            if (!score.getRetioState()) {
                return new MessageVO(false, "人脸占据图片50-70%比例最佳");
            }
            if (!score.getLocState()) {
                return new MessageVO(false, " 分数过低, 人脸位置太偏");
            }

            return new MessageVO(false, "分数过低");
        } else {
            return new MessageVO(true, score.getScore());
        }
    }


}
