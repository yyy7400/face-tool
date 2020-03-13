package com.yang.face.entity.show;

import com.yang.face.constant.enums.MessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanyifan
 * @date 2020/1/4 12:44
 */
@Data
@NoArgsConstructor
public class MessageVO {
    private Boolean state;
    private String msg;

    public MessageVO(MessageEnum messageEnum) {
        this.state = messageEnum.equals(MessageEnum.SUCCESS);
        this.msg = messageEnum.getName();
    }

    public MessageVO(MessageEnum messageEnum, Integer num) {
        this.state = messageEnum.equals(MessageEnum.SUCCESS);
        this.msg = messageEnum.getName() + ":" + num + "条数据";
    }

    public MessageVO(MessageEnum messageEnum, String message) {
        this.state = messageEnum.equals(MessageEnum.SUCCESS);
        this.msg = message;
    }

    public MessageVO(Boolean state, Integer num) {
        this.state = state;
        this.msg = state ? MessageEnum.SUCCESS.getName() : MessageEnum.FAIL.getName() + ":" + num + "条数据";
    }

    public MessageVO(Boolean state, String message) {
        this.state = state;
        this.msg = message;
    }
}
