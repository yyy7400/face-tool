package com.yang.face.controller;

import com.yang.face.entity.show.Response;
import com.yang.face.lock.Locker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 电子加密锁
 * @author yangyuyang
 * @date 2020/3/19 14:37
 */
@RestController
public class LockerController {

    /**
     * 测试接口，查看本系统电子锁信息
     * @return
     */
    @GetMapping("/Locker/checkLocker")
    public Response checkLocker() {
        return Response.show(new Locker().checkLocker());
    }

    /**
     * 前台调用此接口，判断电子锁是否正常，小于0不正常，大于等于0正常
     * @return
     */
    @GetMapping("/Locker/getLocker")
    public Response getLocker() {
        return Response.show(new Locker().getLock());
    }
}