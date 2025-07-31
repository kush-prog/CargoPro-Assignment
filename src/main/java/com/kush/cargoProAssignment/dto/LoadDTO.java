package com.kush.cargoProAssignment.dto;

import com.kush.cargoProAssignment.model.enums.LoadStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoadDTO {
    private UUID id;

    @NotBlank(message = "Shipper ID is required")
    private String shipperId;

    @Valid
    @NotNull(message = "Facility is required")
    private FacilityDTO facility;

    @NotBlank(message = "Product type is required")
    private String productType;

    @NotBlank(message = "Truck type is required")
    private String truckType;

    @NotNull(message = "Number of trucks is required")
    @Positive(message = "Number of trucks must be positive")
    private Integer noOfTrucks;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    private String comment;
    private LocalDateTime datePosted;
    private LoadStatus status;
}
