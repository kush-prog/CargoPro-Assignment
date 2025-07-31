package com.kush.cargoProAssignment.repository;

import com.kush.cargoProAssignment.model.Booking;
import com.kush.cargoProAssignment.model.Facility;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LoadRepository loadRepository;

    private Load load;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility();
        facility.setLoadingPoint("Delhi");
        facility.setUnloadingPoint("Mumbai");
        facility.setLoadingDate(LocalDateTime.now().plusDays(1));
        facility.setUnloadingDate(LocalDateTime.now().plusDays(3));

        load = new Load();
        load.setShipperId("SHIPPER001");
        load.setFacility(facility);
        load.setProductType("Electronics");
        load.setTruckType("Container");
        load.setNoOfTrucks(2);
        load.setWeight(5000.0);
        load.setStatus(LoadStatus.POSTED);
        load = loadRepository.save(load);

        booking1 = new Booking();
        booking1.setLoad(load);
        booking1.setTransporterId("TRANSPORTER001");
        booking1.setProposedRate(50000.0);
        booking1.setComment("Interested");
        booking1.setStatus(BookingStatus.PENDING);

        booking2 = new Booking();
        booking2.setLoad(load);
        booking2.setTransporterId("TRANSPORTER002");
        booking2.setProposedRate(55000.0);
        booking2.setComment("Better rate");
        booking2.setStatus(BookingStatus.ACCEPTED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    @Test
    void testFindByFilters_WithLoadId() {
        List<Booking> result = bookingRepository.findByFilters(load.getId(), null, null);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByFilters_WithTransporterId() {
        List<Booking> result = bookingRepository.findByFilters(null, "TRANSPORTER001", null);
        assertEquals(1, result.size());
        assertEquals("TRANSPORTER001", result.get(0).getTransporterId());
    }

    @Test
    void testFindByFilters_WithStatus() {
        List<Booking> result = bookingRepository.findByFilters(null, null, BookingStatus.ACCEPTED);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.ACCEPTED, result.get(0).getStatus());
    }

    @Test
    void testFindByLoad() {
        List<Booking> result = bookingRepository.findByLoad(load);
        assertEquals(2, result.size());
    }

    @Test
    void testExistsByLoadAndStatus() {
        boolean exists = bookingRepository.existsByLoadAndStatus(load, BookingStatus.ACCEPTED);
        assertTrue(exists);
    }

    @Test
    void testExistsByLoadAndStatus_NotExists() {
        boolean exists = bookingRepository.existsByLoadAndStatus(load, BookingStatus.REJECTED);
        assertFalse(exists);
    }
}
