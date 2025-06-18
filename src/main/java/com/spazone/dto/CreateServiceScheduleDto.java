// CreateServiceScheduleDto.java
package com.spazone.dto;

import java.time.LocalTime;

public class CreateServiceScheduleDto {
    private Integer serviceId;
    private Integer branchId;
    private Integer technicianId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive = true;

    public CreateServiceScheduleDto() {
    }


}
