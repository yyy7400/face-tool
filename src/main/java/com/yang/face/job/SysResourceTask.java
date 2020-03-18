package com.yang.face.job;

import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.util.FileUtil;
import com.yang.face.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Yang
 */
@Component
@Order(1)
public class SysResourceTask implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SysResourceTask.class);

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    private TimerTask timerTask;

    /**
     * 创建初始文件夹
     */
    private void createFolder() {

        List<String> dirs = new ArrayList<>();
        dirs.add(Properties.SERVER_RESOURCE);
        dirs.add(Properties.SERVER_RESOURCE + Constants.Dir.UPLOAD);
        dirs.add(Properties.SERVER_RESOURCE + Constants.Dir.FACE_FEATRUE);
        dirs.add(Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE);
        dirs.add(Properties.SERVER_RESOURCE + Constants.Dir.TEMP);
        dirs.add(Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_YUN);
        dirs.add(Properties.SERVER_RESOURCE + Constants.Dir.CLASS);

        for (String str : dirs) {
            File file = new File(str);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 清理python特征库文件
     */
    private void clearPythonFeatureFile() {

        List<File> files = FileUtil.getFilesAll(PathUtil.combine(Properties.SERVER_RESOURCE, Constants.Dir.FACE_FEATRUE));

        Example example = new Example(UserInfo.class);
        example.createCriteria()
                .andEqualTo("faceFeatureType", FaceFeatureTypeEnum.OPENVINO.getKey())
                .andNotEqualTo("faceFeatureFile", "");

        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        Set<String> set = new HashSet<>();
        userInfos.forEach(o -> set.add(o.getUserId()));

        files.forEach((f) -> {

            try {
                String[] strs = f.getName().split(".");
                // 后缀名是不是npy的删除
                if(strs.length < 2 || !Constants.PYTHON_FEATURE_EXT.equals(strs[1])) {
                    f.delete();
                    return;
                }

                // 不在数据库存储的文件的删除
                if(!set.contains(strs[0])) {
                    f.delete();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(),ex);
            }

        });
    }


    @Override
    public void run(String... args) {

        createFolder();
        //clearPythonFeatureFile();
        timerTask.clearFeatrueImage();

        //clearFeatrueImage();
    }
}
