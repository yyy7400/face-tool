package com.yang.face.service;

/**
 * 取基础平台用户信息和验证登录状态  接口
 * @author zhaoxiaofeng
 * @time 20191010
 */
public interface ProxyService {
    String proxyRequest(String url, String cookie);
}
