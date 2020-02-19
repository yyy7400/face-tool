package com.yang.face.entity.post;

import lombok.Data;

import java.util.List;

/**
 * @author yangyuyang
 */
@Data
public class RecoImageWithUserPost {
    private Integer type;
    private String photo;
    private List<String> userIds;
}
