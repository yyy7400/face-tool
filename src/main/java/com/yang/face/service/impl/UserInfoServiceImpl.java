package com.yang.face.service.impl;

import com.yang.face.entity.db.UserInfo;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.UserInfoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Cacheable(cacheNames = "userInfos")
    @Override
    public List<UserInfo> selectAll() {
        //System.out.println("调用了db all");
        return userInfoMapper.selectAll();
    }

    @CacheEvict(value = "userInfos", allEntries = true)
    @Override
    public void clearSelectAllCache() {
    }


    @Cacheable(value = "userInfos", key = "#userId")
    @Override
    public UserInfo selectByUserId(String userId) {

        //System.out.println("调用了db userId");
        return userInfoMapper.selectOne(new UserInfo(null, userId, null, null, null, null, null, null, null, null));
    }

    @Override
    public List<UserInfo> selectByUserIds(List<String> userIds, List<UserInfo> list) {

        List<UserInfo> res = new ArrayList<>();

        Map<String, UserInfo> map = list.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
        for (String userId : userIds) {
            if (map.containsKey(userId)) {
                res.add(map.get(userId));
            }
        }

        return res;
    }

}
