package com.yang.face.constant.enums;

public enum FaceFeatureTypeEnum {

    /**
     * 虹软
     */
    NONE(0, "无"),

    /**
     * 虹软
     */
    ARC_SOFT(1, "虹软"),
    /**
     * 男
     */
    OPENVINO(2, "Intel OpenVINO");

    ;

    FaceFeatureTypeEnum(Integer key, String name) {
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
        for (FaceFeatureTypeEnum value : FaceFeatureTypeEnum.values()) {
            if (value.getKey().equals(key)) {
                return value.getName();
            }
        }
        return "";
    }

    Integer key;
    String name;
}
