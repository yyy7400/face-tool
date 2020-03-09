package com.yang.face.service;

import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.show.FacePathShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.entity.show.PageShow;
import com.yang.face.entity.show.TeacherGroup;
import com.yang.face.service.yun.ClassStruct;
import com.yang.face.service.yun.GradeStruct;

import java.util.List;

public interface UserInfoService {

    /**
     * 获取所有用户，加入缓存
     * @return
     */
    public List<UserInfo> selectAll();

    /**
     * 清除缓存
     */
    public void clearSelectAllCache();

    /**
     * 查询用户
     * @param groupId
     * @param userType
     * @param userName
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public PageShow search (String groupId, Integer userType, String userName, Integer pageIndex, Integer pageSize);

    public UserInfo selectByUserId(String userId);

    public UserInfo selectById(Integer id);

    public MessageVO updatePhoto(String userId, String photo);

    public MessageVO deletePhoto(String userId);

    public List<UserInfo> selectByUserIds(List<String> userIds, List<UserInfo> list);

    public FacePathShow getPhotoFromYun(String token, String userId, Integer userType);

    /**
     * 更新基础平台用户信息
     * @return
     */
    public Boolean updateYunUserInfo();

    /**
     * 重置用户信息
     * @return
     */
    public MessageVO resetUserInfo();

    public List<TeacherGroup> getGroupInfo(String token);

}
