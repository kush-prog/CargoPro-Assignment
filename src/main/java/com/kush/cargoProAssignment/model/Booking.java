package com.kush.cargoProAssignment.model;

import com.kush.cargoProAssignment.model.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false)
    @NotNull(message = "Load is required")
    private Load load;

    @NotBlank(message = "Transporter ID is required")
    @Column(name = "transporter_id", nullable = false)
    private String transporterId;

    @NotNull(message = "Proposed rate is required")
    @Positive(message = "Proposed rate must be positive")
    @Column(name = "proposed_rate", nullable = false)
    private Double proposedRate;

    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

}