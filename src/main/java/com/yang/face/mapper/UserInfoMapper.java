package com.yang.face.mapper;

import com.yang.face.entity.db.UserInfo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author yangyuyang
 */
@org.apache.ibatis.annotations.Mapper
public interface UserInfoMapper extends Mapper<UserInfo>, MySqlMapper<UserInfo> {
}
