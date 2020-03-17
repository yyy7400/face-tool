package com.yang.face.controller;

import com.yang.face.entity.show.Response;
import com.yang.face.service.UserInfoService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author yangyuyang
 * @date 2020/3/17 11:50
 */
@RestController
public class PublicController {


    @Resource
    UserInfoService userInfoService;

    /***** 智能门禁 ***/

    //获取学生信息
    @RequestMapping(value = "/pub/getStudentFeature", method = RequestMethod.GET)
    public Response getStudentFeature(Boolean hasFeature, Date photoTime) {
        return Response.show(userInfoService.getStudentFeature(hasFeature, photoTime));
    }

    //获取教师信息
    @RequestMapping(value = "/pub/getTeacherFeature", method = RequestMethod.GET)
    public Response getTeacherFeature(Boolean hasFeature, Date photoTime) {
        return Response.show(userInfoService.getTeacherFeature(hasFeature, photoTime));
    }

    //获取管理员信息
    @RequestMapping(value = "/pub/getAdminFeature", method = RequestMethod.GET)
    public Response getAdminFeature(Boolean hasFeature, Date photoTime) {
        return Response.show(userInfoService.getAdminFeature(hasFeature, photoTime));
    }

    //获取其他角色信息
    @RequestMapping(value = "/pub/getAdminFeature", method = RequestMethod.GET)
    public Response getOtherFeature(Boolean hasFeature, Date photoTime) {
        return Response.show(userInfoService.getOtherFeature(hasFeature, photoTime));
    }

    //根据用户id获取人脸图片
    @RequestMapping(value = "/pub/getPhotoByUserIds", method = RequestMethod.POST)
    public Response getPhotoByUserIds(@RequestBody List<String> userIds) {
        return Response.show(userInfoService.getPhotoByUserIds(userIds));
    }


    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
