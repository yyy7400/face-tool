package com.yang.face.service;

import com.yang.face.entity.db.UserInfo;

import java.util.List;

public interface UserInfoService {

    public List<UserInfo> selectAll();

    public void clearSelectAllCache();

    public UserInfo selectByUserId(String userId);

    public List<UserInfo> selectByUserIds(List<String> userIds, List<UserInfo> list);

}
