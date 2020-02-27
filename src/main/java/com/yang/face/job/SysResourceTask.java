package com.yang.face.job;

import com.yang.face.constant.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class SysResourceTask implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SysResourceTask.class);

    private void createFolder() {

        List<String> dirs = new ArrayList<>();
        dirs.add(Properties.SERVER_RESOURCE);
        dirs.add(Properties.SERVER_RESOURCE_IMAGE_FEATRUE);

        for(String str : dirs) {
            File file = new File(str);
            if(!file.exists()) {
                file.mkdirs();
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {

        createFolder();
    }
}
