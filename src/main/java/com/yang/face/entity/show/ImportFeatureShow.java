package com.yang.face.entity.show;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportFeatureShow {
    private String userId;

    private String userName;

    private Integer type;

    private String photo;

    private String photoName;

    private Boolean state;

    private String msg;

    public ImportFeatureShow(String userName, String photo, String photoName) {
        super();
        this.userName = userName;
        this.photo = photo;
        this.photoName = photoName;
    }

    public ImportFeatureShow(String userId, String userName, String photo, String photoName) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.photo = photo;
        this.photoName = photoName;
    }

}

