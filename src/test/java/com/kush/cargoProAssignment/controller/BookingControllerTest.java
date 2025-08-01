package com.kush.cargoProAssignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.cargoProAssignment.controllers.BookingController;
import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService; // Now injected from the TestConfiguration

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDTO bookingDTO;
    private UUID bookingId;
    private UUID loadId;

    // This nested class will provide the mock bean for our test context.
    @TestConfiguration
    static class TestConfig {
        @Bean
        public BookingService bookingService() {
            return mock(BookingService.class);
        }
    }

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        loadId = UUID.randomUUID();
        bookingDTO = new BookingDTO();
        bookingDTO.setLoadId(loadId);
        bookingDTO.setTransporterId("transporter123");
        bookingDTO.setProposedRate(1000.0);

        // Reset the mock before each test
        reset(bookingService);
    }

    @Test
    void createBooking_shouldReturnCreatedBooking_whenValidInput() throws Exception {
        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(bookingDTO);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transporterId").value("transporter123"));
    }

    @Test
    void getBookingById_shouldReturnBooking_whenBookingExists() throws Exception {
        when(bookingService.getBookingById(bookingId)).thenReturn(bookingDTO);

        mockMvc.perform(get("/booking/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transporterId").value("transporter123"));
    }

    @Test
    void getBookingById_shouldReturnNotFound_whenBookingDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Booking not found")).when(bookingService).getBookingById(bookingId);

        mockMvc.perform(get("/booking/{bookingId}", bookingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Booking not found"));
    }

    @Test
    void getBookings_shouldReturnListOfBookings() throws Exception {
        when(bookingService.getBookings(any(), any(), any()))
                .thenReturn(Collections.singletonList(bookingDTO));

        mockMvc.perform(get("/booking")
                        .param("loadId", loadId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transporterId").value("transporter123"));
    }

    @Test
    void updateBooking_shouldReturnUpdatedBooking_whenValidInput() throws Exception {
        when(bookingService.updateBooking(any(), any(BookingDTO.class))).thenReturn(bookingDTO);

        mockMvc.perform(put("/booking/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transporterId").value("transporter123"));
    }

    @Test
    void deleteBooking_shouldReturnNoContent() throws Exception {
        doNothing().when(bookingService).deleteBooking(bookingId);

        mockMvc.perform(delete("/booking/{bookingId}", bookingId))
                .andExpect(status().isNoContent());
    }
}