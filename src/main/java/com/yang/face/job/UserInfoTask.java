package com.yang.face.job;

import com.yang.face.service.UserInfoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Yang
 */
@Component
@Order(2)
public class UserInfoTask implements CommandLineRunner {

    private final UserInfoService userInfoService;

    public UserInfoTask(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    public void updateTask() {
        userInfoService.updateYunUserInfo();
    }

    @Override
    public void run(String... args) throws Exception {
        updateTask();
    }
}
