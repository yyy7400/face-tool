package com.yang.face.service.impl;

import cn.hutool.core.date.DateUtil;
import com.yang.face.client.ClientManager;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.constant.enums.PhotoTypeEnum;
import com.yang.face.constant.enums.UserTypeEnum;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.middle.ActionFaceRecognitionImage;
import com.yang.face.entity.middle.DetectionVideo;
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
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
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

    private static List<DetectionVideo> dtcVideos = new ArrayList<>();
    private static List<DetectionVideo> dtcVideosAction = new ArrayList<>();

    @Override
    public Integer faceType() {
        return FaceFeatureTypeEnum.OPENVINO.getKey();
    }

    @Override
    public List<FaceRecoShow> recoImageRoom(Integer type, String photo, List<String> userIds) {
        // String url = photo;
        List<FaceRecoShow> faces = new ArrayList<>();

        String photoUrl = getPhoto(type, photo);
        if (photoUrl.isEmpty()) {
            return new ArrayList<>();
        }

        List<FaceRecognitionImage> list = pythonApiService.faceRecognitionImage(type, photo, userIds);
        list.forEach(o ->
                faces.add(new FaceRecoShow(o.getUserId(), resetSimilarityScore(o.getSimilarityScore()), o.getHeadPhoto(), o.getState()))
        );

        return faces;
    }

    @Override
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {
        // String url = photo;
        List<FaceRecoShow> faces = new ArrayList<>();

        String photoUrl = getPhoto(type, photo);
        if (photoUrl.isEmpty()) {
            return new ArrayList<>();
        }

        List<FaceRecognitionImage> list = pythonApiService.faceRecognitionImageEC(type, photo, userIds);
        list.stream().filter(o -> o.getState()).forEach(o ->
            faces.add(new FaceRecoShow(o.getUserId(), resetSimilarityScore(o.getSimilarityScore()), o.getHeadPhoto(), true))
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

    @Override
    public MessageVO cleanFeatureUpdate(List<String> userIds) {

        try {

            MessageVO messageVO = new MessageVO(false, "");
            Example example = new Example(UserInfo.class);
            example.createCriteria().andIn("userId", userIds);
            List<UserInfo> userInfos = userInfoMapper.selectByExample(example);

            UserInfo userInfo = new UserInfo();
            userInfo.setPhotoUrl("");
            userInfo.setFaceFeatureByte(new byte[0]);
            userInfo.setFaceFeatureFile("");
            userInfo.setScore(0);
            userInfo.setUpdateTime(new Date());
            int res = userInfoMapper.updateByExampleSelective(userInfo, example);

            if (res > 0) {
                messageVO.setState(true);
            }

            userInfos.forEach(o -> {
                if ("".equals(o.getFaceFeatureFile())) {
                    return;
                }
                try {
                    File file = new File(PathUtil.getAbsPath(o.getFaceFeatureFile()));
                    file.delete();
                    System.out.println(file.getAbsolutePath());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });

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
     *
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
            String[] imagesAbs = IamgeUtil.getFaceIconPlus(photoUrl);

            // 评分
            String imageUrl = PathUtil.getUrl(imagesAbs[0]);
            FaceScoreImageMod score = pythonApiService.faceScoreIamgeMod(PhotoTypeEnum.IMAGE.getKey(), imageUrl);


            if (score == null || !score.getState()) {
                MessageVO msg = ScoreCopy2MessageVO(score);
                ImportFeatureShow obj11 = new ImportFeatureShow(o.getUserId(), "", o.getType(), PathUtil.getUrl(imagesAbs[1]), "", false,
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
                ImportFeatureShow obj1 = new ImportFeatureShow(o.getUserId(), o.getUserId(), o.getType(), PathUtil.getUrl(imagesAbs[1]), "", false,
                        "特征提取失败");
                res.add(obj1);

            } else {
                String userName = userMap.containsKey(o.getUserId()) ? userMap.get(o.getUserId()).getUserName() : o.getUserId();
                ImportFeatureShow obj1 = new ImportFeatureShow(o.getUserId(), userName, o.getType(), PathUtil.getUrl(imagesAbs[1]), "", true, "");
                res.add(obj1);

                // 4.0 更新特征
                if (!userMap.containsKey(o.getUserId())) {
                    usersAdd.add(new UserInfo(null, o.getUserId(), userName, UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                            PathUtil.getRelPath(imagesAbs[1]), FaceFeatureTypeEnum.OPENVINO.getKey(), new byte[0], PathUtil.getRelPath(featureFileLocal), score.getScore(), DateUtil.date(), DateUtil.date()));
                } else {
                    Example example = new Example(UserInfo.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId", o.getUserId());

                    UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                            PathUtil.getRelPath(imagesAbs[1]), FaceFeatureTypeEnum.OPENVINO.getKey(), null, PathUtil.getRelPath(featureFileLocal), score.getScore(), null, DateUtil.date());
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
     *
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
                String[] imagesAbs = IamgeUtil.getFaceIconPlus(photoUrl);

                // 评分
                String imageUrl = PathUtil.getUrl(imagesAbs[0]);
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
                    ImportFeatureShow obj1 = new ImportFeatureShow(userId, userId, user.getType(), PathUtil.getUrl(imagesAbs[1]), "", false,
                            "特征提取失败");
                    res.add(obj1);

                } else {
                    String userName = userMap.containsKey(userId) ? userMap.get(userId).getUserName() : userId;
                    ImportFeatureShow obj1 = new ImportFeatureShow(userId, userName, user.getType(), imageUrl, "", true, "");
                    res.add(obj1);

                    // 4.0 更新特征
                    if (!mapDB.containsKey(userId)) {
                        usersAdd.add(new UserInfo(null, userId, userName, UserTypeEnum.OTHER.getKey(), 0, "", "", "", "", "", "",
                                PathUtil.getRelPath(imagesAbs[1]), FaceFeatureTypeEnum.OPENVINO.getKey(), new byte[0], PathUtil.getRelPath(featureFileLocal), score.getScore(), DateUtil.date(), DateUtil.date()));
                    } else {
                        Example example = new Example(UserInfo.class);
                        Example.Criteria criteria = example.createCriteria();
                        criteria.andEqualTo("userId", userId);

                        UserInfo userInfo = new UserInfo(null, null, null, null, null, null, null, null, null, null, null,
                                PathUtil.getRelPath(imagesAbs[1]), FaceFeatureTypeEnum.OPENVINO.getKey(), null, PathUtil.getRelPath(featureFileLocal), score.getScore(), null, DateUtil.date());
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

        photo = getPhoto(PhotoTypeEnum.IMAGE.getKey(), PathUtil.getUrl(photo));
        FaceScoreImageMod faceScoreImageMod = pythonApiService.faceScoreIamgeMod(PhotoTypeEnum.IMAGE.getKey(), photo);
        return ScoreCopy2MessageVO(faceScoreImageMod);
    }

    @Override
    public MessageVO startDetectionVideo(String rtspUrl) {

        // 2、检测缓存中是否有已开启的url
        List<String> addrs = ClientManager.getKeyPython();
        if (addrs.isEmpty()) {
            return new MessageVO(false, "计算服务异常，无法获取视频rtmp地址");
        }

        updateDtcVideos(addrs, rtspUrl);
        MessageVO messageVO = new MessageVO(false, "获取视频rtmp地址失败");
        synchronized (dtcVideos) {
            for (int i = 0; i < dtcVideos.size(); i++) {

                // 查询服务器列表，有则直接返回, 没有则先关闭其他的
                String addr = dtcVideos.get(i).getAddr();
                Map<String, String> vidoes = pythonApiService.faceDetectionVideoList(dtcVideos.get(i).getAddr());
                if (vidoes.containsKey(rtspUrl)) {
                    messageVO = new MessageVO(true, vidoes.get(rtspUrl));
                    break;
                } else {
                    vidoes.forEach((k, v) -> {
                        pythonApiService.faceDetectionVideoClose(rtspUrl, addr);
                    });
                }

                // 重新开启新的视频直播流
                Map<String, Boolean> map = pythonApiService.faceDetectionVideoStart(rtspUrl, dtcVideos.get(i).getAddr());
                String rtmpUrl = "";
                Boolean state = false;
                for (String k : map.keySet()) {
                    rtmpUrl = k;
                    state = map.get(k);
                }

                if (state) {
                    //更新dtcVideos中未推流的对象
                    if (dtcVideos.get(i).getVideoUrlRtmp().isEmpty() && dtcVideos.get(i).getVideoUrlRtsp().isEmpty()) {
                        dtcVideos.get(i).setVideoUrlRtsp(rtspUrl);
                        dtcVideos.get(i).setVideoUrlRtmp(rtmpUrl);
                        dtcVideos.get(i).setTime(new Date());
                    }

                    messageVO = new MessageVO(true, rtmpUrl);
                    break;
                }
            }
        }

        return messageVO;
    }

    @Override
    public MessageVO stopDetectionVideo(String rtspUrl) {
        Boolean state = true;

        for (int i = 0; i < dtcVideos.size(); i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    pythonApiService.faceDetectionVideoClose(rtspUrl, dtcVideos.get(index).getAddr());
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }).start();

            dtcVideos.get(index).setVideoUrlRtsp("");
            dtcVideos.get(index).setVideoUrlRtmp("");
            dtcVideos.get(index).setTime(new Date());
        }

        return new MessageVO(state, "");
    }

    @Override
    public MessageVO startDetectionVideoAction(String rtspUrl) {
        // 2、检测缓存中是否有已开启的url
        List<String> addrs = ClientManager.getKeyPython();
        if (addrs.isEmpty()) {
            return new MessageVO(false, "计算服务异常，无法获取视频rtmp地址");
        }

        updateDtcVideosAction(dtcVideosAction, addrs, rtspUrl);
        MessageVO messageVO = new MessageVO(false, "获取视频rtmp地址失败");
        synchronized (dtcVideosAction) {
            for (int i = 0; i < dtcVideosAction.size(); i++) {

                // 查询服务器列表，有则直接返回, 没有则先关闭其他的
                String addr = dtcVideosAction.get(i).getAddr();
                Map<String, String> vidoes = pythonApiService.actionRecognitionVideoGetList(dtcVideosAction.get(i).getAddr());
                if (vidoes.containsKey(rtspUrl)) {
                    messageVO = new MessageVO(true, vidoes.get(rtspUrl));
                    break;
                } else {
                    vidoes.forEach((k, v) -> {
                        pythonApiService.actionRecognitionVideoStop(addr, rtspUrl);
                    });
                }

                // 重新开启新的视频直播流
                Map<String, Boolean> map = pythonApiService.actionRecognitionVideoStart(dtcVideosAction.get(i).getAddr(), rtspUrl);
                String rtmpUrl = "";
                Boolean state = false;
                for (String k : map.keySet()) {
                    rtmpUrl = k;
                    state = map.get(k);
                }

                if (state) {
                    //更新dtcVideosAction中未推流的对象
                    if (dtcVideosAction.get(i).getVideoUrlRtmp().isEmpty() && dtcVideosAction.get(i).getVideoUrlRtsp().isEmpty()) {
                        dtcVideosAction.get(i).setVideoUrlRtsp(rtspUrl);
                        dtcVideosAction.get(i).setVideoUrlRtmp(rtmpUrl);
                        dtcVideosAction.get(i).setTime(new Date());
                    }

                    messageVO = new MessageVO(true, rtmpUrl);
                    break;
                }
            }
        }

        return messageVO;
    }

    @Override
    public MessageVO stopDetectionVideoAction(String rtspUrl) {
        Boolean state = true;

        for (int i = 0; i < dtcVideosAction.size(); i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    pythonApiService.actionRecognitionVideoStop( dtcVideosAction.get(index).getAddr(), rtspUrl);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }).start();

            dtcVideosAction.get(index).setVideoUrlRtsp("");
            dtcVideosAction.get(index).setVideoUrlRtmp("");
            dtcVideosAction.get(index).setTime(new Date());
        }

        return new MessageVO(state, "");
    }

    @Override
    public List<ActionFaceRecognitionImage> faceRecognitionImageAction(Integer type, String photo, String scheduleId, List<String> userIds) {
        // String url = photo;
        List<ActionFaceRecognitionImage> faces = new ArrayList<>();

        String photoUrl = getPhoto(type, photo);
        if (photoUrl.isEmpty()) {
            return new ArrayList<>();
        }

        List<ActionFaceRecognitionImage> list = pythonApiService.actionFaceRecognitionImage(type, photo, scheduleId, userIds);
        list.forEach(o ->
                faces.add(new ActionFaceRecognitionImage(o.getScheduleId(), o.getPhoto_id(), o.getUserId(), o.getAction_label(), o.getPhoto()))
        );
        return faces;

    }


    public MessageVO detectionVideoCloseAll() {

        List<String> addrs = ClientManager.getKeyPython();
        synchronized (dtcVideos) {
            for (String addr : addrs) {
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        //new FaceHttpClient().detectionVideoClose(ip);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }).start();

                dtcVideos.add(new DetectionVideo(addr, "", "", new Date()));
            }
        }

        return new MessageVO(true, "");
    }

    // 已排行遍历的顺序(、rtspUrl相同拍第一, url为空的第二，其余第三)
    public void updateDtcVideos(List<String> addrs, String rtspUrl) {
        synchronized (dtcVideos) {
            List<DetectionVideo> videos = new ArrayList<>();

            //根据addrs更新
            for (String addr : addrs) {
                Boolean state = false;
                for (DetectionVideo o : dtcVideos) {
                    if (addr.equals(o.getAddr())) {
                        state = true;
                        videos.add(new DetectionVideo(o.getAddr(), o.getVideoUrlRtsp(), o.getVideoUrlRtmp(), o.getTime()));
                        break;
                    }
                }

                if (!state) {
                    videos.add(new DetectionVideo(addr, "", "", new Date()));
                }
            }

            // 1、按时间正排序
            Collections.sort(videos, Comparator.comparing(DetectionVideo::getTime));

            // 2、rtspUrl相同排第一, url为空的第二，其余第三
            List<DetectionVideo> list1 = new ArrayList<>();
            List<DetectionVideo> list2 = new ArrayList<>();
            List<DetectionVideo> list3 = new ArrayList<>();
            for (DetectionVideo o : videos) {
                if (o.getVideoUrlRtsp().equals(rtspUrl)) {
                    list1.add(o);
                } else if (o.getVideoUrlRtsp().isEmpty() && o.getVideoUrlRtmp().isEmpty()) {
                    list2.add(o);
                } else {
                    list3.add(o);
                }
            }
            dtcVideos.clear();
            dtcVideos.addAll(list1);
            dtcVideos.addAll(list2);
            dtcVideos.addAll(list3);
        }
    }

    // 人体姿势识别，已排行遍历的顺序(、rtspUrl相同拍第一, url为空的第二，其余第三)
    public void updateDtcVideosAction(List<DetectionVideo> dtcVideosAction, List<String> addrs, String rtspUrl) {
        synchronized (dtcVideosAction) {
            List<DetectionVideo> videos = new ArrayList<>();

            //根据addrs更新
            for (String addr : addrs) {
                Boolean state = false;
                for (DetectionVideo o : dtcVideosAction) {
                    if (addr.equals(o.getAddr())) {
                        state = true;
                        videos.add(new DetectionVideo(o.getAddr(), o.getVideoUrlRtsp(), o.getVideoUrlRtmp(), o.getTime()));
                        break;
                    }
                }

                if (!state) {
                    videos.add(new DetectionVideo(addr, "", "", new Date()));
                }
            }

            // 1、按时间正排序
            Collections.sort(videos, new Comparator<DetectionVideo>() {
                @Override
                public int compare(DetectionVideo a, DetectionVideo b) {
                    return a.getTime().compareTo(b.getTime());
                }
            });

            // 2、rtspUrl相同排第一, url为空的第二，其余第三
            List<DetectionVideo> list1 = new ArrayList<>();
            List<DetectionVideo> list2 = new ArrayList<>();
            List<DetectionVideo> list3 = new ArrayList<>();
            for (DetectionVideo o : videos) {
                if (o.getVideoUrlRtsp().equals(rtspUrl)) {
                    list1.add(o);
                } else if (o.getVideoUrlRtsp().isEmpty() && o.getVideoUrlRtmp().isEmpty()) {
                    list2.add(o);
                } else {
                    list3.add(o);
                }
            }
            dtcVideosAction.clear();
            dtcVideosAction.addAll(list1);
            dtcVideosAction.addAll(list2);
            dtcVideosAction.addAll(list3);
        }
    }

    public String getPhoto(int type, String photo) {
        String url = "";

        // url
        if (type == PhotoTypeEnum.IMAGE.getKey()) {
            try {
                url = URLDecoder.decode(photo, "GBK");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        } else if (type == PhotoTypeEnum.BASE64.getKey()) {
            String path = Properties.SERVER_RESOURCE + Constants.Dir.TEMP + DateTimeUtil.getTimeStamp() + ".jpg";

            String filePath = PathUtil.getAbsPath(path);
            boolean flag = FileUtil.base64ToImage(photo, filePath);
            if (flag) {
                url = PathUtil.getUrl(path);
            }
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
