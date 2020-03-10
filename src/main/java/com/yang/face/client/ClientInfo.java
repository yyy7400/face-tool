package com.yang.face.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfo {

    private Integer type;

    private String addr;

    private Date updateTime;

}
