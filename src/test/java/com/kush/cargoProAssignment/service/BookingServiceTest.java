package com.kush.cargoProAssignment.service;

import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.model.Booking;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.exceptions.BusinessException;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private LoadService loadService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookingService bookingService;

    private BookingDTO bookingDTO;
    private Booking booking;
    private Load load;
    private UUID bookingId;
    private UUID loadId;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        loadId = UUID.randomUUID();

        // Setup Load
        load = new Load();
        load.setId(loadId);
        load.setStatus(LoadStatus.POSTED);

        // Setup BookingDTO
        bookingDTO = new BookingDTO();
        bookingDTO.setId(bookingId);
        bookingDTO.setLoadId(loadId);
        bookingDTO.setTransporterId("TRANSPORTER001");
        bookingDTO.setProposedRate(50000.0);
        bookingDTO.setComment("Interested in this load");
        bookingDTO.setStatus(BookingStatus.PENDING);

        // Setup Booking entity
        booking = new Booking();
        booking.setId(bookingId);
        booking.setLoad(load);
        booking.setTransporterId("TRANSPORTER001");
        booking.setProposedRate(50000.0);
        booking.setComment("Interested in this load");
        booking.setStatus(BookingStatus.PENDING);
        booking.setRequestedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBooking_Success() {
        // Given
        when(loadService.findEntityById(loadId)).thenReturn(load);
        when(modelMapper.map(bookingDTO, Booking.class)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(bookingDTO);

        // Then
        assertNotNull(result);
        assertEquals(bookingDTO.getTransporterId(), result.getTransporterId());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(loadService).findEntityById(loadId);
        verify(loadService).updateLoadStatus(loadId, LoadStatus.BOOKED);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_CancelledLoad_ThrowsException() {
        // Given
        load.setStatus(LoadStatus.CANCELLED);
        when(loadService.findEntityById(loadId)).thenReturn(load);

        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.createBooking(bookingDTO));
        verify(loadService).findEntityById(loadId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void testGetBookings_WithFilters_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByFilters(loadId, "TRANSPORTER001", BookingStatus.PENDING))
                .thenReturn(bookings);
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        // When
        List<BookingDTO> result = bookingService.getBookings(loadId, "TRANSPORTER001", BookingStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingDTO.getTransporterId(), result.get(0).getTransporterId());
        verify(bookingRepository).findByFilters(loadId, "TRANSPORTER001", BookingStatus.PENDING);
    }

    @Test
    void testGetBookingById_Success() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.getBookingById(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(bookingDTO.getId(), result.getId());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetBookingById_NotFound() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(bookingId));
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testUpdateBooking_ToAccepted_Success() {
        // Given
        booking.setStatus(BookingStatus.PENDING);
        BookingDTO updateDTO = new BookingDTO();
        updateDTO.setStatus(BookingStatus.ACCEPTED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.updateBooking(bookingId, updateDTO);

        // Then
        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
        verify(loadService).updateLoadStatus(loadId, LoadStatus.BOOKED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void testDeleteBooking_Success() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.findByLoad(load)).thenReturn(Arrays.asList());

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).delete(booking);
        verify(loadService).updateLoadStatus(loadId, LoadStatus.CANCELLED);
    }
}