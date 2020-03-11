package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * python api 首层包装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PythonResponse implements Serializable {
    private static final long serialVersionUID = -5542718673659312628L;

    private Date timestamp;
    private int status;
    private String msg;
    private Object data;

}
