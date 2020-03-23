package com.yang.face.service;

import com.yang.face.entity.middle.ActionFaceRecognitionImage;
import com.yang.face.entity.middle.FaceRecognitionImage;
import com.yang.face.entity.middle.FaceScoreImageMod;
import com.yang.face.entity.middle.FeatureFileInfo;

import java.util.List;
import java.util.Map;

/**
 * Intel OpenVINO 人脸识别
 * @author Yang
 */
public interface PythonApiService {

    /**
     * 提取特征, tested
     *
     * @param userId
     * @param photoType
     * @param photo
     * @return userId, featureFile
     */
    Map<String, String> getFaceFeature(String userId, Integer photoType, String photo);

    /**
     * 单张图片人脸评分
     *
     * @param photoType
     * @param photo
     * @return score, state
     */
    Map<Integer, Boolean> faceScoreIamge(Integer photoType, String photo);

    /**
     * 单张图片人脸评分-带维度
     *
     * @param photoType
     * @param photo
     * @return
     */
    FaceScoreImageMod faceScoreIamgeMod(Integer photoType, String photo);

    /**
     * 更新特征库, tested
     *
     * @return
     */
    Boolean updateFaceFeature();

    /**
     * 开启视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    Map<String, Boolean> faceDetectionVideoStart(String videoUrl);

    /**
     * 开启视频流检测
     *
     * @param videoUrl
     * @param addr
     * @return liveUrl, state
     */
    Map<String, Boolean> faceDetectionVideoStart(String videoUrl, String addr);

    /**
     * 关闭视频流检测
     *
     * @param videoUrl
     * @return liveUrl, state
     */
    Boolean faceDetectionVideoClose(String videoUrl, String addr);

    /**
     * 获取当前人脸检测视频列表
     *
     * @return
     */
    Map<String, String> faceDetectionVideoList(String addr);

    /**
     * 教室内人脸识别-图片
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @return
     */
    List<FaceRecognitionImage> faceRecognitionImage(Integer photoType, String photo, List<String> userIds);

    /**
     * 单面摄像头，电子班牌上人脸识别
     *
     * @param photoType
     * @param photo
     * @param userIds
     * @return
     */
    List<FaceRecognitionImage> faceRecognitionImageEC(Integer photoType, String photo, List<String> userIds);

    /**
     * 两种人脸照片对比
     * @param photoType
     * @param photo
     * @param photoType2
     * @param photo2
     * @return similarityScore, state
     */
    Map<Double, Boolean> idCardFaceCmp(Integer photoType, String photo, Integer photoType2, String photo2);

    /**
     * 清除特征库
     *
     * @param ids
     * @return
     */
    Boolean faceFeatureClean(List<String> ids);

    /**
     * 通知客户端更新特征
     *
     * @param files
     */
    @Deprecated
    void noticeDownloadFeature(List<String> files);

    /**
     * 获取特征文件
     * @return
     */
    List<FeatureFileInfo> getFeatureFiles();

    /**
     * 清理特征文件缓存
     * @return
     */
    void clearFeatureFiles();

    /**
     * 轮询获取Python端地址
     * @return
     */
    String getAddrByPolling();


    /**********************************  学情分析相关接口  **********************************/
    /**
     * 启动人体动作识别视频流
     * @param addr
     * @param rtspUrl
     * @return
     */
    Map<String, Boolean> actionRecognitionVideoStart(String addr, String rtspUrl);

    /**
     * 关闭人体动作识别视频流
     * @param addr
     * @return
     */
    Boolean actionRecognitionVideoStop(String addr, String videoUrl);

    /**
     * 获取人体动作识别视频流列表
     * @param addr
     * @return
     */
    Map<String, String> actionRecognitionVideoGetList(String addr);

    /**
     * 人体动作识别与人脸识别的数据融合-图片
     * 识别照片中的人体动作，融合人脸ID，最终给出返回结果。
     * @return
     */
    List<ActionFaceRecognitionImage> actionFaceRecognitionImage(Integer type, String photo, String scheduleId, List<String> userIds);

}
