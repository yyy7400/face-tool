package com.yang.face.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;

/**
 * @author yangyuyang
 * @date 2020/3/25 16:54
 */
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    /**
     * 配置解决跨域问题
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对那种格式的请求进行跨域处理
        registry.addMapping("/**")
                // 表示支持的域
                .allowedOrigins("*")
                // 表示允许的请求头，默认是全部
                .allowedHeaders("*")
                // 表示允许的请求方法，默认是post、get和head
                .allowedMethods("*")
                .allowCredentials(true)
                // 表示探测请求的有效期
                .maxAge(3600);
    }

    /**
     * 拦截器注册
     *
     * @param registry 注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new ApiSignatureInterceptor())
//                .addPathPatterns("/**")
//        ;
    }



    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        //factory.setMaxFileSize("30000MB");
        /// 设置总上传数据总大小
        //factory.setMaxRequestSize("300000MB");
        return factory.createMultipartConfig();
    }

}
