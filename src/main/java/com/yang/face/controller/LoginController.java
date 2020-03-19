package com.yang.face.controller;

import com.yang.face.constant.Properties;
import com.yang.face.entity.show.Response;
import com.yang.face.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yangyuyang
 * @date 2020/3/19 14:29
 */

public class LoginController {

    @Autowired
    private ProxyService proxyService;
    /**
     * 获取基础平台地址
     * @return
     */
    @GetMapping("/mainServerAddr/getMainServerAddr")
    public Response getMainServerAddr(){
        return Response.show(Properties.YUN_SERVER_ADDR);
    }

    /**
     * 查询用户是否在线
     * @param token
     * @return
     */
    @GetMapping("/mainServerAddr/WS_UserMgr_G_IsOnline")
    public Response  WS_UserMgr_G_IsOnline(String token){
        String url = Properties.YUN_SERVER_ADDR + "UserMgr/Login/Api/Login.ashx?token="+token+"&method=TokenCheck&params=E35";
        return Response.show(proxyService.proxyRequest(url, ""));
    }

    /**
     * 获取管理员信息
     * @param token
     * @return
     */
    @GetMapping("/mainServerAddr/WS_UserMgr_G_GetAdmin")
    public Response WS_UserMgr_G_GetAdmin(String token){
        String url = Properties.YUN_SERVER_ADDR + "UserMgr/Login/Api/Login.ashx?token="+token+"&method=GetUserInfo&params=E35";
        return Response.show(proxyService.proxyRequest(url, ""));
    }

    /**
     *登出
     * @param token
     * @return
     */
    @GetMapping("/mainServerAddr/loginOut")
    public Response loginOut(String token){
        String url = Properties.YUN_SERVER_ADDR + "UserMgr/Login/Api/Login.ashx?token="+token+"&method=Logout&params=E35";
        return Response.show(proxyService.proxyRequest(url, ""));
    }
}
