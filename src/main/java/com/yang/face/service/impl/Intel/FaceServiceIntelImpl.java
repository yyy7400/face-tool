package com.yang.face.service.impl.Intel;

import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.midle.ByteFile;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.UserInfoService;
import com.yang.face.service.impl.FaceEngineServiceImpl;
import com.yang.face.util.PathUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class FaceServiceIntelImpl extends FaceEngineServiceImpl {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 人脸识别, userIds.isEmpty() 时对比全部
     *
     * @param type
     * @param photo   目前只支持base64，url地址需要下载
     * @param userIds
     * @return
     */
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {

        List<FaceRecoShow> list = new ArrayList<>();

        return list;
    }
}
