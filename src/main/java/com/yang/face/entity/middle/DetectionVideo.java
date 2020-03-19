package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yangyuyang
 * @date 2020/3/19 10:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionVideo {

    private String addr;
    private String videoUrlRtsp;
    private String videoUrlRtmp;
    private Date time;
}
