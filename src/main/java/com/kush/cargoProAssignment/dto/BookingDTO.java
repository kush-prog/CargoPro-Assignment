package com.kush.cargoProAssignment.dto;

import com.kush.cargoProAssignment.model.enums.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingDTO {
    private UUID id;

    @NotNull(message = "Load ID is required")
    private UUID loadId;

    @NotBlank(message = "Transporter ID is required")
    private String transporterId;

    @NotNull(message = "Proposed rate is required")
    @Positive(message = "Proposed rate must be positive")
    private Double proposedRate;

    private String comment;
    private BookingStatus status;
    private LocalDateTime requestedAt;
}