package com.yang.face.service.yun;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class YunMD5 {

	public static String reverseMd5(String string) {
		String finalPassword = new StringBuffer("").append(md5(string))
				.reverse().toString();
		return finalPassword;
	}
	
	public static String md5(String string) {
		
		if(string == null || string.isEmpty()) {
			return "";
		}

		
		byte[] msgBytes = new byte[] {};
		try {
			msgBytes = string.getBytes("gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
        /**
         * 声明使用Md5算法,获得MessaDigest对象
         */
        MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        /**
         * 使用指定的字节更新摘要
         */
        md5.update(msgBytes);
        /**
         	* 完成哈希计算,获得密文
         */
        byte[] digest = md5.digest();
        /**
         	* 以上两行代码等同于 byte[] digest = md5.digest(msgBytes);
         */
        return byteArr2hexString(digest);
	}
	
	/**
	 * 将byte数组转化为16进制字符串形式
     * @param bys
     * @return
     */
    public static String byteArr2hexString(byte[] bys){
        StringBuffer hexVal=new StringBuffer();
        int val=0;
        for (int i = 0; i < bys.length; i++) {
            //将byte转化为int  如果byte是一个负数就必须要和16进制的0xff做一次与运算
            val=((int)bys[i]) & 0xff;
            if(val<16){
                hexVal.append("0");
            }
            hexVal.append(Integer.toHexString(val));
        }

        return hexVal.toString();

    }
}
