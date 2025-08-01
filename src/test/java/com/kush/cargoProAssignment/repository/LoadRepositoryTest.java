package com.kush.cargoProAssignment.repository;

import com.kush.cargoProAssignment.model.Facility;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class LoadRepositoryTest {

    @Autowired
    private LoadRepository loadRepository;

    @BeforeEach
    void setUp() {
        loadRepository.deleteAll();
    }

    @Test
    void findByFilters_shouldReturnFilteredLoads() {
        Facility facility = new Facility();
        facility.setLoadingPoint("Point A");
        facility.setUnloadingPoint("Point B");
        facility.setLoadingDate(LocalDateTime.now());
        facility.setUnloadingDate(LocalDateTime.now().plusDays(1));

        Load load1 = new Load();
        load1.setShipperId("shipper1");
        load1.setTruckType("FLATBED");
        load1.setStatus(LoadStatus.POSTED);
        load1.setProductType("Electronics");
        load1.setWeight(100.0);
        load1.setNoOfTrucks(1);
        load1.setFacility(facility);
        loadRepository.save(load1);

        Load load2 = new Load();
        load2.setShipperId("shipper2");
        load2.setTruckType("TILT");
        load2.setStatus(LoadStatus.BOOKED);
        load2.setProductType("Furniture");
        load2.setWeight(200.0);
        load2.setNoOfTrucks(2);
        load2.setFacility(facility);
        loadRepository.save(load2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Load> result = loadRepository.findByFilters("shipper1", "FLATBED", LoadStatus.POSTED, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("shipper1", result.getContent().get(0).getShipperId());
    }

    @Test
    void findByFilters_shouldReturnAllLoads_whenNoFiltersAreProvided() {
        Facility facility = new Facility();
        facility.setLoadingPoint("Point A");
        facility.setUnloadingPoint("Point B");
        facility.setLoadingDate(LocalDateTime.now());
        facility.setUnloadingDate(LocalDateTime.now().plusDays(1));

        Load load1 = new Load();
        load1.setShipperId("shipper1");
        load1.setProductType("Electronics");
        load1.setTruckType("Container"); // Added the missing field
        load1.setWeight(100.0);
        load1.setNoOfTrucks(1);
        load1.setFacility(facility);
        loadRepository.save(load1);

        Load load2 = new Load();
        load2.setShipperId("shipper2");
        load2.setProductType("Furniture");
        load2.setTruckType("FLATBED"); // Added the missing field
        load2.setWeight(200.0);
        load2.setNoOfTrucks(2);
        load2.setFacility(facility);
        loadRepository.save(load2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Load> result = loadRepository.findByFilters(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
}