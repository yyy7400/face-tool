package com.yang.face.service;

import com.thoughtworks.xstream.core.ReferenceByIdUnmarshaller;
import com.yang.face.entity.middle.ActionFaceRecognitionImage;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatureShow;
import com.yang.face.entity.show.MessageVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略这模式
 *
 * @author yangyuyang
 * @date 2020/3/16 11:23
 */
@Service
public class FaceStrageService {

    private Map<Integer, FaceService> map = new HashMap<>();

    @Resource
    SystemSettingService systemSettingService;


    /**
     * 构造函数，如果是集合接口对象，那么就会把spring容器中所有关于该接口的子类加到map中
     *
     * @param list
     */
    public FaceStrageService(List<FaceService> list) {

        list.forEach(o ->
            map.put(o.faceType(), o)
        );
    }

    private Integer getFaceTypeDb() {
        return systemSettingService.selectOne().getFaceType();
    }

    /**
     * 人脸识别
     *
     * @param type
     * @param photo
     * @param userIds
     * @return
     */
    public List<FaceRecoShow> recoImageRoom(Integer type, String photo, List<String> userIds) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.recoImageRoom(type, photo, userIds);
    }

    /**
     * 人脸识别
     *
     * @param type
     * @param photo
     * @param userIds
     * @return
     */
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.recoImage(type, photo, userIds);
    }

    /**
     * 清理人脸库特征
     *
     * @param userId
     * @return
     */
    public MessageVO cleanFeature(List<String> userId) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.cleanFeature(userId);
    }

    /**
     * 清理人脸库特征,只更新
     *
     * @param userId，size > 0
     * @return
     */
    public MessageVO cleanFeatureUpdate(List<String> userId) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.cleanFeatureUpdate(userId);
    }

    /**
     * 批量导入人脸库，不更新
     *
     * @param list
     * @return
     */
    public List<ImportFeatureShow> importFeaturesNoUpdate(List<ImportFeaturePost> list) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.importFeaturesNoUpadte(list);
    }

    /**
     * 批量导入人脸库，并更新
     *
     * @param list
     * @return
     */
    public List<ImportFeatureShow> importFeatures(List<ImportFeaturePost> list) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.importFeatures(list);
    }

    /**
     * 批量导入人脸库，并更新
     *
     * @param zipPath
     * @return
     */
    public List<ImportFeatureShow> importFeatures(String zipPath) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.importFeatures(zipPath);
    }

    /**
     * 更新人脸库特征
     *
     * @return
     */
    public MessageVO updateFeatures() {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.updateFeatures();
    }

    /**
     * 人脸评分，不持之base64格式
     *
     * @return
     */
    public MessageVO getPhotoScore(String photo) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.getPhotoScore(photo);
    }

    public MessageVO startDetectionVideo(String url) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.startDetectionVideo(url);
    }

    public MessageVO stopDetectionVideo(String url) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.stopDetectionVideo(url);
    }

    public MessageVO startDetectionVideoAction(String url) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.startDetectionVideoAction(url);
    }

    public MessageVO stopDetectionVideoAction(String url) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.stopDetectionVideoAction(url);
    }

    public List<ActionFaceRecognitionImage> faceRecognitionImageAction(Integer type, String photo, String scheduleId, List<String> userIds) {
        FaceService faceService = map.get(getFaceTypeDb());
        return faceService.faceRecognitionImageAction(type, photo, scheduleId, userIds);
    }
}
