package com.yang.face.mapper;

import com.yang.face.entity.db.UserInfo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author yangyuyang
 */
@org.apache.ibatis.annotations.Mapper
public interface UserInfoMapper extends Mapper<UserInfo>, MySqlMapper<UserInfo> {

    @Update("truncate table user_info")
    public void turncateTable();
}
