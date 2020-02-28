package com.yang.face.job;

import com.yang.face.constant.Properties;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.mapper.UserInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TimerTask.class);

    @Resource
    private UserInfoMapper userInfoMapper;

    @Scheduled(cron = "15 20 10 * * ?")
    public void clearFeatrueImage() {

        // 1. 数据库所有图片
        List<UserInfo> users = userInfoMapper.selectAll();
        Map<String, UserInfo> userMap = new HashMap<>();
        for (UserInfo userInfo : users) {
            if ("".equals(userInfo.getPhotoUrl())) {
                continue;
            }
            String[] strs = userInfo.getPhotoUrl().split("/");
            String name = strs[strs.length - 1];
            userMap.put(name, userInfo);
        }

        // 2. 查找路径下所有图片，并删除不记录在数据库中的图片
        File file = new File(Properties.SERVER_RESOURCE_IMAGE_FEATRUE);
        for (File f : file.listFiles()) {
            if (!userMap.containsKey(f.getName())) {
                f.delete();
            }
        }

    }
}
