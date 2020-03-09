package com.yang.face.util;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpClientUtil {
	
	private static int TIMEOUT = 10000;//10s
	
	public String httpGetStr(String url) throws ClientProtocolException, IOException {
		return httpGetStr(url, TIMEOUT);
	}
	
	public String httpGetStr(String url, int timeOut) throws ClientProtocolException, IOException {
		
		RequestConfig  Requestconfig = RequestConfig.custom()
				.setConnectTimeout(timeOut)//设置连接超时时间
				.setConnectionRequestTimeout(timeOut)//设置请求超时时间
				.setSocketTimeout(timeOut)//设置socket超时时间
				.setRedirectsEnabled(true)//默认允许自动重定向
				.build();
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet getMethod = new HttpGet(url);
		
		getMethod.setConfig(Requestconfig);
		HttpResponse response = client.execute(getMethod);
		
		String result = "";
		if(response.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(response.getEntity());//得到接口返回的结果
		}
		return result;
	}

	public String httpPostStr(String jsonData, String url) throws HttpException, IOException {
		return httpPostStr(jsonData, url, TIMEOUT);
	}
	
	public String httpPostStr(String jsonData, String url, int timeOut) throws HttpException, IOException {

		//RequestConfig  Requestconfig = RequestConfig.custom().setConnectTimeout(TIMEOUT).build();//单独设置连接超时时间
		RequestConfig  Requestconfig = RequestConfig.custom()
				.setConnectTimeout(timeOut)//设置连接超时时间
				.setConnectionRequestTimeout(timeOut)//设置请求超时时间
				.setSocketTimeout(timeOut)//设置socket超时时间
				.setRedirectsEnabled(true)//默认允许自动重定向
				.build();
		
		HttpClient client = HttpClientBuilder.create().build();
		
		HttpPost post= new HttpPost(url);
		post.setConfig(Requestconfig);
		post.setHeader("Content-Type", "application/json");
		
		//String param = URLEncodedUtils.format(params, "UTF-8");
		//如果参数含有中文，加上面一句进行转码处理

		StringEntity entity = new StringEntity(jsonData, Charset.forName("UTF-8"));
		post.setEntity(entity);
		// 执行,返回一个结果
		HttpResponse response = client.execute(post);
		//System.out.println("结果：" + response);
		// 获取xml结果
		String result = "";
		if (200 == response.getStatusLine().getStatusCode()) {
			// 得到执行结果
			result = EntityUtils.toString(response.getEntity());
			//System.out.println(result);
		}
		return result;
	}
}