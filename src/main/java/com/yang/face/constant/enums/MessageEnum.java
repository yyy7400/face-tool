package com.yang.face.constant.enums;

import lombok.Getter;

/**
 * @author wanyifan
 * @date 2020/1/4 12:55
 */
@Getter
public enum MessageEnum {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    FAIL(400, "操作失败"),
    ;

    MessageEnum(Integer key, String name) {
        this.key = key;
        this.name = name;
    }

    Integer key;
    String name;
}
