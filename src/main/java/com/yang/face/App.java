package com.yang.face;

import com.yang.face.service.impl.FaceEngineServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.*;
import java.util.concurrent.Executors;

/**
 * @author yangyuyang
 */
@SpringBootApplication
@MapperScan("com.yang.face.mapper")
public class App extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(App.class);
    }

    public static void main(String[] args) {

        SpringApplication.run(App.class, args);

        //System.out.println(new FaceEngineServiceImpl());

//        HashSet hashSet;
//        LinkedHashSet linkedHashSet;
//        TreeSet treeSet;
//        LinkedList linkedList = new LinkedList();
//        linkedList.get(0);
//
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.get(0);
//        arrayList.remove(0);
//
//        Vector vector;
//        LinkedHashMap linkedHashMap;
//
//        Executors excutors;

    }

}
