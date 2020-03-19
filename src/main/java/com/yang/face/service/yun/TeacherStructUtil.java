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
 * @author Yang
 * 连接云平台，根据从基础平台生成的token,得到学生详细信息
 */
@Service
public class TeacherStructUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(TeacherStructUtil.class);
	private static String addr = "UserMgr/UserInfoMgr/WS/Service_UserInfo.asmx/";
	
	
	String yunAddr = Properties.YUN_SERVER_ADDR;
	
	String Token = "?Token=";	
	String UserID = "&UserID=";
	String UserName = "&UserName=";
	String GroupID = "&GroupID=";
	String SchoolID = "&SchoolID=";
	String UserClass = "&UserClass=";
	String UpdateTime = "&UpdateTime=";
	String DataModel = "&DataModel=all";
	
	public List<TeacherStruct> getTeaDetailInfo() {
		return getTeaDetailInfo("", "");
	}
	
	public List<TeacherStruct> getTeaDetailInfo(String token) {
		return getTeaDetailInfo(token, "");
	}
	
	public List<TeacherStruct> getTeaDetailInfo(String token, String userId) {

		String method = "WS_UserMgr_G_GetTeacher";
		
		Token = Token + token;
		UserID = UserID + userId;
		
		String url = yunAddr + addr + method + Token + UserID +
				UserName + GroupID + SchoolID + UserClass + UpdateTime + DataModel;
		List<TeacherStruct> resList = new ArrayList<>();
		
		try {
			String result = HttpClientUtil.httpGetStr(url);
			if(result.isEmpty()) {
				return resList;
			}
			
			// 使用注解的方式获取 
			XStream xStream = new XStream();
			xStream.processAnnotations(TeacherStructList.class);// 显示声明使用注解
			xStream.autodetectAnnotations(true);
			TeacherStructList teaStructList = (TeacherStructList) 
							xStream.fromXML(result);// 使用注解的方式获取
			
			resList = teaStructList.getTeaStructList();
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}		
		
		return resList;
	}

	public List<TeacherGroupStruct> getTeaGroupInfo(String token, String schoolId) {

		String method = "WS_UserMgr_G_GetTeacherGroup";

		if(token.isEmpty() || token == null) {
			YunToken yunToken = new YunToken();
			Token = Token + yunToken.getTokenAdmin();
		} else {
			Token += token;
		}
		if(schoolId != null && !schoolId.isEmpty() ) {
			SchoolID = SchoolID + schoolId;
		}

		String url = new StringBuilder().append(yunAddr)
				.append(addr)
				.append(method)
				.append(Token)
				.append(GroupID)
				.append(SchoolID)
				.append(UpdateTime)
				.toString();
		List<TeacherGroupStruct> resList = new ArrayList<TeacherGroupStruct>();

		try {
			String result = HttpClientUtil.httpGetStr(url);
			if(result.isEmpty()) {
				return resList;
			}

			// 使用注解的方式获取
			XStream xStream = new XStream();
			xStream.processAnnotations(TeacherGroupStructList.class);// 显示声明使用注解
			xStream.autodetectAnnotations(true);
			TeacherGroupStructList teaGroupStructList = (TeacherGroupStructList)
					xStream.fromXML(result);// 使用注解的方式获取

			resList = teaGroupStructList.getTeaGroupStructList();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return resList;

	}
}
