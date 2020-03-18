package com.yang.face.entity.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author yangyuyang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportFeaturePost {

    private String userId;

    private Integer type;

    private String photo;
}
