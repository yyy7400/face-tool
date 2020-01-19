package com.yang.face.entity.db;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yangyuyang
 */
@Data
@Entity
public class UserInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userId;

    private Integer userType;

    private String userName;

    private Boolean sex;

    private Integer age;

    private String photoUrl;

    private byte[] faceFeature;

    private Date createTime;

    private Date updateTime;
}
