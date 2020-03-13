package com.yang.face.service.impl;

import com.yang.face.entity.db.SystemSetting;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.mapper.SystemSettingMapper;
import com.yang.face.service.SystemSettingService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

/**
 * @author yangyuyang
 * @date 2020/3/13 10:46
 */
@Service
public class SystemSettingServeiceImpl implements SystemSettingService {

    @Mapper
    private SystemSettingMapper systemSettingMapper;

    @Cacheable(value = "systemSetting")
    @Override
    public SystemSetting selectOne() {
        return systemSettingMapper.selectOne(null);
    }

    @CacheEvict(value = "systemSetting")
    @Override
    public MessageVO update(SystemSetting systemSetting) {

        SystemSetting obj = systemSetting.clone();
        systemSetting.setId(1);

        Integer state = systemSettingMapper.updateByPrimaryKeySelective(obj);
        if(state > 0) {
            return new MessageVO(true, state);
        } else  {
            return new MessageVO(false, state);
        }
    }

    @CacheEvict(value = "systemSetting")
    @Override
    public MessageVO updateFaceType(Integer faceType) {

        SystemSetting obj = new SystemSetting();
        obj.setId(1);
        obj.setFaceType(faceType);

        Integer state = systemSettingMapper.updateByPrimaryKeySelective(obj);
        if(state > 0) {
            return new MessageVO(true, state);
        } else  {
            return new MessageVO(false, state);
        }
    }
}
