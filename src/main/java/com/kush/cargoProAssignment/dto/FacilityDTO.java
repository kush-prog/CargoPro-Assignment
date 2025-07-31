package com.kush.cargoProAssignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FacilityDTO {
    @NotBlank(message = "Loading point is required")
    private String loadingPoint;

    @NotBlank(message = "Unloading point is required")
    private String unloadingPoint;

    @NotNull(message = "Loading date is required")
    private LocalDateTime loadingDate;

    @NotNull(message = "Unloading date is required")
    private LocalDateTime unloadingDate;
}
