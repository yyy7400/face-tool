package com.yang.face.controller;

import com.yang.face.entity.post.UserIdAndPhotoPost;
import com.yang.face.entity.post.UserIdAndTypePost;
import com.yang.face.entity.show.Response;
import com.yang.face.service.FaceStrageService;
import com.yang.face.service.UserInfoService;
import com.yang.face.service.yun.SchoolInfoStructUtil;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private FaceStrageService faceStrageService;

    /**
     * 分页查找用户信息
     * @param groupId
     * @param userName
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/userInfo/search")
    public Response search( String groupId, String gradeId, String classId, Integer userType, String userName, Integer pageIndex, Integer pageSize) {
        return Response.show(userInfoService.search(groupId, gradeId, classId, userType, userName, pageIndex, pageSize));
    }

    /**
     * 根据id查询, tested
     * @param id
     * @return
     */
    @GetMapping("/userInfo/getById")
    public Response getById(Integer id) {
        return Response.show(userInfoService.selectById(id));
    }

    /**
     * 根据userId查询, tested
     * @param userId
     * @return
     */
    @GetMapping("/userInfo/getByUserId")
    public Response getByUserId(String userId) {
        return Response.show(userInfoService.selectByUserId(userId));
    }

    /**
     * 删除个人照片, tested
     * @param userId
     * @return
     */
    @GetMapping("/userInfo/delPhoto")
    public Response delPhoto(String userId) {
        return Response.show(userInfoService.deletePhoto(userId));
    }

    /**
     * 删除个人信息, 两个入参不能同时为空, tested
     * userId "" 为全部
     * userType -1 为全部
     * @return
     */
    @PostMapping("/userInfo/delUser")
    public Response delUser(@RequestBody UserIdAndTypePost o) {
        return Response.show(userInfoService.deleteUser(o.getUserType(), o.getUserId()));
    }

    /**
     * 更新人脸特征, tested
     * @return
     */
    @PostMapping("/userInfo/updatePhoto2")
    public Response updatePhoto2(@RequestBody UserIdAndPhotoPost o) {
        return Response.show(userInfoService.updatePhoto(o.getUserId(), o.getPhoto()));
    }

    /**
     * 检测人脸分数, tested
     * @param photo
     * @return
     */
    @GetMapping("/userInfo/getPhotoScore")
    public Response getPhotoScore(String photo) {
        return Response.show(faceStrageService.getPhotoScore(photo));
    }

    /**
     * 批量更新人脸特征, tested
     * @return
     */
    @GetMapping("/userInfo/importFeature")
    public Response importFeature(String zipPath) {
        return Response.show(faceStrageService.importFeatures(zipPath));
    }

    /*
     *重置人脸特征, tested
     * */
    @GetMapping("/userInfo/resetLibrary")
    public Response resetLibrary(String token) {
        return Response.show(userInfoService.resetUserInfo());
    }

    /**
     * 从基础云平台下载个人图片, tested
     */
    @GetMapping("/userInfo/getPhotoFromYun")
    public Response getPhotoFromYun(String token, String userId, Integer userType) {
        return Response.show(userInfoService.getPhotoFromYun(token, userId, userType));
    }

    /**
     * 从基础云平台得到所有学校信息, tested
     */
    @GetMapping("/userInfo/getSchoolInfo")
    public Response getSchoolInfo() {
        return Response.show(new SchoolInfoStructUtil().getSchoolInfo());
    }

    /**
     * 从基础云平台得到所有年级信息, tested
     */
    @GetMapping("/userInfo/getGradeInfo")
    public Response getGradeInfo(String token) {
        return Response.show(new SchoolInfoStructUtil().getGradeInfo(token));
    }

    /**
     * 从基础云平台得到所有班级信息, tested
     */
    @GetMapping("/userInfo/getClassInfo")
    public Response getClassInfo(String token) {
        userInfoService.updateYunUserInfo();
        return Response.show(new SchoolInfoStructUtil().getClassInfo(token));
    }

    /**
     * 从基础云平台得到教师分组信息, tested
     */
    @GetMapping("/userInfo/getGroupInfo")
    public Response getGroupInfo(String token) {
        return Response.show(userInfoService.getGroupInfo(token));
    }

}
