package com.yang.face.service;

import com.yang.face.entity.db.UserInfo;

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


    public List<UserInfo> search(String schoolId, String groupId, String userName, Integer pageIndex, Integer pageSize);

    public UserInfo selectByUserId(String userId);

    public UserInfo selectById(Integer id);

    public boolean updatePhtoto(String userId, String photo);

    public boolean deletePhoto(String userId);

    public List<UserInfo> selectByUserIds(List<String> userIds, List<UserInfo> list);

    /**
     * 更新用户信息
     * @return
     */
    Boolean updateUserInfo();

}
