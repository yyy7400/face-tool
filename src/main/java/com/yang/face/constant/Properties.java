package com.yang.face.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Yang
 * 读取application.properties 文件
 */
@Component
public class Properties {

    public static String SERVER_IP;

    public static String SERVER_PORT;

    public static String SERVER_ADRR;

    public static String SERVER_RESOURCE;

    public static String YUN_SERVER_ADDR;

    @Value("${server.ip}")
    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }

    @Value("${server.port}")
    public void setServerPort(String serverPort) {
        SERVER_PORT = serverPort;
    }

    @Value("${server.addr}")
    public void setServerAdrr(String serverAdrr) {
        SERVER_ADRR = serverAdrr;
    }

    @Value("${server.resource}")
    public void setServerResource(String serverResource) {
        SERVER_RESOURCE = serverResource;
    }

    @Value("${yun.server.addr}")
    public void setYunServerAddr(String yunServerAddr) {
        YUN_SERVER_ADDR = yunServerAddr;
    }
}
