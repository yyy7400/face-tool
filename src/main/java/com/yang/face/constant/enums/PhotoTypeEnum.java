package com.yang.face.constant.enums;

/**
 * 图片类型
 */
public enum PhotoTypeEnum {

    IMAGE(1, "图片URL地址"),

    BASE64(2, "图片Base64"),
    ;

    private PhotoTypeEnum(int _key, String _name) {
        key = _key;
        name= _name;
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
        for(PhotoTypeEnum e : PhotoTypeEnum.values()) {
            if(e.getKey() == key)
                return e.getName();
        }

        return "";
    }
}
