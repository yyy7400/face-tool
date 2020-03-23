package com.yang.face.job;

import com.yang.face.client.ClientManager;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @author Yang
 */
@Component
@EnableScheduling
public class TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TimerTask.class);

    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 每天 00:20 执行
     */
    @Scheduled(cron = "0 20 0 * * ?")
    public void clearFeatrueImage() {

        // 1. 数据库所有图片
        List<UserInfo> users = userInfoMapper.selectAll();
        Set<String> imageSet = new HashSet<>();
        for (UserInfo userInfo : users) {
            if ("".equals(userInfo.getPhotoUrl())) {
                continue;
            }
            String[] strs = userInfo.getPhotoUrl().split("/");
            String name = strs[strs.length - 1];
            String name_c = name.substring(0,name.lastIndexOf('.')) + "_c" + name.substring(name.indexOf('.'));

            imageSet.add(name);
            imageSet.add(name_c);
        }

        // 2. 查找路径下所有图片，并删除不记录在数据库中的图片
        File file = new File(Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE);
        for (File f : file.listFiles()) {
            if (!imageSet.contains(f.getName())) {
                f.delete();
            }
        }
    }

    /**
     * 从1号开始，每两天的 00:50 执行
     */
    @Scheduled(cron = "0 50 0 1/2 * ?")
    public void clearUselessFile() {

        List<String> list = new ArrayList<>();
        list.add(Properties.SERVER_RESOURCE + Constants.Dir.TEMP);
        list.add(Properties.SERVER_RESOURCE + Constants.Dir.UPLOAD);

        for (String str : list) {
            FileUtil.deleteSubFileAndFolder(str);
        }
    }

    // 从0秒开始，每秒检测一次, 10s 过期
    @Scheduled(cron = "0/1 * * * * ?")
    public void clearExpiredClient() {
        ClientManager.clearExpiredClient();
    }
}
