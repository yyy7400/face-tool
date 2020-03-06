package com.yang.face.constant.enums;

/**
 * @author wanyifan
 * @date 2020/1/6 8:52
 */
public enum UserTypeEnum {

    ADMIN(0, "管理员"),

    TEACHER(1, "教师"),

    STUDENT(2, "学生"),

    OTHER(99, "未知"),

    ;

    UserTypeEnum(Integer key, String name) {
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
        for (UserTypeEnum value : UserTypeEnum.values()) {
            if (value.getKey().equals(key)) {
                return value.getName();
            }
        }
        return "";
    }

    public static Integer getKey(String name) {
        for (UserTypeEnum value : UserTypeEnum.values()) {
            if (value.getName().equals(name)){
                return value.getKey();
            }
        }
        return null;
    }

    Integer key;
    String name;
}
