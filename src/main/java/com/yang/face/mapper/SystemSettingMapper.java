package com.yang.face.mapper;

import com.yang.face.entity.db.SystemSetting;
import com.yang.face.entity.db.UserInfo;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author yangyuyang
 */
@org.apache.ibatis.annotations.Mapper
public interface SystemSettingMapper extends Mapper<SystemSetting>, MySqlMapper<SystemSetting> {

    @Update("truncate table system_setting")
    void turncateTable();
}
