package com.yang.face.entity.show;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangyuyang
 */
@Data
public class FaceRecoShow implements Serializable {

    private static final long serialVersionUID = 4936042355799873954L;

    private String userId;

    private Integer similarityScore;

    private String headPhoto;

    public FaceRecoShow(String userId, Integer similarityScore, String photoUrl) {
        this.userId = userId;
        this.similarityScore = similarityScore;
        this.headPhoto = photoUrl;
    }
}
