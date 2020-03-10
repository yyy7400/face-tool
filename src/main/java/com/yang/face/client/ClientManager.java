package com.yang.face.client;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.yang.face.constant.enums.ClientTypeEnum;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * client 管理器
 */
public class ClientManager {

    private final static Map<String, ClientInfo> clientMap = new ConcurrentHashMap<>();

    public static void put(String key, ClientInfo value){
        clientMap.put(key,value);
    }

    public static void put(String addr, Integer type){
        put(addr, new ClientInfo(type, addr, DateUtil.date()));
    }

    public static ClientInfo get(String key){
        if(clientMap.containsKey(key)) {
            return clientMap.get(key);
        } else {
            return null;
        }
    }

    // 每秒检测一次, 10s 过期
    @Scheduled(cron = "1 * * * * ?")
    public void ClearExpiredClient() {
        clientMap.values().removeIf(v -> DateUtil.isExpired(v.getUpdateTime(), DateField.SECOND, 10, DateUtil.date()));
    }

    /**
     * 获取Python clients
     * @return
     */
    public static List<ClientInfo> getValuePython() {
        return clientMap.values().stream().filter(o -> o.getType().equals(ClientTypeEnum.PYTHON.getKey())).collect(Collectors.toList());
    }

    /**
     * 获取Python clients
     * @return
     */
    public static List<String> getKeyPython() {
        List<String> addrs = new ArrayList<>();
        clientMap.forEach((k, v) -> {
            if(v.getType().equals(ClientTypeEnum.PYTHON.getKey())) {
                addrs.add(k);
            }
        });

        return addrs;
    }



}
