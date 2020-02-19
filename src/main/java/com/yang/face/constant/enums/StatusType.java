package com.yang.face.constant.enums;

/**
 * @author yangyuyang
 * @date 2018-11-02
 * web api 状态码
 */
public enum StatusType {
    //"正常"
    NORMAL(0, "正常"),

    //"无数据"
    NO_DATA(1, "无数据"),

    //"非法安全码"
    ILLEGAL_SECURE_CODE(2, "非法安全码"),

    //"非法参数"
    ILLEGAL_PARAMETER(3, "非法参数"),

    //"操作失败"
    FAILED_OPERATE(4, "操作失败");

    StatusType(int _key, String _name) {
        key = _key;
        name = _name;
    }

    int key;
    String name;

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static String getName(int key) {
        for (StatusType e : StatusType.values()) {
            if (e.getKey() == key) {
                return e.getName();
            }
        }

        return "";
    }
}
