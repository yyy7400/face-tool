package com.yang.face.controller;

import com.yang.face.constant.enums.ClientTypeEnum;
import com.yang.face.constant.enums.MessageEnum;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.entity.show.Response;
import com.yang.face.client.ClientManager;
import com.yang.face.service.SystemSettingService;
import com.yang.face.service.impl.UserInfoServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Yang
 * 客户端接口
 */
@RestController
public class ClientController {

    @Resource
    private SystemSettingService systemSettingService;

    @Resource
    private UserInfoServiceImpl userInfoService;

    @GetMapping("/client/heartBeat")
    public Response heartBeat(@RequestParam(defaultValue = "0") Integer type, @NotNull String addr) {

        if (type.equals(ClientTypeEnum.PYTHON.getKey())) {
            ClientManager.put(addr, type);
            return Response.show(new MessageVO(MessageEnum.SUCCESS));
        }

        return Response.show(new MessageVO(MessageEnum.FAIL));

    }
}
