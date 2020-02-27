package com.yang.face.entity.post;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author yangyuyang
 */
@Data
public class RecoImageWithUserPost {
    @Min(value = 1)
    private Integer type;
    private String photo;
    private List<String> userIds;
}
