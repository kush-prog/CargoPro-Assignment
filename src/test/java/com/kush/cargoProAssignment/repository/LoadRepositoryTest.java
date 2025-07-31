package com.kush.cargoProAssignment.repository;

import com.kush.cargoProAssignment.model.*;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LoadRepositoryTest {

    @Autowired
    private LoadRepository loadRepository;

    private Load load1;
    private Load load2;

    @BeforeEach
    void setUp() {
        Facility facility1 = new Facility();
        facility1.setLoadingPoint("Delhi");
        facility1.setUnloadingPoint("Mumbai");
        facility1.setLoadingDate(LocalDateTime.now().plusDays(1));
        facility1.setUnloadingDate(LocalDateTime.now().plusDays(3));

        Facility facility2 = new Facility();
        facility2.setLoadingPoint("Bangalore");
        facility2.setUnloadingPoint("Chennai");
        facility2.setLoadingDate(LocalDateTime.now().plusDays(2));
        facility2.setUnloadingDate(LocalDateTime.now().plusDays(4));

        load1 = new Load();
        load1.setShipperId("SHIPPER001");
        load1.setFacility(facility1);
        load1.setProductType("Electronics");
        load1.setTruckType("Container");
        load1.setNoOfTrucks(2);
        load1.setWeight(5000.0);
        load1.setComment("Fragile items");
        load1.setStatus(LoadStatus.POSTED);

        load2 = new Load();
        load2.setShipperId("SHIPPER002");
        load2.setFacility(facility2);
        load2.setProductType("Furniture");
        load2.setTruckType("Truck");
        load2.setNoOfTrucks(1);
        load2.setWeight(3000.0);
        load2.setComment("Heavy items");
        load2.setStatus(LoadStatus.BOOKED);

        loadRepository.save(load1);
        loadRepository.save(load2);
    }

    @Test
    void testFindByFilters_WithShipperId() {
        Page<Load> result = loadRepository.findByFilters("SHIPPER001", null, null, PageRequest.of(0, 10));
        assertEquals(1, result.getContent().size());
        assertEquals("SHIPPER001", result.getContent().get(0).getShipperId());
    }

    @Test
    void testFindByFilters_WithTruckType() {
        Page<Load> result = loadRepository.findByFilters(null, "Container", null, PageRequest.of(0, 10));
        assertEquals(1, result.getContent().size());
        assertEquals("Container", result.getContent().get(0).getTruckType());
    }

    @Test
    void testFindByFilters_WithStatus() {
        Page<Load> result = loadRepository.findByFilters(null, null, LoadStatus.POSTED, PageRequest.of(0, 10));
        assertEquals(1, result.getContent().size());
        assertEquals(LoadStatus.POSTED, result.getContent().get(0).getStatus());
    }

    @Test
    void testFindByFilters_WithAllFilters() {
        Page<Load> result = loadRepository.findByFilters("SHIPPER001", "Container", LoadStatus.POSTED, PageRequest.of(0, 10));
        assertEquals(1, result.getContent().size());
        Load load = result.getContent().get(0);
        assertEquals("SHIPPER001", load.getShipperId());
        assertEquals("Container", load.getTruckType());
        assertEquals(LoadStatus.POSTED, load.getStatus());
    }

    @Test
    void testFindByFilters_NoFilters() {
        Page<Load> result = loadRepository.findByFilters(null, null, null, PageRequest.of(0, 10));
        assertEquals(2, result.getContent().size());
    }
}