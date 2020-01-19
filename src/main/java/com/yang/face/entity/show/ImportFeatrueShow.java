package com.yang.face.entity.show;

import lombok.Data;

/**
 * @author yangyuyang
 */
@Data
public class ImportFeatrueShow {

    private String userId;

    private String userName;

    private Integer type;

    private String photo;

    private String photoName;

    private Boolean state;

    private String msg;
}
