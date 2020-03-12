package com.yang.face.service.yun;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Yang
 */
@Data
public class ScheduleHours {

    /**
     * 学校 ID
     */
    @JsonProperty("SchoolID")
    private String SchoolID;

    /**
     * 课时编号
     */
    @JsonProperty("ClassHourID")
    private String ClassHourID;

    /**
     * 课时名称
     */
    @JsonProperty("ClassHourName")
    private String ClassHourName;

    /**
     * 上课时间
     */
    @JsonProperty("StartTime")
    private String StartTime;

    /**
     * 下课时间
     */
    @JsonProperty("EndTime")
    private String EndTime;
}
