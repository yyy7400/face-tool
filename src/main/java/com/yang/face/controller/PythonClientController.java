package com.yang.face.controller;

import com.yang.face.entity.show.Response;
import com.yang.face.service.PythonApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
