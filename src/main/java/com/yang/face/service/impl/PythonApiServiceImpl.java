package com.yang.face.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yang.face.client.ClientManager;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.ClientTypeEnum;
import com.yang.face.entity.middle.FaceRecognitionImage;
import com.yang.face.entity.middle.FaceScoreImageMod;
import com.yang.face.entity.middle.FeatureFileInfo;
import com.yang.face.entity.middle.UserIdFeatureFiles;
import com.yang.face.service.PythonApiService;
import com.yang.face.service.UserInfoService;
import com.yang.face.util.FileUtil;
import com.yang.face.util.HttpClientUtil;
import com.yang.face.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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
    private static volatile int addrPollingIndex = 0;

    public PythonApiServiceImpl() {
        ClientManager.put("http://192.168.129.134:8001/", ClientTypeEnum.PYTHON.getKey());
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
     * 单张图片人脸评分-带维度
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

        Boolean state = false;

        try {

            List<String> list = ClientManager.getKeyPython();

            for (String addr : list) {
                // 请求
                String url = PathUtil.combine(addr, "/face_feature_update");
                Map<String, Object> paramMap = new ConcurrentHashMap<>();
                String str = HttpClientUtil.httpPostStr(paramMap, url);

                // 解析首层
                JSONObject jsonObject = JSONObject.parseObject(str);
                Integer status = jsonObject.getInteger("status");
                if (status != 0) {
                    return state;
                }

                // 解析数据层
                state = jsonObject.getJSONObject("data").getBoolean("state");
            }


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
            String url = PathUtil.combine(getAddrByPolling(), "/face_detection_video_close");
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

    /**
     * 关闭视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    @Override
    public Boolean faceDetectionVideoClose(String videoUrl) {

        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_detection_video_close_all");
            JSONObject json = new JSONObject();

            if (!videoUrl.isEmpty()) {
                url = PathUtil.combine(getAddrByPolling(), "/face_detection_video_start");
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
     * @return
     */
    @Override
    public List<String> faceDetectionVideoList() {

        List<String> list = new ArrayList<>();

        try {
            // 请求
            String url = PathUtil.combine(getAddrByPolling(), "/face_detection_video_close");
            JSONObject json = new JSONObject();

            String str = HttpClientUtil.httpPostStr(json.toJSONString(), url);

            // 解析首层
            JSONObject jsonObject = JSONObject.parseObject(str);
            Integer status = jsonObject.getInteger("status");
            if (status != 0) {
                return list;
            }

            // 解析数据层
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            list.addAll(JSONArray.parseArray(jsonArray.toJSONString(), String.class));

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return list;
    }

    /**
     * 教室内人脸识别-图片
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
     * 单面摄像头，电子班牌上人脸识别
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
     * 清除特征库
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean faceFeatureClean(List<String> ids) {
        List<String> addrs = ClientManager.getKeyPython();
        addrs.forEach((o) -> noticeDownloadFeature(ids, o));
        return true;
    }

    /**
     * 多线程调用api
     *
     * @param ids
     * @param addr
     * @return
     */
    @Async("taskExcutor")
    public void faceFeatureClean(List<String> ids, String addr) {
        try {
            // 请求
            String url = "";
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
            ;
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
     * @param photoType
     * @param photo
     * @param userIds
     * @param ec:       true 调用电子班牌人脸识别接口
     * @return
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
                PathUtil.combine(getAddrByPolling(), "/face_recognition_image");
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
    public List<FeatureFileInfo> getFeatureFiles() {

        List<FeatureFileInfo> list = new ArrayList<>();

        String dir = Properties.SERVER_RESOURCE + Constants.Dir.FACE_FEATRUE;
        List<File> files = FileUtil.getFilesAll(dir);
        files.forEach((f) -> {
            String[] strs = f.getName().split(".");
            // 判断后缀名
            if(strs.length < 2 || !Constants.PYTHON_FEATURE_EXT.equals(strs[1])) {
                return;
            }

            String fileUrl = PathUtil.getRelPath(f.getAbsolutePath());
            Date updateTime = DateUtil.date(f.lastModified());
            String md5 = FileUtil.getMD5(f);

            list.add(new FeatureFileInfo(strs[0], fileUrl, updateTime, md5));
        });

        return list;
    }

    @Override
    public String getAddrByPolling() {

        String addr = "";
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

}
