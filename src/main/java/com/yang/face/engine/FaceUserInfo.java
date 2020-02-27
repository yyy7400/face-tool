package com.yang.face.engine;


import lombok.Data;

import java.util.Date;

/**
 * @author yangyuyang
 */
@Data
public class FaceUserInfo {

    private Integer id;

    private String userId;

    private Integer userType;

    private String userName;

    private Integer sex;

    private Integer age;

    private String photoUrl;

    private byte[] faceFeature;

    private Date createTime;

    private Date updateTime;

    private Integer similarityScore;

}
