package com.yang.face.constant.enums;

/**
 * @author Yang
 */
public enum PhotoType {
    IMAGE(1, "图片URL地址"),

    BASE64(2, "图片Base64"),
    ;

    private PhotoType(int key, String name) {
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
        for (PhotoType e : PhotoType.values()) {
            if (e.getKey() == key) {
                return e.getName();
            }
        }

        return "";
    }
}
