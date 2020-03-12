package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangyuyang
 * @date 2020/3/12 10:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceRecognitionImage {

    private String userId;

    private Double similarityScore;

    private Integer x1;

    private Integer x2;

    private Integer y1;

    private Integer y2;

    private String headPhoto;

    private Boolean state;

}
