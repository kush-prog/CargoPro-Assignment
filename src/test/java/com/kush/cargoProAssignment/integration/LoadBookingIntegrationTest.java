package com.kush.cargoProAssignment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.cargoProAssignment.dto.FacilityDTO;
import com.kush.cargoProAssignment.dto.LoadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
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
    void testCompleteLoadBookingFlow() throws Exception {
        // Create a load
        String loadResponse = mockMvc.perform(post("/load")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("POSTED"))
                .andReturn().getResponse().getContentAsString();

        LoadDTO createdLoad = objectMapper.readValue(loadResponse, LoadDTO.class);
        String loadId = createdLoad.getId().toString();

        // Get the created load
        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipperId").value("SHIPPER001"));

        // Get loads with filters
        mockMvc.perform(get("/load")
                .param("shipperId", "SHIPPER001")
                .param("status", "POSTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].shipperId").value("SHIPPER001"));

        // Update the load
        loadDTO.setComment("Updated comment");
        mockMvc.perform(put("/load/{loadId}", loadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated comment"));

        // Delete the load
        mockMvc.perform(delete("/load/{loadId}", loadId))
                .andExpect(status().isNoContent());

        // Verify load is deleted
        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isNotFound());
    }
}