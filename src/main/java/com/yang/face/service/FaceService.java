package com.yang.face.service;

import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatureShow;
import com.yang.face.entity.show.MessageVO;

import java.util.List;

/**
 * @author yangyuyang
 */
public interface FaceService {

    /**
     * 人脸识别类型，Arc、OpenVINO
     * @return
     */
    Integer faceType();

    /**
     * 教室内远距离人脸识别场景，无感考勤专用
     * @param type
     * @param photo
     * @param userIds
     * @return
     */
    List<FaceRecoShow> recoImageRoom(Integer type, String photo, List<String> userIds);

    /**
     * 进距离人脸识别场景，电子班牌，门禁专用
     *
     * @param type
     * @param photo
     * @param userIds
     * @return
     */
    List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds);

    /**
     * 清理人脸库特征, 会删除数据库数据
     *
     * @param userIds
     * @return
     */
    MessageVO cleanFeature(List<String> userIds);

    /**
     * 清理人脸库特征, 更新数据库数据
     *
     * @param userIds
     * @return
     */
    MessageVO cleanFeatureUpdate(List<String> userIds);

    /**
     * 批量导入人脸库, 不更新
     *
     * @param list
     * @return
     */
    List<ImportFeatureShow> importFeaturesNoUpadte(List<ImportFeaturePost> list);

    /**
     * 批量导入人脸库，并更新
     *
     * @param list
     * @return
     */
    List<ImportFeatureShow> importFeatures(List<ImportFeaturePost> list);

    /**
     * 批量导入人脸库，并更新
     *
     * @param zipPath
     * @return
     */
    List<ImportFeatureShow> importFeatures(String zipPath);

    /**
     * 更新人脸库特征
     *
     * @return
     */
    MessageVO updateFeatures();

    /**
     * 获取图片人脸分数
     * @param photo
     * @return
     */
    MessageVO getPhotoScore(String photo);

    /**
     * 开启人脸检测直播视频流
     * @param url
     * @return
     */
    MessageVO startDetectionVideo(String url);

    /**
     * 关闭人脸检测直播视频流
     * @param url
     * @return
     */
    MessageVO stopDetectionVideo(String url);



    /**
     * 判断支持的图片格式
     * @param ext
     * @return
     */
    static Boolean supportImage(String ext) {
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
