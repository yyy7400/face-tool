package com.yang.face.service.yun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.List;

/**
 * 2018-12-13
 * 生成基础平台token
 * @author yangyuyang
 */
public class YunToken {

    private static final Logger logger = LoggerFactory.getLogger(YunToken.class);

    String yunAddr = Properties.YUN_SERVER_ADDR + "UserMgr/Login/Api/Login.ashx";

    //获取学校超级管理员的token
    public String getTokenAdmin() {
        List<SchoolInfoStruct> list = new SchoolInfoStructUtil().getSchoolInfo();
        if (list.isEmpty()) {
            return "";
        }

        return getToken("admin_" + list.get(0).getSchoolCode());
    }

    /**
     * 获取基础平台token
     */
    public String getToken(String userId) {
        try {
            userId = encodeCardUserID(userId);
            String params = URLEncoder.encode(String.format("%s|%s|%d||||", userId, Constants.SYS_ID, MachineType.TERMINAL), "utf-8");
            String url = yunAddr + "?method=LoginByCard&params=" + params;

            String jsonStr = HttpClientUtil.httpGetStr(url);

            if (jsonStr.isEmpty()) {
                return "";
            }

            ObjectMapper mapper = new ObjectMapper();
            TokenJson result = mapper.readValue(jsonStr, TokenJson.class);

            if (result.getError() == 0 && result.getData() != null && result.getData().getResult() == 1) {
                return result.getData().getToken();
            }
            else {
                return "";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

    private static String encodeCardUserID(String userId) {
        // 0.1 数据反序 ---- 字符串格式为：用户ID?学校ID?卡的序列号? 
        String str_before = userId + "?0?0?";

        char[] charArray = str_before.toCharArray();
        char[] charArray2 = new char[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            charArray2[charArray.length - i - 1] = charArray[i];
        }

        // 0.2 单数（位数）放前面，双数（位数）放后面
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        for (int i = 0; i < charArray2.length; ++i) {
            if (i % 2 != 0) {
                s1.append(charArray2[i]);
            }
            else {
                s2.append(charArray2[i]);
            }
        }
        return s1.append(s2).toString();
    }
}
