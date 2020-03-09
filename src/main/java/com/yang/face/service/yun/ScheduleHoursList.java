package com.yang.face.service.yun;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleHoursList {
    private String error;

    @JsonProperty("data")
    private List<ScheduleHours> data;
}
