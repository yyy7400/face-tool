package com.yang.face.entity.show;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yangyuyang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceRecoShow implements Serializable {

    private static final long serialVersionUID = 4936042355799873954L;

    private String userId;

    private Integer similarityScore;

    private String headPhoto;

    private Boolean state;

}
