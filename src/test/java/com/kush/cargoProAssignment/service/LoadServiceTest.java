package com.kush.cargoProAssignment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

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

import com.kush.cargoProAssignment.dto.FacilityDTO;
import com.kush.cargoProAssignment.dto.LoadDTO;
import com.kush.cargoProAssignment.model.Facility;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.repository.LoadRepository;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LoadService loadService;

    private LoadDTO loadDTO;
    private Load load;
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

        Facility facility = new Facility();
        facility.setLoadingPoint("Delhi");
        facility.setUnloadingPoint("Mumbai");
        facility.setLoadingDate(LocalDateTime.now().plusDays(1));
        facility.setUnloadingDate(LocalDateTime.now().plusDays(3));

        load = new Load();
        load.setId(loadId);
        load.setShipperId("SHIPPER001");
        load.setFacility(facility);
        load.setProductType("Electronics");
        load.setTruckType("Container");
        load.setNoOfTrucks(2);
        load.setWeight(5000.0);
        load.setComment("Fragile items");
        load.setStatus(LoadStatus.POSTED);
    }

    @Test
    void testCreateLoad_Success() {
        when(modelMapper.map(loadDTO, Load.class)).thenReturn(load);
        when(loadRepository.save(any(Load.class))).thenReturn(load);
        when(modelMapper.map(load, LoadDTO.class)).thenReturn(loadDTO);

        LoadDTO result = loadService.createLoad(loadDTO);
        assertNotNull(result);
        assertEquals(loadDTO.getShipperId(), result.getShipperId());
        assertEquals(LoadStatus.POSTED, result.getStatus());
        verify(loadRepository).save(any(Load.class));
        verify(modelMapper).map(loadDTO, Load.class);
        verify(modelMapper).map(load, LoadDTO.class);
    }

    @Test
    void testGetLoads_WithFilters_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Load> loadPage = new PageImpl<>(Arrays.asList(load));

        when(loadRepository.findByFilters("SHIPPER001", "Container", LoadStatus.POSTED, pageable))
                .thenReturn(loadPage);
        when(modelMapper.map(load, LoadDTO.class)).thenReturn(loadDTO);
        Page<LoadDTO> result = loadService.getLoads("SHIPPER001", "Container", LoadStatus.POSTED, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(loadDTO.getShipperId(), result.getContent().get(0).getShipperId());
        verify(loadRepository).findByFilters("SHIPPER001", "Container", LoadStatus.POSTED, pageable);
    }

    @Test
    void testGetLoadById_Success() {
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        when(modelMapper.map(load, LoadDTO.class)).thenReturn(loadDTO);

        LoadDTO result = loadService.getLoadById(loadId);
        assertNotNull(result);
        assertEquals(loadDTO.getId(), result.getId());
        assertEquals(loadDTO.getShipperId(), result.getShipperId());
        verify(loadRepository).findById(loadId);
        verify(modelMapper).map(load, LoadDTO.class);
    }

    @Test
    void testGetLoadById_NotFound() {
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loadService.getLoadById(loadId));
        verify(loadRepository).findById(loadId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdateLoad_Success() {
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        doNothing().when(modelMapper).map(loadDTO, load);
        when(loadRepository.save(load)).thenReturn(load);
        when(modelMapper.map(load, LoadDTO.class)).thenReturn(loadDTO);
        LoadDTO result = loadService.updateLoad(loadId, loadDTO);

        assertNotNull(result);
        assertEquals(loadDTO.getId(), result.getId());
        verify(loadRepository).findById(loadId);
        verify(loadRepository).save(load);
    }

    @Test
    void testDeleteLoad_Success() {
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        loadService.deleteLoad(loadId);
        verify(loadRepository).findById(loadId);
        verify(loadRepository).delete(load);
    }

    @Test
    void testUpdateLoadStatus_Success() {
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(load));
        when(loadRepository.save(load)).thenReturn(load);

        loadService.updateLoadStatus(loadId, LoadStatus.BOOKED);
        assertEquals(LoadStatus.BOOKED, load.getStatus());
        verify(loadRepository).findById(loadId);
        verify(loadRepository).save(load);
    }
}