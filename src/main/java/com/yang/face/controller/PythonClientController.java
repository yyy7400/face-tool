package com.yang.face.controller;

import com.yang.face.client.ClientManager;
import com.yang.face.constant.enums.ClientTypeEnum;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.entity.show.Response;
import com.yang.face.service.PythonApiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author yangyuyang
 * @date 2020/3/12 16:38
 */

@RestController
public class PythonClientController {

    @Resource
    PythonApiService pythonApiService;

    @GetMapping("/pythonClient/features")
    public Response features(){
        return Response.show(pythonApiService.getFeatureFiles());
    }

    @GetMapping("/pythonClient/heartBeat")
    public Response heartBeat(@Validated @NotNull String addr){

        if(addr.isEmpty()) {
            return Response.show(new MessageVO(false, "地址不能为空"));
        } else {
            ClientManager.put(addr, ClientTypeEnum.PYTHON.getKey());
            return Response.show(new MessageVO(true, ""));
        }

    }
}
