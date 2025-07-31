package com.kush.cargoProAssignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.cargoProAssignment.controllers.LoadController;
import com.kush.cargoProAssignment.dto.FacilityDTO;
import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.service.LoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

@WebMvcTest(LoadController.class)
class LoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoadService loadService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoadDTO loadDTO;
    private UUID loadId;

    @BeforeEach
    void setUp() {
        loadId = UUID.randomUUID();

        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setLoadingPoint("Delhi");
        facilityDTO.setUnloadingPoint("Mumbai");
        facilityDTO.setLoadingDate(LocalDateTime.now().plusDays(1));
        facilityDTO.setUnloadingDate(LocalDateTime.now().plusDays(3));

        loadDTO = new LoadDTO();
        loadDTO.setId(loadId);
        loadDTO.setShipperId("SHIPPER001");
        loadDTO.setFacility(facilityDTO);
        loadDTO.setProductType("Electronics");
        loadDTO.setTruckType("Container");
        loadDTO.setNoOfTrucks(2);
        loadDTO.setWeight(5000.0);
        loadDTO.setComment("Fragile items");
        loadDTO.setStatus(LoadStatus.POSTED);
    }

    @Test
    void testCreateLoad_Success() throws Exception {
        when(loadService.createLoad(any(LoadDTO.class))).thenReturn(loadDTO);

        mockMvc.perform(post("/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipperId").value("SHIPPER001"))
                .andExpect(jsonPath("$.productType").value("Electronics"))
                .andExpect(jsonPath("$.status").value("POSTED"));
    }

    @Test
    void testCreateLoad_ValidationError() throws Exception {

        LoadDTO invalidLoad = new LoadDTO();

        mockMvc.perform(post("/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetLoads_Success() throws Exception {
        Page<LoadDTO> loadPage = new PageImpl<>(Arrays.asList(loadDTO));
        when(loadService.getLoads(eq("SHIPPER001"), eq("Container"), eq(LoadStatus.POSTED), eq(1), eq(10)))
                .thenReturn(loadPage);

        mockMvc.perform(get("/load")
                        .param("shipperId", "SHIPPER001")
                        .param("truckType", "Container")
                        .param("status", "POSTED")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].shipperId").value("SHIPPER001"));
    }

    @Test
    void testGetLoadById_Success() throws Exception {

        when(loadService.getLoadById(loadId)).thenReturn(loadDTO);

        mockMvc.perform(get("/load/{loadId}", loadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loadId.toString()))
                .andExpect(jsonPath("$.shipperId").value("SHIPPER001"));
    }

    @Test
    void testUpdateLoad_Success() throws Exception{
        when(loadService.updateLoad(eq(loadId), any(LoadDTO.class))).thenReturn(loadDTO);
        mockMvc.perform(put("/load/{loadId}", loadId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loadDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipperId").value("SHIPPER001"));
    }

    @Test
    void testDeleteLoad_Success() throws Exception {
        mockMvc.perform(delete("/load/{loadId}", loadId))
                .andExpect(status().isNoContent());
    }
}