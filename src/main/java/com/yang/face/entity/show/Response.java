package com.yang.face.entity.show;

import com.yang.face.constant.enums.StatusType;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Yang
 */
@Data
public class Response {
    private String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    private int status;
    private String msg;
    private Object data;

    public static Response show(List<?> list) {
        Response response = new Response();
        response.msg = "";
        response.data = new ArrayList<>();
        if (list == null) {
            response.status = StatusType.FAILED_OPERATE.getKey();
            response.msg = StatusType.FAILED_OPERATE.getName();
        } else if (list.isEmpty()) {
            response.status = StatusType.NO_DATA.getKey();
            response.msg = StatusType.NO_DATA.getName();
            response.data = list;
        } else {
            response.status = StatusType.NORMAL.getKey();
            response.data = list;
        }
        return response;
    }

    public static Response show(Object o) {
        Response response = new Response();
        response.msg = "";
        if (o == null) {
            response.status = StatusType.FAILED_OPERATE.getKey();
            response.msg = StatusType.FAILED_OPERATE.getName();
            response.data = "";
        } else if (o instanceof List<?> && ((List<?>) o).isEmpty()) {
            response.status = StatusType.NO_DATA.getKey();
            response.msg = StatusType.NO_DATA.getName();
            response.data = o;
        } else {
            response.data = o;
            response.status = StatusType.NORMAL.getKey();
        }
        return response;
    }

    public static Response show(Object o, Boolean canBeNull) {
        Response response = new Response();
        response.msg = "";
        if (o == null) {
            if (Boolean.TRUE.equals(canBeNull)) {
                response.status = StatusType.NO_DATA.getKey();
                response.msg = StatusType.NO_DATA.getName();
            } else {
                response.status = StatusType.FAILED_OPERATE.getKey();
                response.msg = StatusType.FAILED_OPERATE.getName();
            }
            response.data = "";
        } else {
            response.data = o;
            response.status = StatusType.NORMAL.getKey();
        }
        return response;
    }

    public static Response show(int i) {
        Response response = new Response();
        response.msg = "";
        if (i <= 0) {
            response.status = StatusType.FAILED_OPERATE.getKey();
            response.msg = StatusType.FAILED_OPERATE.getName();
            response.data = i;
        } else {
            response.status = StatusType.NORMAL.getKey();
            response.data = i;
        }
        return response;
    }
}
