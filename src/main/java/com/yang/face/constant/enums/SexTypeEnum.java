package com.yang.face.constant.enums;

/**
 * @author Yang
 */
public enum SexTypeEnum {
    /**
     * 未知
     */
    UNKNOWN(0, "保密"),
    /**
     * 男
     */
    MALE(1, "男"),
    /**
     * 女
     */
    FEMALE(2, "女");

    ;

    SexTypeEnum(Integer key, String name) {
        this.key = key;
        this.name = name;
    }

    public Integer getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static String getName(Integer key) {
        for (SexTypeEnum value : SexTypeEnum.values()) {
            if (value.getKey().equals(key)) {
                return value.getName();
            }
        }
        return "";
    }

    Integer key;
    String name;
}
