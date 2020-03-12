package com.yang.face.controller;

import com.yang.face.constant.enums.ClientTypeEnum;
import com.yang.face.constant.enums.MessageEnum;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.entity.show.Response;
import com.yang.face.client.ClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author Yang
 * 客户端接口
 */
public class ClientController {

    @GetMapping("/client/heartBeat")
    public Response heartBeat(@RequestParam(defaultValue = "0") Integer type, @NotNull String addr) {

        if (type.equals(ClientTypeEnum.PYTHON.getKey())) {
            ClientManager.put(addr, type);
            return Response.show(new MessageVO(MessageEnum.SUCCESS));
        }

        return Response.show(new MessageVO(MessageEnum.FAIL));

    }
}
