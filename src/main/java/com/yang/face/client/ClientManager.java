package com.yang.face.client;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.yang.face.constant.enums.ClientTypeEnum;
import com.yang.face.entity.db.SystemSetting;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Yang
 * client 管理器
 */

@Component
public class ClientManager {

    private final static Map<String, ClientInfo> CLIENT_MAP = new ConcurrentHashMap<>();

    public static void put(String key, ClientInfo value){
        CLIENT_MAP.put(key,value);
    }

    public static void put(String addr, Integer type){
        put(addr, new ClientInfo(type, addr, DateUtil.date()));
    }

    public static ClientInfo get(String key){
        if(CLIENT_MAP.containsKey(key)) {
            return CLIENT_MAP.get(key);
        } else {
            return null;
        }
    }

    private static int count_show;
    public static void clearExpiredClient() {

        count_show ++;
        if(count_show % 60 == 0 && !CLIENT_MAP.isEmpty()) {
            System.out.println("clientPython:" + DateUtil.formatTime(DateUtil.date()));
            CLIENT_MAP.keySet().forEach(o -> System.out.println(o));
        }

        CLIENT_MAP.values().removeIf(v -> !DateUtil.isExpired(v.getUpdateTime(), DateField.SECOND, 10, DateUtil.date()));
    }

    /**
     * 获取Python clients
     * @return
     */
    public static List<ClientInfo> getValuePython() {
        return CLIENT_MAP.values().stream().filter(o -> o.getType().equals(ClientTypeEnum.PYTHON.getKey())).collect(Collectors.toList());
    }

    /**
     * 获取Python clients
     * @return
     */
    public static List<String> getKeyPython() {
        List<String> addrs = new ArrayList<>();
        CLIENT_MAP.forEach((k, v) -> {
            if(v.getType().equals(ClientTypeEnum.PYTHON.getKey())) {
                addrs.add(k);
            }
        });

        return addrs;
    }



}
