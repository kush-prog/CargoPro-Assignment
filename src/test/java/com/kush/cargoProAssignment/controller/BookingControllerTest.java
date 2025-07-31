package com.kush.cargoProAssignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.cargoProAssignment.controllers.BookingController;
import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import com.kush.cargoProAssignment.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDTO bookingDTO;
    private UUID bookingId;
    private UUID loadId;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        loadId = UUID.randomUUID();

        bookingDTO = new BookingDTO();
        bookingDTO.setId(bookingId);
        bookingDTO.setLoadId(loadId);
        bookingDTO.setTransporterId("TRANSPORTER001");
        bookingDTO.setProposedRate(50000.0);
        bookingDTO.setComment("Interested in this load");
        bookingDTO.setStatus(BookingStatus.PENDING);
        bookingDTO.setRequestedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(bookingDTO);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transporterId").value("TRANSPORTER001"))
                .andExpect(jsonPath("$.proposedRate").value(50000.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateBooking_ValidationError() throws Exception {

        BookingDTO invalidBooking = new BookingDTO();

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookings_Success() throws Exception {

        when(bookingService.getBookings(eq(loadId), eq("TRANSPORTER001"), eq(BookingStatus.PENDING)))
                .thenReturn(Arrays.asList(bookingDTO));

        mockMvc.perform(get("/booking")
                        .param("loadId", loadId.toString())
                        .param("transporterId", "TRANSPORTER001")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transporterId").value("TRANSPORTER001"));
    }

    @Test
    void testGetBookingById_Success() throws Exception {

        when(bookingService.getBookingById(bookingId)).thenReturn(bookingDTO);

        mockMvc.perform(get("/booking/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId.toString()))
                .andExpect(jsonPath("$.transporterId").value("TRANSPORTER001"));
    }

    @Test
    void testUpdateBooking_Success() throws Exception {

        when(bookingService.updateBooking(eq(bookingId), any(BookingDTO.class))).thenReturn(bookingDTO);

        mockMvc.perform(put("/booking/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transporterId").value("TRANSPORTER001"));
    }

    @Test
    void testDeleteBooking_Success() throws Exception {

        mockMvc.perform(delete("/booking/{bookingId}", bookingId))
                .andExpect(status().isNoContent());
    }
}