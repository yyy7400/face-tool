package com.yang.face.service.yun;

import java.io.UnsupportedEncodingException;

public class YunEncrypt {

	/**
	 * 加密算法
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 * @return加密后字符串
	 */
	public static String encryptCode(String userID, String str) {
		byte[] idByte = null;
		idByte = YunMD5.md5(userID).getBytes();
		byte S_key = idByte[idByte.length - 1];// 密钥
		byte[] value;
		try {
			value = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			value = str.getBytes();
		}
		int[] valueXOR = new int[value.length];// 异或之后的字符串
		StringBuffer result = new StringBuffer("");// 结果字符串
		for (int i = 0; i < value.length; i++) {
			// value[i]与S_key异或运算
			// Log.i("加密", "异或之后 " + (value[i] ^ S_key));
			valueXOR[i] = (value[i] ^ S_key);
			if (valueXOR[i] < 0) {
				valueXOR[i] += 256;
			}
			result.append(String.format("%03d", valueXOR[i]));// 左侧补充0
		}
		String finalResult = result.reverse().toString();
		return finalResult;
	}

	/**
	 * 解密算法
	 * 
	 * @param userID 用户ID
	 * @param EncryptInfo 密文
	 * @throws UnsupportedEncodingException
	 * @return原文
	 */
	public static String decryptCode(String userID, String EncryptInfo) {
		byte[] idByte = YunMD5.md5(userID).getBytes();// android默认utf-8格式，一个中文字符占用3个字符
		byte S_key = idByte[idByte.length - 1];// 密钥
		String S_source = new StringBuffer("").append(EncryptInfo).reverse()
				.toString();
		byte[] value = new byte[S_source.length() / 3];// 构造value数组

		int temp;
		for (int i = 0, k = 0; i < S_source.length(); i += 3, k++) {
			temp = Integer.parseInt(S_source.substring(i, i + 3));
			value[k] = (byte) (temp ^ S_key);
		}
		String result;
		try {
			result = new String(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			result = new String(value);
		}
		return result;
	}
}
