package com.yang.face.service.impl;

import com.yang.face.client.ClientInfo;
import com.yang.face.client.ClientManager;
import com.yang.face.service.PythonApiService;

import java.util.List;

public class PythonApiServiceImpl implements PythonApiService {

    private static volatile int addrPollingIndex = 0;

    @Override
    public String getAddrByPolling() {

        String addr = "";
        List<String> addrs = ClientManager.getKeyPython();
        if(addrs.isEmpty()) {
            return addr;
        }

        if(addrPollingIndex >= addrs.size()) {
            addrPollingIndex = 0;
        }
        addr = addrs.get(addrPollingIndex);
        addrPollingIndex ++;

        return null;
    }
}
