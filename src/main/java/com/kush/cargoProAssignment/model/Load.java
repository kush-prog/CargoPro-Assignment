package com.kush.cargoProAssignment.model;

import com.kush.cargoProAssignment.model.enums.LoadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loads")
public class Load {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Shipper ID is required")
    @Column(name = "shipper_id", nullable = false)
    private String shipperId;

    @Embedded
    private Facility facility;

    @NotBlank(message = "Product type is required")
    @Column(name = "product_type", nullable = false)
    private String productType;

    @NotBlank(message = "Truck type is required")
    @Column(name = "truck_type", nullable = false)
    private String truckType;

    @NotNull(message = "Number of trucks is required")
    @Positive(message = "Number of trucks must be positive")
    @Column(name = "no_of_trucks", nullable = false)
    private Integer noOfTrucks;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Column(nullable = false)
    private Double weight;

    private String comment;

    @CreationTimestamp
    @Column(name = "date_posted", nullable = false, updatable = false)
    private LocalDateTime datePosted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoadStatus status = LoadStatus.POSTED;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
