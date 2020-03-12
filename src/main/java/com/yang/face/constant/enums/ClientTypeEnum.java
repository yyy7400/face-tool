package com.yang.face.constant.enums;

/**
 *
 */
public enum ClientTypeEnum {
    PYTHON(1, "PYTHON"),

    OTHRER(2, "OTHER"),
            ;

    private ClientTypeEnum(int key, String name) {
        this.key = key;
        this.name = name;
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
        for (ClientTypeEnum e : ClientTypeEnum.values()) {
            if (e.getKey() == key) {
                return e.getName();
            }
        }

        return "";
    }
}
