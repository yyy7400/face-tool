package com.yang.face.service.yun;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.thoughtworks.xstream.XStream;
import com.yang.face.constant.Properties;
import com.yang.face.util.DateTimeUtil;
import com.yang.face.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.List;

/*
 * yangyuyang 2019-07-15
 * 
 * 加密锁
 * 
 * */
public class LockerMgrUtil {

	private static final Logger logger = LoggerFactory.getLogger(LockerMgrUtil.class);
	
	public static String ADDR = "LockerMgr/WS/Service_LockerMgr.asmx/";
	String yunAddr = Properties.YUN_SERVER_ADDR;
	
	public SubSystemLocker getLocker(int lockId) {		
		
		SubSystemLocker res = null;
		try {			
			String method = "WS_G_GetSubSystemLockerInfoByID";
			String lockerId = String.format("?LockerID=%d", lockId);
			//yyyy-MM-dd HH:mm:ss
			String dateStr = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_FORMAT);
			String requestTime = String.format("&RequestTime=%s", URLEncoder.encode(dateStr,"utf-8"));			
			String secCode = String.format("&SecCode=%s", YunMD5.reverseMd5(lockId + dateStr));//cabb8967157bb11df006f75f879ff8e3
			
	
			StringBuffer stringBuffer = new StringBuffer();
			String url = stringBuffer
					.append(yunAddr)
					.append(ADDR)
					.append(method)
					.append(lockerId)
					.append(requestTime)
					.append(secCode)
					.toString();
			
			String result = HttpClientUtil.httpGetStr(url);
			if(result == "") {
                return null;
            }

			// 使用注解的方式获取 
			XStream xStream = new XStream();
			
			xStream.processAnnotations(ListType.class);// 显示声明使用注解
			xStream.autodetectAnnotations(true);
			ListType list = (ListType) 
							xStream.fromXML(result);// 使用注解的方式获取 
			
			if(list == null || list.getList() == null|| list.getList().size() == 0) {
				return null;
			}
			
			List<String> objs = list.getList();	
			res = new SubSystemLocker();
			res.setResult(Integer.valueOf(objs.get(0)));
			res.setPoint(getDecryptInt(lockId, objs.get(1)));
			res.setYear(getDecryptInt(lockId, objs.get(2)));
			res.setMonth(getDecryptInt(lockId, objs.get(3)));
			res.setDay(getDecryptInt(lockId, objs.get(4)));
			String time = getDecrytString(lockId, objs.get(5));
			res.setTime(DateTimeUtil.getDateTime(time));

			
		} catch (IOException e) {			
			logger.error(e.getMessage(), e);
			return null;
		}
				
		return res;
		
	}
	
	private int getDecryptInt(int lockid, String str)
    {
		String s = getDecrytString(lockid, str);
        if (s == null || s.isEmpty()) {
            return -1;
        }

        return Integer.valueOf(s);
    }


    private String getDecrytString(int lockid, String str)
    {
        return YunEncrypt.decryptCode(String.valueOf(lockid), str);
    }
}
