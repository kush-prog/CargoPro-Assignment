package com.kush.cargoProAssignment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Facility {
    @NotBlank(message = "Loading point is required")
    @Column(name = "loading_point", nullable = false)
    private String loadingPoint;

    @NotBlank(message = "Unloading point is required")
    @Column(name = "unloading_point", nullable = false)
    private String unloadingPoint;

    @NotNull(message = "Loading date is required")
    @Column(name = "loading_date", nullable = false)
    private LocalDateTime loadingDate;

    @NotNull(message = "Unloading date is required")
    @Column(name = "unloading_date", nullable = false)
    private LocalDateTime unloadingDate;
}