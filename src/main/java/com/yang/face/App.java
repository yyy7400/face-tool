package com.yang.face;

import com.yang.face.service.impl.FaceEngineServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangyuyang
 */
@SpringBootApplication
@MapperScan("com.yang.face.mapper")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        System.out.println(new FaceEngineServiceImpl());

        StringBuffer a;
        StringBuilder b;
        String str;
        ArrayList l = new ArrayList();
        Map<Integer, String> c = new ConcurrentHashMap<>();
    }

}
