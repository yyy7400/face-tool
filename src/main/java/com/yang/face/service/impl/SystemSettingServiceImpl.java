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

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangyuyang
 * @date 2020/3/13 10:46
 */
@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    @Resource
    private SystemSettingMapper systemSettingMapper;

    @Cacheable(value = "systemSetting")
    @Override
    public SystemSetting selectOne() {
        List<SystemSetting> list = systemSettingMapper.selectAll();
        return systemSettingMapper.selectAll().get(0);
    }

    //同一个注解，同一个缓存区域不能使用在不同方法里面，这样会导致注解失效
    @CacheEvict(value = "systemSetting")
    @Override
    public MessageVO update(SystemSetting systemSetting) {

        SystemSetting obj = systemSetting.clone();
        obj.setId(1);

        Integer state = systemSettingMapper.updateByPrimaryKeySelective(obj);
        if(state > 0) {
            return new MessageVO(true, state);
        } else  {
            return new MessageVO(false, state);
        }
    }

}
