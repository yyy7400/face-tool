package com.yang.face.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yang.face.client.ClientManager;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.entity.middle.*;
import com.yang.face.service.PythonApiService;
import com.yang.face.service.UserInfoService;
import com.yang.face.util.FileUtil;
import com.yang.face.util.HttpClientUtil;
import com.yang.face.util.PathUtil;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yang
 * Python Web Api 调用
 */
@Service
public class PythonApiServiceImpl implements PythonApiService {

    private final static Logger logger = LoggerFactory.getLogger(PythonApiServiceImpl.class);

    @Resource
    private UserInfoService userInfoService;


    /**
     * 地址轮询位置
     */
    private static int addrPollingIndex = 0;

    public PythonApiServiceImpl() {
        //ClientManager.put("http://192.168.129.134:8001/", ClientTypeEnum.PYTHON.getKey());
    }

    /**
     * 提取特征, tested
     *
     * @param userId
     * @param photoType
     * @param photo
     * @return userId, featureFile
     */
    @Override
    public Map<String, String> getFaceFeature(String userId, Integer photoType, String photo) {


        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_feature");
            JSONObject json = new JSONObject();
            json.put("id", userId);
            json.put("photo_type", photoType);
            json.put("photo", photo);


            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String featureFile = obj.getString("featureFile");
                map.put(id, featureFile);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    /**
     * 单张图片人脸评分
     *
     * @param photoType
     * @param photo
     * @return score, state
     */
    @Override
    @Deprecated
    public Map<Integer, Boolean> faceScoreIamge(Integer photoType, String photo) {

        Map<Integer, Boolean> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_score_image");
            JSONObject json = new JSONObject();
            json.put("photo_type", photoType);
            json.put("photo", photo);

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONObject obj = jsonObject.getJSONObject("data");
            Integer score = obj.getInteger("score");
            Boolean state = obj.getBoolean("state");
            map.put(score, state);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    /**
     * 单张图片人脸评分-带维度, tested
     *
     * @param photoType
     * @param photo
     * @return
     */
    @Override
    public FaceScoreImageMod faceScoreIamgeMod(Integer photoType, String photo) {

        FaceScoreImageMod res = null;
        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_score_image_mod");
            JSONObject json = new JSONObject();
            json.put("photo_type", photoType);
            json.put("photo", photo);

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return null;
            }

            // 解析数据层
            JSONObject obj = jsonObject.getJSONObject("data");
            res = JSONObject.parseObject(obj.toJSONString(), FaceScoreImageMod.class);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return res;
    }

    /**
     * 更新特征库, tested
     *
     * @return
     */
    @Override
    public Boolean updateFaceFeature() {

        try {

            List<String> list = ClientManager.getKeyPython();

            new Thread(() -> {
                for (String addr : list) {
                    // 请求
                    String url = PathUtil.combine(addr, "/face_feature_update");
                    Map<String, Object> paramMap = new ConcurrentHashMap<>();
                    String str = null;
                    try {
                        str = HttpClientUtil.httpPostStr(paramMap, url);
                    } catch (HttpException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 解析首层
                    JSONObject jsonObject = JSONObject.parseObject(str);
                    Integer status = jsonObject.getInteger("status");
                    if (status == 0) {
                        //logger.info("{} 同步成功。", addr);
                    } else {
                        logger.info("{} 同步失败。", addr);
                    }

                    // 解析数据层
                    //state = jsonObject.getJSONObject("data").getBoolean("state");
                }

            }).start();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }

        return true;
    }

    /**
     * 开启视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    @Override
    public Map<String, Boolean> faceDetectionVideoStart(String videoUrl) {

        Map<String, Boolean> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_detection_video_start");
            JSONObject json = new JSONObject();
            json.put("videoUrl", videoUrl);
            json.put("faceServerIp", "");

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Boolean state = jsonData.getBoolean("state");
            String liveUrl = jsonData.getString("videoUrl");
            map.put(liveUrl, state);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    @Override
    public Map<String, Boolean> faceDetectionVideoStart(String rtspUrl, String addr) {

        Map<String, Boolean> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(addr, "/face_detection_video_start");
            JSONObject json = new JSONObject();
            json.put("videoUrl", rtspUrl);
            json.put("faceServerIp", "");

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Boolean state = jsonData.getBoolean("state");
            String liveUrl = jsonData.getString("videoUrl");
            map.put(liveUrl, state);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    /**
     * 关闭视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    @Override
    public Boolean faceDetectionVideoClose(String videoUrl, String addr) {

        try {
            // 请求
            String url = PathUtil.combine(addr, "/face_detection_video_close_all");
            JSONObject json = new JSONObject();

            if (!videoUrl.isEmpty()) {
                url = PathUtil.combine(addr, "/face_detection_video_close");
                json.put("videoUrl", videoUrl);
            }
            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return false;
            }

            // 解析数据层
            JSONObject jsonData = jsonObject.getJSONObject("data");
            return jsonData.getBoolean("state");

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * 获取当前人脸检测视频列表
     *
     * @return videoUrl, rtmpvideoUrl
     */
    @Override
    public Map<String, String> faceDetectionVideoList(String addr) {

        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(addr, "/face_detection_video_get_list");
            JSONObject json = new JSONObject();

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONObject JsonData = jsonObject.getJSONObject("data");
            List<String> videoUrls = JSONArray.parseArray(JsonData.getJSONArray("videoUrl").toJSONString(), String.class);
            List<String> rtmpvideoUrls = JSONArray.parseArray(JsonData.getJSONArray("rtmpvideoUrl").toJSONString(), String.class);
            if (videoUrls.isEmpty() || rtmpvideoUrls.isEmpty() || videoUrls.size() != rtmpvideoUrls.size()) {
                return map;
            }

            for (int i = 0; i < rtmpvideoUrls.size(); i++) {
                map.put(videoUrls.get(i), rtmpvideoUrls.get(i));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    /**
     * 教室内人脸识别-图片,, tested
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @return
     */
    @Override
    public List<FaceRecognitionImage> faceRecognitionImage(Integer photoType, String photo, List<String> userIds) {
        return faceRecognitionImage(photoType, photo, userIds, false);
    }

    /**
     * 单面摄像头，电子班牌上人脸识别,, tested
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @return
     */
    @Override
    public List<FaceRecognitionImage> faceRecognitionImageEC(Integer photoType, String photo, List<String> userIds) {
        return faceRecognitionImage(photoType, photo, userIds, true);
    }

    /**
     * 两种人脸照片对比
     *
     * @param photoType
     * @param photo
     * @param photoType2
     * @param photo2
     * @return similarityScore, state
     */
    @Override
    public Map<Double, Boolean> idCardFaceCmp(Integer photoType, String photo, Integer photoType2, String photo2) {
        Map<Double, Boolean> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_detection_video_close");
            JSONObject json = new JSONObject();
            json.put("type1", photoType);
            json.put("photo1", photo);
            json.put("type2", photoType2);
            json.put("photo2", photo2);

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                Double similarityScore = jsonArray.getJSONObject(i).getDouble("similarityScore");
                Boolean state = jsonArray.getJSONObject(i).getBoolean("state");
                map.put(similarityScore, state);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    /**
     * 清除特征库, tested
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean faceFeatureClean(List<String> ids) {
        List<String> addrs = ClientManager.getKeyPython();
        addrs.forEach((o) -> faceFeatureClean(ids));

        return true;
    }

    /**
     * 多线程调用api
     *
     * @param ids
     * @param addr
     */
    @Async("taskExcutor")
    public void faceFeatureClean(List<String> ids, String addr) {
        try {
            // 请求
            String url;
            JSONObject json = new JSONObject();

            if (ids.isEmpty()) {
                url = PathUtil.combine(addr, "/face_feature_clean");
            } else {
                url = PathUtil.combine(addr, "/face_detection_video_close_all");
                json.put("ids", ids);
            }
            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                System.out.println("success");
            } else {
                System.out.println("failed");
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 通知客户端更新特征
     *
     * @param files
     */
    @Override
    public void noticeDownloadFeature(List<String> files) {

        List<String> addrs = ClientManager.getKeyPython();
        addrs.forEach((o) -> noticeDownloadFeature(files, o));
    }

    /**
     * 多线程调用api
     *
     * @param addr
     * @param files
     */
    @Async("taskExcutor")
    public void noticeDownloadFeature(List<String> files, String addr) {

        try {
            // 请求
            String url = PathUtil.combine(addr, "/face_feature_clean");
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(files);

            String str = HttpClientUtil.httpPostStr(jsonArray.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                System.out.println("success");
            } else {
                System.out.println("failed");
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * tested
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @param ec:       true 调用电子班牌人脸识别接口
     */
    private List<FaceRecognitionImage> faceRecognitionImage(Integer photoType, String photo, List<String> userIds, Boolean ec) {

        List<FaceRecognitionImage> res = new ArrayList<>();
        try {

            List<UserIdFeatureFiles> userInfos = new ArrayList<>();
            for (String userId : userIds) {
                userInfos.add(new UserIdFeatureFiles(userId, ""));
            }

            // 请求
            String url = "";
            if (ec) {
                url = PathUtil.combine(getAddrByPolling(), "/face_recognition_image_ec");
            } else {
                url = PathUtil.combine(getAddrByPolling(), "/face_recognition_image");
            }

            JSONObject json = new JSONObject();
            json.put("photo_type", photoType);
            json.put("photo", photo);
            json.put("userInfos", userInfos);

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return res;
            }

            // 解析数据层
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                FaceRecognitionImage faceRecognitionImage = JSONObject.parseObject(o.toJSONString(), FaceRecognitionImage.class);
                res.add(faceRecognitionImage);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return res;
    }

    @Override
    //@Cacheable(value = "featureFiles")
    public List<FeatureFileInfo> getFeatureFiles() {

        List<FeatureFileInfo> list = new ArrayList<>();

        String dir = Properties.SERVER_RESOURCE + Constants.Dir.FACE_FEATRUE;
        List<File> files = FileUtil.getFilesAll(dir);
        files.forEach((f) -> {
            String[] strs = f.getName().split("\\.");
            // 判断后缀名
            if (strs.length < 2 || !Constants.PYTHON_FEATURE_EXT.equals(strs[1])) {
                return;
            }

            String fileUrl = PathUtil.getUrl(f.getAbsolutePath());
            Date updateTime = DateUtil.date(f.lastModified());
            String md5 = FileUtil.getMD5(f);

            list.add(new FeatureFileInfo(strs[0], fileUrl, String.valueOf(updateTime.getTime()), md5));
        });

        return list;
    }

    @Override
    @CacheEvict(value = "featureFiles", allEntries = true)
    public void clearFeatureFiles() {
    }

    @Override
    public String getAddrByPolling() {

        // 临时测试地址
        String addr = "http://192.168.129.134:5009/";
        List<String> addrs = ClientManager.getKeyPython();
        if (addrs.isEmpty()) {
            return addr;
        }

        if (addrPollingIndex >= addrs.size()) {
            addrPollingIndex = 0;
        }
        addr = addrs.get(addrPollingIndex);
        addrPollingIndex++;

        return addr;
    }


    /**********************************  学情分析相关接口  **********************************/

    @Override
    public Map<String, Boolean> actionRecognitionVideoStart(String addr, String rtspUrl) {
        Map<String, Boolean> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(addr, "/action_recognition_video_start");
            JSONObject json = new JSONObject();
            json.put("videoUrl", rtspUrl);
            json.put("faceServerIp", "");

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Boolean state = jsonData.getBoolean("state");
            String liveUrl = jsonData.getString("videoUrl");
            map.put(liveUrl, state);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    @Override
    public Boolean actionRecognitionVideoStop(String addr, String videoUrl) {
        try {
            if (videoUrl.isEmpty()) {
                return false;
            }

            // 请求
            JSONObject json = new JSONObject();
            String url = PathUtil.combine(addr, "/action_recognition_video_close");
            json.put("videoUrl", videoUrl);
            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return false;
            }

            // 解析数据层
            JSONObject jsonData = jsonObject.getJSONObject("data");
            return jsonData.getBoolean("state");

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public Map<String, String> actionRecognitionVideoGetList(String addr) {
        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            // 请求
            String url = PathUtil.combine(addr, "/action_recognition_video_get_list");
            JSONObject json = new JSONObject();

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return map;
            }

            // 解析数据层
            JSONObject JsonData = jsonObject.getJSONObject("data");
            List<String> videoUrls = JSONArray.parseArray(JsonData.getJSONArray("videoUrl").toJSONString(), String.class);
            List<String> rtmpvideoUrls = JSONArray.parseArray(JsonData.getJSONArray("rtmpvideoUrl").toJSONString(), String.class);
            if (videoUrls.isEmpty() || rtmpvideoUrls.isEmpty() || videoUrls.size() != rtmpvideoUrls.size()) {
                return map;
            }

            for (int i = 0; i < rtmpvideoUrls.size(); i++) {
                map.put(videoUrls.get(i), rtmpvideoUrls.get(i));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return map;

    }

    @Override
    public List<ActionFaceRecognitionImage> actionFaceRecognitionImage(Integer type, String photo, String scheduleId, List<String> userIds) {

        List<ActionFaceRecognitionImage> list = new ArrayList<>();

        try {

            List<UserIdFeatureFiles> userInfos = new ArrayList<>();
            for (String userId : userIds) {
                userInfos.add(new UserIdFeatureFiles(userId, ""));
            }

            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/action_face_recognition_image");
            JSONObject json = new JSONObject();
            json.put("type", type);
            json.put("photo", photo);
            json.put("scheduleId", scheduleId);
            json.put("userInfos", userInfos);

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return list;
            }

            // 解析数据层
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                String sId = o.getString("scheduleId");
                String pId = o.getString("photo_id");
                List<String> uIds = o.getJSONArray("userId").toJavaList(String.class);
                List<String> ls = o.getJSONArray("action_label").toJavaList(String.class);
                String p = o.getString("photo");

                list.add(new ActionFaceRecognitionImage(sId, pId, uIds, ls, p));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return list;
    }

}
