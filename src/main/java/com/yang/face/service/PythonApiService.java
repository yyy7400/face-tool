package com.yang.face.service;

import com.yang.face.entity.middle.FaceRecognitionImage;
import com.yang.face.entity.middle.FaceScoreImageMod;
import com.yang.face.entity.middle.FeatureFileInfo;

import java.util.List;
import java.util.Map;

public interface PythonApiService {

    /**
     * 提取特征, tested
     *
     * @param userId
     * @param photoType
     * @param photo
     * @return userId, featureFile
     */
    public Map<String, String> getFaceFeature(String userId, Integer photoType, String photo);

    /**
     * 单张图片人脸评分
     *
     * @param photoType
     * @param photo
     * @return score, state
     */
    public Map<Integer, Boolean> faceScoreIamge(Integer photoType, String photo);

    /**
     * 单张图片人脸评分-带维度
     *
     * @param photoType
     * @param photo
     * @return
     */
    public FaceScoreImageMod faceScoreIamgeMod(Integer photoType, String photo);

    /**
     * 更新特征库, tested
     *
     * @return
     */
    public Boolean updateFaceFeature();

    /**
     * 开启视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    public Map<String, Boolean> faceDetectionVideoStart(String videoUrl);

    /**
     * 关闭视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    public Boolean faceDetectionVideoClose(String videoUrl);

    /**
     * 获取当前人脸检测视频列表
     *
     * @return
     */
    public List<String> faceDetectionVideoList();

    /**
     * 教室内人脸识别-图片
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @return
     */
    public List<FaceRecognitionImage> faceRecognitionImage(Integer photoType, String photo, List<String> userIds);

    /**
     * 单面摄像头，电子班牌上人脸识别
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @return
     */
    public List<FaceRecognitionImage> faceRecognitionImageEC(Integer photoType, String photo, List<String> userIds);

    /**
     * 两种人脸照片对比
     * @param photoType
     * @param photo
     * @param photoType2
     * @param photo2
     * @return similarityScore, state
     */
    public Map<Double, Boolean> idCardFaceCmp(Integer photoType, String photo, Integer photoType2, String photo2);

    /**
     * 清除特征库
     *
     * @param ids
     * @return
     */
    public Boolean faceFeatureClean(List<String> ids);

    /**
     * 通知客户端更新特征
     *
     * @param files
     */
    public void noticeDownloadFeature(List<String> files);

    /**
     * 获取特征文件
     * @return
     */
    public List<FeatureFileInfo> getFeatureFiles();

    public String getAddrByPolling();
}
