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
@Table(name = "User_Info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 8543251640066597652L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Column(name = "userId")
    private String userId;

    //@Column(name = "userName")
    private String userName;

    //@Column(name = "userType")
    private Integer userType;

    //@Column(name = "sex")
    private Integer sex;

    //@Column(name = "gradeId")
    private String gradeId;

    //@Column(name = "gradeName")
    private String gradeName;

    //@Column(name = "classId")
    private String classId;

    //@Column(name = "className")
    private String className;

    //@Column(name = "groupId")
    private String groupId;

    //@Column(name = "groupName")
    private String groupName;

    //@Column(name = "photoUrl")
    private String photoUrl;

    //@Column(name = "faceFeatureType")
    private Integer faceFeatureType;

    //@Column(name = "faceFeatureByte")
    private byte[] faceFeatureByte;

    //@Column(name = "faceFeatureFile")
    private String faceFeatureFile;

    //@Column(name = "score")
    private Integer score;

    //@Column(name = "createTime")
    private Date createTime;

    //@Column(name = "updateTime")
    private Date updateTime;

    public UserInfo(String userId) {
        this.userId = userId;
    }
}
