package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangyuyang
 * @date 2020/3/23 10:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionFaceRecognitionImage {

    private String scheduleId;

    private String photo_id;

    private List<String> userId;

    private List<String> action_label;

    private String photo;
}
