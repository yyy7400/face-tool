package com.yang.face.service.yun;
/*
 * author zhufeng 2019-03-27
 */

import com.thoughtworks.xstream.XStream;
import com.yang.face.constant.Properties;
import com.yang.face.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangyuyang
 * 连接云平台，根据从基础平台生成的token,得到学生详细信息
 */
@Service
public class AdminStructUtil {
    private static final Logger logger = LoggerFactory.getLogger(AdminStructUtil.class);
    private static String ADDR = "UserMgr/UserInfoMgr/WS/Service_UserInfo.asmx/";


    String yunAddr = Properties.YUN_SERVER_ADDR;

    String Token = "?Token=";
    String UserID = "&UserID=";
    String UserName = "&UserName=";
    String SchoolID = "&SchoolID=";
    String UserType = "&UserType=";
    String UpdateTime = "&UpdateTime=";
    String DataModel = "&DataModel=all";

    public List<AdminStruct> getAdminDetailInfo() {

        String method = "WS_UserMgr_G_GetAdmin";

        YunToken yunToken = new YunToken();
        String token = yunToken.getTokenAdmin();
        return getAdminDetailInfo(token);
    }

    public List<AdminStruct> getAdminDetailInfo(String token) {
        return getAdminDetailInfo(token, "");
    }

    public List<AdminStruct> getAdminDetailInfo(String token, String userId) {

        String method = "WS_UserMgr_G_GetAdmin";

        Token = Token + token;
        UserID = UserID + userId;

        String url = yunAddr + ADDR + method + Token + UserID +
                UserName + SchoolID + UserType + UpdateTime + DataModel;
        List<AdminStruct> resList = new ArrayList<>();

        try {
            String result = new HttpClientUtil().httpGetStr(url);
            if (result.isEmpty()) {
                return resList;
            }

            // 使用注解的方式获取
            XStream xStream = new XStream();
            xStream.processAnnotations(AdminStructList.class);// 显示声明使用注解
            xStream.autodetectAnnotations(true);
            AdminStructList adminStructList = (AdminStructList)
                    xStream.fromXML(result);// 使用注解的方式获取


            resList = adminStructList.getAdminStructList();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return resList;

    }

}
