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
     * 人脸识别
     *
     * @param type
     * @param photo
     * @param userIds
     * @return
     */
    List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds);

    /**
     * 清理人脸库特征
     *
     * @param userId
     * @return
     */
    MessageVO cleanFeature(List<String> userId);

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

}
