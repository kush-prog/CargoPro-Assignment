package com.kush.cargoProAssignment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.dto.FacilityDTO;
import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoadBookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private LoadDTO loadDTO;

    @BeforeEach
    void setUp() {
        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setLoadingPoint("Delhi");
        facilityDTO.setUnloadingPoint("Mumbai");
        facilityDTO.setLoadingDate(LocalDateTime.now().plusDays(1));
        facilityDTO.setUnloadingDate(LocalDateTime.now().plusDays(3));

        loadDTO = new LoadDTO();
        loadDTO.setShipperId("SHIPPER001");
        loadDTO.setFacility(facilityDTO);
        loadDTO.setProductType("Electronics");
        loadDTO.setTruckType("Container");
        loadDTO.setNoOfTrucks(2);
        loadDTO.setWeight(5000.0);
        loadDTO.setComment("Fragile items");
    }

    @Test
    void testFullLoadBookingFlow() throws Exception {
        // 1. Create a Load
        String loadResponse = mockMvc.perform(post("/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("POSTED"))
                .andReturn().getResponse().getContentAsString();

        LoadDTO createdLoad = objectMapper.readValue(loadResponse, LoadDTO.class);
        UUID loadId = createdLoad.getId();

        // 2. Create a Booking for the Load
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setLoadId(loadId);
        bookingDTO.setTransporterId("TRANSPORTER001");
        bookingDTO.setProposedRate(1200.0);
        String bookingResponse = mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        BookingDTO createdBooking = objectMapper.readValue(bookingResponse, BookingDTO.class);
        UUID bookingId = createdBooking.getId();

        // Check the updated load status by fetching it again
        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"));

        // 3. Update the Booking to ACCEPTED
        createdBooking.setStatus(BookingStatus.ACCEPTED);
        mockMvc.perform(put("/booking/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // Verify Load status remains BOOKED
        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"));

        // 4. Create another booking for the same load
        BookingDTO anotherBookingDTO = new BookingDTO();
        anotherBookingDTO.setLoadId(loadId);
        anotherBookingDTO.setTransporterId("TRANSPORTER002");
        anotherBookingDTO.setProposedRate(1100.0);
        String anotherBookingResponse = mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(anotherBookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        BookingDTO anotherBooking = objectMapper.readValue(anotherBookingResponse, BookingDTO.class);
        UUID anotherBookingId = anotherBooking.getId();

        // 5. Delete the first booking
        mockMvc.perform(delete("/booking/{bookingId}", bookingId))
                .andExpect(status().isNoContent());

        // Verify Load status remains BOOKED because another booking still exists
        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("POSTED"));

        // 6. Delete the last remaining booking
        mockMvc.perform(delete("/booking/{bookingId}", anotherBookingId))
                .andExpect(status().isNoContent());

        // Verify that the load's status is reverted to CANCELLED since it was the last booking
        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}