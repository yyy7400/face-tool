package com.yang.face.controller;

import com.yang.face.entity.show.Response;
import com.yang.face.service.UserInfoService;
import com.yang.face.service.yun.SchoolInfoStructUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Yang
 * @date 2020-03-07
 * 人脸库接口
 */

@RestController
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 分页查找用户信息
     * @param groupId
     * @param groupId
     * @param userName
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/userInfo/search")
    public Response search( String groupId, Integer userType, String userName, Integer pageIndex, Integer pageSize) {
        return Response.show(userInfoService.search(groupId, userType, userName, pageIndex, pageSize));
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/userInfo/getById")
    public Response getById(Integer id) {
        return Response.show(userInfoService.selectById(id));
    }

    /**
     * 根据userId查询
     * @param userId
     * @return
     */
    @GetMapping("/userInfo/getByUserId")
    public Response getByUserId(String userId) {
        return Response.show(userInfoService.selectByUserId(userId));
    }

    /**
     * 删除个人照片
     * @param userId
     * @return
     */
    @DeleteMapping("/userInfo/delPhoto")
    public Response delPhoto(String userId) {
        return Response.show(userInfoService.deletePhoto(userId));
    }

    /**
     * 更新人脸特征
     * @param userId
     * @param photo
     * @return
     */
    @PutMapping("/userInfo/updatePhoto2")
    public Response updatePhoto2(String userId, String photo) {
        return Response.show(userInfoService.updatePhoto(userId, photo));
    }

    /**
     * 批量更新人脸特征
     * @return
     */
    @GetMapping("/userInfo/importFeature")
    public Response importFeature(String zipPath) {
        return null;
    }

    /*
     *重置人脸特征
     * */
    @GetMapping("/userInfo/resetLibrary")
    public Response resetLibrary(String token) {
        return Response.show(userInfoService.resetUserInfo());
    }

    /*
     * 从基础云平台下载个人图片
     * */
    @GetMapping("/studentFace/getPhotoFromYun")
    public Response getPhotoFromYun(String token, String userId, Integer userType) {
        return Response.show(userInfoService.getPhotoFromYun(token, userId, userType));
    }

    /*
     * 从基础云平台得到所有学校信息
     * */
    @GetMapping("/userInfo/getSchoolInfo")
    public Response getSchoolInfo() {
        return Response.show(new SchoolInfoStructUtil().getSchoolInfo());
    }

    /*
     * 从基础云平台得到所有年级信息
     * */
    @GetMapping("/userInfo/getGradeInfo")
    public Response getGradeInfo(String token) {
        return Response.show(new SchoolInfoStructUtil().getGradeInfo(token));
    }

    /*
     * 从基础云平台得到所有班级信息
     * */
    @GetMapping("/userInfo/getClassInfo")
    public Response getClassInfo(String token) {
        userInfoService.updateYunUserInfo();
        return Response.show(new SchoolInfoStructUtil().getClassInfo(token));
    }

    /*
     * 从基础云平台得到教师分组信息
     * */
    @GetMapping("/userInfo/getGroupInfo")
    public Response getGroupInfo(String token) {
        return Response.show(userInfoService.getGroupInfo(token));
    }

}
