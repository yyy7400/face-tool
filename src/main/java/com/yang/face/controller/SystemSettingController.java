package com.yang.face.controller;

import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.entity.db.SystemSetting;
import com.yang.face.entity.show.Response;
import com.yang.face.service.FaceStrageService;
import com.yang.face.service.SystemSettingService;
import com.yang.face.service.UserInfoService;
import com.yang.face.service.impl.SystemSettingServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author yangyuyang
 * @date 2020/3/16 9:22
 */
@RestController
public class SystemSettingController {

    @Resource
    SystemSettingService systemSettingService;

    @Resource
    private FaceStrageService faceStrageService;

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/systemSetting/get")
    public Response get() {
        return Response.show(systemSettingService.selectOne());
    }

    @GetMapping("/systemSetting/changeArc")
    public Response changeArc() {

        faceStrageService.cleanFeature(new ArrayList<>());
        userInfoService.updateYunUserInfo();

        SystemSetting systemSetting = new SystemSetting();
        systemSetting.setFaceType(FaceFeatureTypeEnum.ARC_SOFT.getKey());
        return Response.show(systemSettingService.update(systemSetting));
    }

    @GetMapping("/systemSetting/changeOpenVINO")
    public Response changeOpenVINO() {

        faceStrageService.cleanFeature(new ArrayList<>());
        userInfoService.updateYunUserInfo();

        SystemSetting systemSetting = new SystemSetting();
        systemSetting.setFaceType(FaceFeatureTypeEnum.OPENVINO.getKey());
        return Response.show(systemSettingService.update(systemSetting));
    }
}
