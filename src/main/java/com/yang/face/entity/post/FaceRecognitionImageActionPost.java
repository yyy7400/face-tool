package com.yang.face.entity.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangyuyang
 * @date 2020/3/23 14:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceRecognitionImageActionPost {
    private Integer type;
    private String photo;
    private String scheduleId;
    private List<String> userIds;
}
