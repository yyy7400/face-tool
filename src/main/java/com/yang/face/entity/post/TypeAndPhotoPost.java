package com.yang.face.entity.post;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * @author yangyuyang
 */
@Data
public class TypeAndPhotoPost {
    @Min(value = 1)
    private int type;

    @Size(min = 1)
    private String photo;
}
