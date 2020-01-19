package com.yang.face.service.impl;

import cn.hutool.core.codec.Base64;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.yang.face.constant.enums.PhotoType;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatrueShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.FaceEngineService;
import com.yang.face.service.FaceService;
import com.yang.face.util.Base64Util;
import com.yang.face.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangyuyang
 */
public class FaceServiceImpl implements FaceService {

    private static final Logger logger = LoggerFactory.getLogger(FaceServiceImpl.class);

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private FaceEngineService faceEngineService;

    /**
     * 人脸识别, userIds.isEmpty() 时对比全部
     *
     * @param type
     * @param photo   目前只支持base64，url地址需要下载
     * @param userIds
     * @return
     */
    @Override
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {

        List<FaceRecoShow> list = new ArrayList<>();
        try {
            // 1. 图片转转byte
            byte[] photoBytes = getPhotoByte(type, photo);
            ImageInfo imageInfo = ImageFactory.getRGBData(photoBytes);

            // 2. 人脸特征获取
            byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
            if (bytes == null) {
                return list;
            }

            // 3. 从数据库中取出人脸库
            List<UserInfo> userInfoList = new ArrayList<>();
            if (userIds.isEmpty()) {
                userInfoList = userInfoMapper.selectAll();
            } else {
                Example example = new Example(UserInfo.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("userId", userIds);
                userInfoList = userInfoMapper.selectByExample(example);
            }
            if (userInfoList.isEmpty())
                return list;

            // 4. 识别到的人脸列表
            List<FaceUserInfo> faces = faceEngineService.compareFaceFeature(bytes, userInfoList);

            for (FaceUserInfo o : faces) {
                list.add(new FaceRecoShow(o.getUserId(), o.getSimilarityScore(), o.getPhotoUrl()));
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return list;
    }

    /**
     * 清理人脸库特征
     *
     * @param userIds
     * @return
     */
    @Override
    public MessageVO cleanFeature(List<String> userIds) {

        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria();

            if(userIds.isEmpty()) {
                userInfoMapper.deleteByExample(example);
            } else {
                criteria.andIn("userId", userIds);
                userInfoMapper.deleteByExample(example);
            }
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 批量导入人脸库，并更新
     *
     * @param list
     * @return
     */
    @Override
    public List<ImportFeatrueShow> importFeatures(List<ImportFeaturePost> list) {
        return null;
    }

    /**
     * 更新人脸库特征
     *
     * @return
     */
    @Override
    public MessageVO updateFeatures() {
        return null;
    }

    public byte[] getPhotoByte(int type, String photo) throws IOException {
        // url
        if (type == PhotoType.IMAGE.getKey())
            return NetUtil.image2byte(photo);
        else if (type == PhotoType.BASE64.getKey()) {
            return Base64.decode(Base64Util.base64Process(photo));
        }

        return new byte[0];
    }
}
