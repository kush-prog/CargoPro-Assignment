package com.kush.cargoProAssignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.cargoProAssignment.controllers.LoadController;
import com.kush.cargoProAssignment.dto.FacilityDTO;
import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.service.LoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoadController.class)
class LoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoadService loadService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoadDTO loadDTO;
    private UUID loadId;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LoadService loadService() {
            return mock(LoadService.class);
        }
    }

    @BeforeEach
    void setUp() {
        loadId = UUID.randomUUID();
        loadDTO = new LoadDTO();
        loadDTO.setShipperId("shipper1");
        loadDTO.setProductType("Electronics");
        loadDTO.setTruckType("Container");
        loadDTO.setNoOfTrucks(1);
        loadDTO.setWeight(100.0);

        // Correctly initialize FacilityDTO to pass validation
        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setLoadingPoint("Point A");
        facilityDTO.setUnloadingPoint("Point B");
        facilityDTO.setLoadingDate(LocalDateTime.now());
        facilityDTO.setUnloadingDate(LocalDateTime.now().plusDays(1));
        loadDTO.setFacility(facilityDTO);

        reset(loadService);
    }

    @Test
    void createLoad_shouldReturnCreatedLoad_whenValidInput() throws Exception {
        when(loadService.createLoad(any(LoadDTO.class))).thenReturn(loadDTO);

        mockMvc.perform(post("/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipperId").value("shipper1"));
    }

    @Test
    void createLoad_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        LoadDTO invalidLoadDTO = new LoadDTO(); // Missing all required fields

        mockMvc.perform(post("/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoadDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.shipperId").exists());
    }

    @Test
    void getLoadById_shouldReturnLoad_whenLoadExists() throws Exception {
        when(loadService.getLoadById(loadId)).thenReturn(loadDTO);

        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipperId").value("shipper1"));
    }

    @Test
    void getLoadById_shouldReturnNotFound_whenLoadDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Load not found")).when(loadService).getLoadById(loadId);

        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Load not found"));
    }

    @Test
    void getLoads_shouldReturnPageOfLoads() throws Exception {
        when(loadService.getLoads(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.singletonList(loadDTO)));

        mockMvc.perform(get("/load")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void updateLoad_shouldReturnUpdatedLoad_whenValidInput() throws Exception {
        when(loadService.updateLoad(any(), any(LoadDTO.class))).thenReturn(loadDTO);

        mockMvc.perform(put("/load/{loadId}", loadId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipperId").value("shipper1"));
    }

    @Test
    void deleteLoad_shouldReturnNoContent() throws Exception {
        doNothing().when(loadService).deleteLoad(loadId);

        mockMvc.perform(delete("/load/{loadId}", loadId))
                .andExpect(status().isNoContent());
    }
}