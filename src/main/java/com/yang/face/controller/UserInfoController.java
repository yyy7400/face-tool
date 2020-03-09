package com.yang.face.controller;

import com.yang.face.entity.show.Response;
import com.yang.face.service.UserInfoService;
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
     * @param schoolId
     * @param groupId
     * @param userName
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/userInfo/search")
    public Response search(String schoolId, String groupId, String userName, Integer pageIndex, Integer pageSize) {
        return null;
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/userInfo/getById")
    public Response getById(Integer id) {
        return Response.show("");
    }

    /**
     * 根据userId查询
     * @param userId
     * @return
     */
    @GetMapping("/userInfo/getByUserId")
    public Response getByUserId(Integer userId) {
        return null;
    }

    /**
     * 删除个人照片
     * @param userId
     * @return
     */
    @DeleteMapping("/userInfo/delPhoto")
    public Response delPhoto(Integer userId) {
        return null;
    }

    /**
     * 更新人脸特征
     * @param userId
     * @param photo
     * @return
     */
    @PutMapping("/userInfo/updatePhoto2")
    public Response updatePhoto2(Integer userId, String photo) {
        return null;
    }

    /**
     * 批量更新人脸特征
     * @return
     */
    @GetMapping("/userInfo/importFeature")
    public Response importFeature() {
        return null;
    }

    /*
     *重置学生
     * */
    @GetMapping("/userInfo/resetLibrary")
    public Response resetLibrary(String token) {
        return null;
        //return new UnifiedShow(new StudentFaceServiceImpl().resetLibrary(token));
    }

    /*
     * 从基础云平台下载个人图片
     * */
    @GetMapping("/studentFace/getPhotoFromYun")
    public Response getPhotoFromYun(String token, String userId, Integer userType) {
        return null;
        //return new UnifiedShow(sfService.getPhotoFromYun(token, userId, userType));
    }

    /*
     * 从基础云平台得到所有学校信息
     * */
    @GetMapping("/userInfo/getSchoolInfo")
    public Response getSchoolInfo() {
        return null;
        //return new UnifiedShow(new SchoolInfoStructUnits().getSchoolInfo());
    }

    /*
     * 从基础云平台得到所有年级信息
     * */
    @GetMapping("/userInfo/getGradeInfo")
    public Response getGradeInfo(String token) {
        return null;
        //new StudentFaceServiceImpl().SyncStudents(token);
        //return new UnifiedShow(new SchoolInfoStructUnits().getGradeInfo(token));
    }

    /*
     * 从基础云平台得到所有班级信息
     * */
    @GetMapping("/userInfo/getClassInfo")
    public Response getClassInfo(String token) {
        return null;
        //return new UnifiedShow(new SchoolInfoStructUnits().getClassInfo(token));
    }


}
