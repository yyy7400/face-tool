package com.yang.face.constant.enums;

/**
 * @author Yang
 * 图片类型
 */
public enum PhotoTypeEnum {

    IMAGE(1, "图片URL地址"),

    BASE64(2, "图片Base64"),

    //智慧考试
    FTPURL_TEST(3, "FTP地址"),

    //智慧考试
    LOCAL(4, "本地绝对路径"),
    ;

    PhotoTypeEnum(int key, String name) {
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
        for(PhotoTypeEnum e : PhotoTypeEnum.values()) {
            if(e.getKey() == key) {
                return e.getName();
            }
        }

        return "";
    }
}
