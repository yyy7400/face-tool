package com.yang.face.engine;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yangyuyang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceUserInfo {

    private Integer id;

    private String userId;

    private String userName;

    private Integer userType;

    private Integer sex;

    private String photoUrl;

    private byte[] faceFeature;

    private Date createTime;

    private Date updateTime;

    private Integer similarityScore;

}
