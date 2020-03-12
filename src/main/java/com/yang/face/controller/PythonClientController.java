package com.yang.face.controller;

import com.yang.face.entity.show.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangyuyang
 * @date 2020/3/12 16:38
 */

@RestController
public class PythonClientController {

    @GetMapping("/PythonClietn/Features")
    public Response features(){

        return Response.show("");
    }
}
