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

    public ImportFeatrueShow(String userId, String userName, Integer type, String photo, String photoName, Boolean state, String msg) {
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.photo = photo;
        this.photoName = photoName;
        this.state = state;
        this.msg = msg;
    }
}
