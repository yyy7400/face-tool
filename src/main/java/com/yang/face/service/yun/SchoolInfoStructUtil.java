package com.yang.face.service.yun;
/*
 * author zhufeng 2018-12-14
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
 * 从云平台，拿取学校信息
 */
@Service
public class SchoolInfoStructUtil {

    private static final Logger logger = LoggerFactory.getLogger(SchoolInfoStructUtil.class);

    String yunAddr = Properties.YUN_SERVER_ADDR;

    //从云平台，拿取学校信息
    public List<SchoolInfoStruct> getSchoolInfo() {

        String addr = "SysMgr/SysSetting/WS/Service_SysSetting.asmx/";
        String method = "WS_SysMgr_G_GetSchoolBaseInfo";
        String schoolId = "?schoolID=";

        String url = yunAddr + addr + method + schoolId;
        List<SchoolInfoStruct> resList = new ArrayList<SchoolInfoStruct>();

        try {
            String result = HttpClientUtil.httpGetStr(url);
            if (result.isEmpty()) {
                return resList;
            }

            // 使用注解的方式获取
            XStream xStream = new XStream();
            xStream.processAnnotations(SchoolInfoStructList.class);// 显示声明使用注解
            xStream.autodetectAnnotations(true);
            SchoolInfoStructList schoolInfoList = (SchoolInfoStructList)xStream.fromXML(result);// 使用注解的方式获取


            resList = schoolInfoList.getSchoolInfoStructList();
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
        }


        return resList;

    }

    //从云平台，拿取年级信息
    public List<GradeStruct> getGradeInfo(String token) {

        String addr = "UserMgr/UserInfoMgr/WS/Service_UserInfo.asmx/";
        String method = "WS_UserMgr_G_GetGrade";
        String tokens = "?Token=" + token;
        String gradeID = "&GradeID=";
        String schoolId = "&SchoolID=";

        String url = yunAddr + addr + method + tokens + gradeID + schoolId;
        List<GradeStruct> resList = new ArrayList<GradeStruct>();

        try {
            String result = HttpClientUtil.httpGetStr(url);
            if (result == "") {
				return resList;
			}

            // 使用注解的方式获取
            XStream xStream = new XStream();
            xStream.processAnnotations(GradeStructList.class);// 显示声明使用注解
            xStream.autodetectAnnotations(true);
            GradeStructList gradeStructList = (GradeStructList)
                    xStream.fromXML(result);// 使用注解的方式获取


            resList = gradeStructList.getGradeList();
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
        }


        return resList;

    }

    //从云平台，拿取班级信息
    public List<ClassStruct> getClassInfo(String token) {

        String addr = "UserMgr/UserInfoMgr/WS/Service_UserInfo.asmx/";
        String method = "WS_UserMgr_G_GetClass";
        String tokens = "?Token=" + token;
        String classID = "&ClassID=";
        String gradeID = "&GradeID=";
        String schoolID = "&SchoolID=";
        String teacherGH = "&TeacherGH=";
        String updateTime = "&UpdateTime=";

        String url = yunAddr + addr + method + tokens + classID + gradeID + schoolID + teacherGH + updateTime;
        List<ClassStruct> resList = new ArrayList<ClassStruct>();

        try {
            String result = HttpClientUtil.httpGetStr(url);
            if (result == "") {
				return resList;
			}

            // 使用注解的方式获取
            XStream xStream = new XStream();
            xStream.processAnnotations(ClassStructList.class);// 显示声明使用注解
            xStream.autodetectAnnotations(true);
            ClassStructList classStructList = (ClassStructList)
                    xStream.fromXML(result);// 使用注解的方式获取


            resList = classStructList.getClassList();
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
        }

        return resList;
    }

    //通过班主任获取行政班
    public List<ClassStruct> getClassInfoByUser(String token, String teacherId) {

        String addr = "UserMgr/UserInfoMgr/WS/Service_UserInfo.asmx/";
        String method = "WS_UserMgr_G_GetClassByUser";
        String tokens = "?Token=" + token;
        String classID = "&ClassID=";
        String gradeID = "&GradeID=";
        String schoolID = "&SchoolID=";
        String teacherGH = "&TeacherID=" + teacherId;
        String updateTime = "&UpdateTime=";

        String url = new StringBuilder().append(yunAddr)
                .append(addr)
                .append(method)
                .append(tokens)
                .append(classID)
                .append(gradeID)
                .append(schoolID)
                .append(teacherGH)
                .append(updateTime)
                .toString();
        List<ClassStruct> resList = new ArrayList<ClassStruct>();

        try {
            String result = HttpClientUtil.httpGetStr(url);
            if (result == "") {
                return resList;
            }

            // 使用注解的方式获取
            XStream xStream = new XStream();
            xStream.processAnnotations(ClassStructList.class);// 显示声明使用注解
            xStream.autodetectAnnotations(true);
            ClassStructList classStructList = (ClassStructList)
                    xStream.fromXML(result);// 使用注解的方式获取

            resList = classStructList.getClassList();
        } catch (IOException e) {

            logger.error(e.getMessage(), e);
        }

        return resList;
    }
}
