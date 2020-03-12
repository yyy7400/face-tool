package com.yang.face.service.yun;
/*
 * author zhufeng 2018-12-17
 */

import cn.hutool.core.net.NetUtil;
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
public class StudentStructUtil {

	private static final Logger logger = LoggerFactory.getLogger(StudentStructUtil.class);
	private static String addr = "UserMgr/UserInfoMgr/WS/Service_UserInfo.asmx/";
	
	
	String yunAddr = Properties.YUN_SERVER_ADDR;
	
	String Token = "?Token=";	
	String UserID = "&UserID=";
	String UserName = "&UserName=";
	String ClassID = "&ClassID=";
	String GradeID = "&GradeID=";
	String SchoolID = "&SchoolID=";
	String UserClass = "&UserClass=";
	String TeacherID = "&TeacherID=";
	String UpdateTime = "&UpdateTime=";
	String DataModel = "&DataModel=";
	
	
	public List<StudentStruct> getStuDetailInfo() {
	
		YunToken token = new YunToken();
		return getStuDetailInfo(token.getTokenAdmin());
	}
	
	public List<StudentStruct> getStuDetailInfo(String token) {
		return getStuDetailInfo(token, "");
	}
	
	public List<StudentStruct> getStuDetailInfo(String token, String userId) {

		String method = "WS_UserMgr_G_GetStudent";
		
		Token = Token + token;
		UserID = UserID + userId;

		String url = new StringBuilder().append(yunAddr)
				.append(addr)
				.append(method)
				.append(Token)
				.append(UserID)
				.append(UserName)
				.append(ClassID)
				.append(GradeID)
				.append(SchoolID)
				.append(UserClass)
				.append(TeacherID)
				.append(UpdateTime)
				.append(DataModel)
				.toString();


		List<StudentStruct> resList = new ArrayList<>();
		
		try {
			String result = HttpClientUtil.httpGetStr(url);
			if(result.isEmpty()) {
				return resList;
			}
			
			// 使用注解的方式获取 
			XStream xStream = new XStream();
			xStream.processAnnotations(StudentStructList.class);// 显示声明使用注解
			xStream.autodetectAnnotations(true);
			StudentStructList studentStructList = (StudentStructList) 
							xStream.fromXML(result);// 使用注解的方式获取 
			
			
			
			resList = studentStructList.getStudentStructList();
			
		} catch (IOException e) {
			
			logger.error(e.getMessage(), e);
		}
		
		
		return resList;
		
	}
}
