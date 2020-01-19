package com.yang.face.engine;

import lombok.Data;

/**
 * @author yangyuyang
 */
@Data
public class FaceSearchResDto {
    private String faceId;
    private String name;
    private Integer similarValue;
    private Integer age;
    private String gender;
    private String image;


}
