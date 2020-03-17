package com.yang.face.entity.show;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yangyuyang
 * @date 2020/3/17 11:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubStudentFeatureShow {

    private String userId;

    private String userName;

    private String userPhotoYun;

    private Integer sex;

    private String gradeId;

    private String gradeName;

    private String classId;

    private String className;

    private String photoIconUrl;

    private String photoUrl;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date photoTime;
}
