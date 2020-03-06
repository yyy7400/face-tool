package com.yang.face.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取application.properties 文件
 */
@Component
public class Properties {

    public static String SERVER_IP;

    public static String SERVER_PORT;

    public static String SERVER_ADRR;

    public static String SERVER_RESOURCE;

    public static String SERVER_RESOURCE_IMAGE_FEATRUE;

    public static String YUN_SERVER_ADDR;

    public static Integer FACE_RECO_Type;

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

    @Value("${server.resource.image.feature}")
    public void setServerResourceImageFeatrue(String serverResourceImageFeatrue) {
        SERVER_RESOURCE_IMAGE_FEATRUE = serverResourceImageFeatrue;
    }

    @Value("${face.reco.type}")
    public void setFACE_RECO_Type(Integer FACE_RECO_Type) {
        Properties.FACE_RECO_Type = FACE_RECO_Type;
    }

    @Value("${yun.server.addr}")
    public void setYunServerAddr(String yunServerAddr) {
        YUN_SERVER_ADDR = yunServerAddr;
    }
}
