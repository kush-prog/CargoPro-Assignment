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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private LoadRepository loadRepository;

    private Load load1;
    private Load load2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        loadRepository.deleteAll();

        Facility facility = new Facility();
        facility.setLoadingPoint("Point A");
        facility.setUnloadingPoint("Point B");
        facility.setLoadingDate(LocalDateTime.now());
        facility.setUnloadingDate(LocalDateTime.now().plusDays(1));

        load1 = new Load();
        load1.setShipperId("shipper1");
        load1.setProductType("ProductA");
        load1.setTruckType("TruckA");
        load1.setWeight(100.0);
        load1.setNoOfTrucks(1);
        load1.setStatus(LoadStatus.POSTED);
        load1.setFacility(facility);
        load1 = loadRepository.save(load1);

        load2 = new Load();
        load2.setShipperId("shipper2");
        load2.setProductType("ProductB");
        load2.setTruckType("TruckB");
        load2.setWeight(200.0);
        load2.setNoOfTrucks(2);
        load2.setStatus(LoadStatus.BOOKED);
        load2.setFacility(facility);
        load2 = loadRepository.save(load2);

        booking1 = new Booking();
        booking1.setLoad(load1);
        booking1.setTransporterId("transporter1");
        booking1.setProposedRate(500.0);
        booking1.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking1);

        booking2 = new Booking();
        booking2.setLoad(load2);
        booking2.setTransporterId("transporter2");
        booking2.setProposedRate(750.0);
        booking2.setStatus(BookingStatus.ACCEPTED);
        bookingRepository.save(booking2);
    }

    @Test
    void findByFilters_shouldReturnFilteredBookings() {
        List<Booking> result = bookingRepository.findByFilters(load1.getId(), "transporter1", BookingStatus.PENDING);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals("transporter1", result.get(0).getTransporterId());
    }

    @Test
    void findByFilters_shouldReturnAllBookings_whenNoFiltersProvided() {
        List<Booking> result = bookingRepository.findByFilters(null, null, null);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByLoad_shouldReturnBookingsForSpecificLoad() {
        List<Booking> result = bookingRepository.findByLoad(load1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
    }

    @Test
    void existsByLoadAndStatus_shouldReturnTrue_whenBookingExists() {
        boolean exists = bookingRepository.existsByLoadAndStatus(load2, BookingStatus.ACCEPTED);
        assertTrue(exists);
    }

    @Test
    void existsByLoadAndStatus_shouldReturnFalse_whenBookingDoesNotExist() {
        boolean exists = bookingRepository.existsByLoadAndStatus(load1, BookingStatus.ACCEPTED);
        assertFalse(exists);
    }
}