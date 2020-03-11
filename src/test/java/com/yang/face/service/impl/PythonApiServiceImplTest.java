package com.yang.face.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Map;

class PythonApiServiceImplTest {

    @Test
    void getFaceFeature() {

        Map<String, String> map = new PythonApiServiceImpl().getFaceFeature("S00001", 1,"http://192.168.129.142:9902/S00002.png");
        Assert.isTrue(!map.isEmpty(), "updateFaceFeature");
    }

    //@Test
    void updateFaceFeature() {
        Boolean state = new PythonApiServiceImpl().updateFaceFeature();
        Assert.isTrue(state, "updateFaceFeature");
    }
}