package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 特征信息，用于client同步
 * @author yangyuyang
 * @date 2020/3/12 16:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureFileInfo {
    private String userId;
    private String fileUrl;
    private Date updateTime;
    private String md5;
}
