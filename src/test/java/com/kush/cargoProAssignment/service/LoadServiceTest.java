package com.kush.cargoProAssignment.service;

import com.kush.cargoProAssignment.dto.FacilityDTO;
import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.model.Facility;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.repository.LoadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LoadService loadService;

    private UUID loadId;
    private Load load;
    private LoadDTO loadDTO;

    @BeforeEach
    void setUp() {
        loadId = UUID.randomUUID();

        Facility facility = new Facility();
        facility.setLoadingPoint("Point A");
        facility.setUnloadingPoint("Point B");
        facility.setLoadingDate(LocalDateTime.now());
        facility.setUnloadingDate(LocalDateTime.now().plusDays(1));

        load = new Load();
        load.setId(loadId);
        load.setShipperId("shipper123");
        load.setProductType("Electronics");
        load.setTruckType("Container");
        load.setNoOfTrucks(1);
        load.setWeight(100.0);
        load.setFacility(facility);
        load.setStatus(LoadStatus.POSTED);

        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setLoadingPoint("Point A");
        facilityDTO.setUnloadingPoint("Point B");
        facilityDTO.setLoadingDate(LocalDateTime.now());
        facilityDTO.setUnloadingDate(LocalDateTime.now().plusDays(1));

        loadDTO = new LoadDTO();
        loadDTO.setShipperId("shipper123");
        loadDTO.setProductType("Electronics");
        loadDTO.setTruckType("Container");
        loadDTO.setNoOfTrucks(1);
        loadDTO.setWeight(100.0);
        loadDTO.setFacility(facilityDTO);

        // Corrected ModelMapper mocking to return specific objects
        lenient().when(modelMapper.map(any(LoadDTO.class), eq(Load.class))).thenReturn(load);
        lenient().when(modelMapper.map(any(Load.class), eq(LoadDTO.class))).thenReturn(loadDTO);
        lenient().when(modelMapper.map(any(FacilityDTO.class), eq(Facility.class))).thenReturn(facility);
    }

    @Test
    void createLoad_shouldReturnCreatedLoadDTO_withPostedStatus() {
        // Given
        when(loadRepository.save(any(Load.class))).thenReturn(load);

        // When
        LoadDTO result = loadService.createLoad(loadDTO);

        // Then
        assertNotNull(result);
        assertEquals(LoadStatus.POSTED, load.getStatus());
        verify(loadRepository, times(1)).save(load);
    }

    @Test
    void getLoads_shouldReturnPageOfLoadDTOs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Load> loadPage = new PageImpl<>(Collections.singletonList(load));
        when(loadRepository.findByFilters(any(), any(), any(), any())).thenReturn(loadPage);

        // When
        Page<LoadDTO> result = loadService.getLoads("shipper123", null, null, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(loadDTO, result.getContent().get(0));
    }

    @Test
    void getLoadById_shouldReturnLoadDTO_whenLoadExists() {
        // Given
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));

        // When
        LoadDTO result = loadService.getLoadById(loadId);

        // Then
        assertNotNull(result);
        assertEquals(loadDTO, result);
    }

    @Test
    void getLoadById_shouldThrowException_whenLoadDoesNotExist() {
        // Given
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> loadService.getLoadById(loadId));
    }

    @Test
    void updateLoad_shouldReturnUpdatedLoadDTO() {
        // Given
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        when(loadRepository.save(any(Load.class))).thenReturn(load);

        LoadDTO updatedDto = new LoadDTO();
        updatedDto.setShipperId("newShipperId");

        // When
        LoadDTO result = loadService.updateLoad(loadId, updatedDto);

        // Then
        assertNotNull(result);
        verify(loadRepository, times(1)).save(load);
    }

    @Test
    void deleteLoad_shouldCallDelete_whenLoadExists() {
        // Given
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));

        // When
        loadService.deleteLoad(loadId);

        // Then
        verify(loadRepository, times(1)).delete(load);
    }

    @Test
    void deleteLoad_shouldThrowException_whenLoadDoesNotExist() {
        // Given
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> loadService.deleteLoad(loadId));
    }
}