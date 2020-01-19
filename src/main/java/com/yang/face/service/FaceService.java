package com.yang.face.service;

import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatrueShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.service.impl.FaceServiceImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author yangyuyang
 */
public interface FaceService {

    /**
     * 人脸识别
     * @param type
     * @param photo
     * @param userIds
     * @return
     */
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds);

    /**
     * 清理人脸库特征
     * @param userId
     * @return
     */
    public MessageVO cleanFeature(List<String> userId);

    /**
     * 批量导入人脸库，并更新
     * @param list
     * @return
     */
    public List<ImportFeatrueShow> importFeatures(List<ImportFeaturePost> list);

    /**
     * 更新人脸库特征
     * @return
     */
    public MessageVO updateFeatures();

}
