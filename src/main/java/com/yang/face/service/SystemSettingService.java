package com.yang.face.service;

import com.yang.face.entity.db.SystemSetting;
import com.yang.face.entity.show.MessageVO;

/**
 * @author yangyuyang
 * @date 2020/3/13 10:42
 */
public interface SystemSettingService {

    /**
     * 获取系统信息系统
     * @return
     */
    SystemSetting selectOne();

    /**
     * 更新系统信息
     * @param systemSetting
     * @return
     */
    MessageVO update(SystemSetting systemSetting);

}
