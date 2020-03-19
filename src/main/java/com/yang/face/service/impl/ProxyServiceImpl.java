package com.yang.face.service.impl;
import cn.hutool.http.HttpRequest;
import com.yang.face.service.ProxyService;
import org.springframework.stereotype.Service;

/**
 * 取基础平台用户信息和验证登录状态
 * @author zhaoxiaofeng
 * @time 20191010
 */
@Service
public class ProxyServiceImpl implements ProxyService {

    @Override
    public String proxyRequest(String url, String cookie) {
        if (url.contains("|")) {
            url = url.replace("|", "%7C");
        }
        HttpRequest httpRequest = HttpRequest.get(url);
        String result;
        result = httpRequest.cookie(cookie)
                .execute().body();
        return result;
    }
}
