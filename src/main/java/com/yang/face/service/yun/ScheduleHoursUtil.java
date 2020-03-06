package com.yang.face.service.yun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yang.face.constant.Properties;
import com.yang.face.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangyuyang
 */
public class ScheduleHoursUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleHoursUtil.class);
    private static String ADDR = "UserMgr/TeachInfoMgr/Api/Service_TeachInfo.ashx";

    String yunAddr = Properties.YUN_SERVER_ADDR;
    String Method = "?method=GetScheduleHours";
    String Params = "&params=";

    /**
     * 获取课程表时间安排
     * @return
     */
    public List<ScheduleHours> getScheduleHours() {

        List<SchoolInfoStruct> schools =  new SchoolInfoStructUtil().getSchoolInfo();
        if(schools.isEmpty()) {
            logger.error("获取学校数量为空");
            return new ArrayList<>();
        }

        String schoolId = schools.get(0).getSchoolID();
        String token = new YunToken().getTokenAdmin();

        return getScheduleHours(schoolId, token);
    }

    public List<ScheduleHours> getScheduleHours(String schoolId, String token) {

        System.out.println("schoolId:" + schoolId );
        Params += schoolId;
        Params += "&token=" + token;
        String url = yunAddr + ADDR + Method + Params;

        System.out.println("get schedule:" + url);
        List<ScheduleHours> list = new ArrayList<>();

        try {

            String result = new HttpClientUtil().httpGetStr(url);
            if(result.isEmpty()) {
                return list;
            }
            result = URLDecoder.decode(result, "UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            ScheduleHoursList resList = mapper.readValue(result, ScheduleHoursList.class);

            list.addAll(resList.getData());

            return list;
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
            return list;
        }

    }

    public static void main(String[] args) {
        new ScheduleHoursUtil().getScheduleHours();
    }
}
