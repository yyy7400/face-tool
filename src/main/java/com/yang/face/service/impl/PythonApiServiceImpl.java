package com.yang.face.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.yang.face.client.ClientManager;
import com.yang.face.constant.enums.ClientTypeEnum;
import com.yang.face.constant.enums.PhotoType;
import com.yang.face.entity.middle.FaceScoreImageMod;
import com.yang.face.entity.middle.PythonResponse;
import com.yang.face.service.PythonApiService;
import com.yang.face.util.HttpClientUtil;
import com.yang.face.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Python Web Api 调用
 */
public class PythonApiServiceImpl implements PythonApiService {

    private final static Logger logger = LoggerFactory.getLogger(PythonApiServiceImpl.class);

    /**
     * 地址轮询位置
     */
    private static volatile int addrPollingIndex = 0;

    public PythonApiServiceImpl(){
        ClientManager.put("http://192.168.129.134:8001/", ClientTypeEnum.PYTHON.getKey());
    }

    /**
     * 提取特征
     * @param userId
     * @param photoType
     * @param photo
     * @return userId, featureFile
     */
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
            if(status != 0) {
                return map;
            }

            // 解析数据层
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String featureFile = obj.getString("featureFile");
                map.put(id,featureFile);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    /**
     *  单张图片人脸评分
     * @param photoType
     * @param photo
     * @return score, state
     */
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
            if(status != 0) {
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
     * @param photoType
     * @param photo
     * @return
     */
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
            if(status != 0) {
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
     * 更新特征库
     * @return
     */
    public Boolean updateFaceFeature() {

        Boolean state = false;

        try {

            List<String> list = ClientManager.getKeyPython();

            for(String addr : list) {
                // 请求
                String url = PathUtil.combine(addr, "/face_feature_update");
                Map<String, Object> paramMap = new ConcurrentHashMap<>();
                String str = HttpClientUtil.httpPostStr(paramMap, url);

                // 解析首层
                JSONObject jsonObject = JSONObject.parseObject(str);
                Integer status = jsonObject.getInteger("status");
                if(status != 0) {
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





    @Override
    public String getAddrByPolling() {

        String addr = "";
        List<String> addrs = ClientManager.getKeyPython();
        if(addrs.isEmpty()) {
            return addr;
        }

        if(addrPollingIndex >= addrs.size()) {
            addrPollingIndex = 0;
        }
        addr = addrs.get(addrPollingIndex);
        addrPollingIndex ++;

        return addr;
    }
}
