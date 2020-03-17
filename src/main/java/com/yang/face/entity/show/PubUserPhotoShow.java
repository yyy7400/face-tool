package com.yang.face.entity.show;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yangyuyang
 * @date 2020/3/17 11:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubUserPhotoShow {

    private String userId;

    private String photoIconUrl;

    private String photoUrl;

    private Date photoTime;
}
