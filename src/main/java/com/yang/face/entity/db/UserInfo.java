package com.yang.face.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yangyuyang
 */
@Data
@NoArgsConstructor()
@AllArgsConstructor()
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 8543251640066597652L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "userType")
    private Integer userType;

    @Column(name = "userName")
    private String userName;

    @Column(name = "sex")
    private Integer sex;

    @Column(name = "age")
    private Integer age;

    @Column(name = "photoUrl")
    private String photoUrl;

    @Column(name = "faceFeature")
    private byte[] faceFeature;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "updateTime")
    private Date updateTime;

    public UserInfo(String userId, Integer userType, String userName, Integer sex, Integer age, String photoUrl, byte[] faceFeature, Date createTime, Date updateTime) {
        this.userId = userId;
        this.userType = userType;
        this.userName = userName;
        this.sex = sex;
        this.age = age;
        this.photoUrl = photoUrl;
        this.faceFeature = faceFeature;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
