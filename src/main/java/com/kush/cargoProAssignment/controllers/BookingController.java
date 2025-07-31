package com.kush.cargoProAssignment.controllers;

import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import com.kush.cargoProAssignment.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
@Tag(name = "Booking Management", description = "APIs for managing bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        BookingDTO createdBooking = bookingService.createBooking(bookingDTO);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get bookings with filters")
    public ResponseEntity<List<BookingDTO>> getBookings(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) String transporterId,
            @RequestParam(required = false) BookingStatus status) {

        List<BookingDTO> bookings = bookingService.getBookings(loadId, transporterId, status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable UUID bookingId) {
        BookingDTO booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{bookingId}")
    @Operation(summary = "Update booking details")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable UUID bookingId, @Valid @RequestBody BookingDTO bookingDTO) {
        BookingDTO updatedBooking = bookingService.updateBooking(bookingId, bookingDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Delete a booking")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
