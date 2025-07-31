package com.kush.cargoProAssignment.repository;

import com.kush.cargoProAssignment.model.Booking;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("SELECT b FROM Booking b WHERE " +
            "(:loadId IS NULL OR b.load.id = :loadId) AND " +
            "(:transporterId IS NULL OR b.transporterId = :transporterId) AND " +
            "(:status IS NULL OR b.status = :status)")
    List<Booking> findByFilters(@Param("loadId") UUID loadId,
                                @Param("transporterId") String transporterId,
                                @Param("status") BookingStatus status);

    List<Booking> findByLoad(Load load);

    boolean existsByLoadAndStatus(Load load, BookingStatus status);
}
