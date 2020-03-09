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

/**
 * 从云平台，拿取学期信息
 * @author yangyuyang
 */
@Service
public class TermInfoUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(TermInfoUtil.class);

	private static String ADDR = "SysMgr/SysSetting/WS/Service_SysSetting.asmx/";

	String yunAddr = Properties.YUN_SERVER_ADDR;
	String method = "WS_SysMgr_G_GetFullTermInfo";

	/**
	 * 返回格式
	 * 2019-202001 ：2019-2020学年第一学期
	 * 2019/9/1 0:00:00
	 * 2020/2/15 0:00:00
	 */
	public TermInfoExtract getTermInfo() {
		String url = yunAddr + ADDR + method;

		TermInfoExtract res = new TermInfoExtract();
		try {
			String result = new HttpClientUtil().httpGetStr(url);
			if(result.isEmpty()) {
				return res;
			}

			// 使用注解的方式获取 
			XStream xStream = new XStream();
			xStream.processAnnotations(TermInfo.class);// 显示声明使用注解
			xStream.autodetectAnnotations(true);
			TermInfo termInfo = (TermInfo) 
							xStream.fromXML(result);// 使用注解的方式获取
			
			if(!termInfo.getList().isEmpty()) {
				res.setTermNo(termInfo.getList().get(0));
				res.setTermStartTime(termInfo.getList().get(1));
				res.setTermEndTime(termInfo.getList().get(2));
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
				
		return res;
	}
	
	public int getTermTime() {
		
		TermInfoExtract res = getTermInfo();
		String strTime = res.getTermStartTime();
		String[] list = strTime.split(" ");
		String[] listTime = list[0].split("/");
		
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : listTime) {
			if(string.length() == 1) {
				stringBuilder.append("0").append(string);
			}else {
				stringBuilder.append(string);
			}
		}

		return Integer.parseInt(stringBuilder.toString());
	}

	public static void main(String[] args) {
		//TermInfoExtract a = new TermInfoUtil().getTermInfo();
	}

}
