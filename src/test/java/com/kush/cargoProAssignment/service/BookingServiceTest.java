package com.kush.cargoProAssignment.service;

import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.exceptions.BusinessException;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.model.Booking;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
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

    private UUID bookingId;
    private UUID loadId;
    private Booking booking;
    private Load load;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        loadId = UUID.randomUUID();

        load = new Load();
        load.setId(loadId);
        load.setStatus(LoadStatus.POSTED);

        booking = new Booking();
        booking.setId(bookingId);
        booking.setLoad(load);
        booking.setStatus(BookingStatus.PENDING);

        bookingDTO = new BookingDTO();
        bookingDTO.setLoadId(loadId);
    }

    @Test
    void createBooking_shouldSetLoadStatusToBooked_whenSuccessful() {
        // Given
        when(loadService.findEntityById(loadId)).thenReturn(load);
        when(modelMapper.map(bookingDTO, Booking.class)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.createBooking(bookingDTO);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        verify(loadService, times(1)).updateLoadStatus(loadId, LoadStatus.BOOKED);
    }

    @Test
    void createBooking_shouldThrowException_whenLoadIsCancelled() {
        // Given
        load.setStatus(LoadStatus.CANCELLED);
        when(loadService.findEntityById(loadId)).thenReturn(load);

        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.createBooking(bookingDTO));
        verify(loadService, never()).updateLoadStatus(any(), any());
    }

    @Test
    void getBookings_shouldReturnListOfBookingDTOs() {
        // Given
        List<Booking> bookingList = Collections.singletonList(booking);
        when(bookingRepository.findByFilters(any(), any(), any())).thenReturn(bookingList);
        when(modelMapper.map(any(Booking.class), eq(BookingDTO.class))).thenReturn(bookingDTO);

        // When
        List<BookingDTO> result = bookingService.getBookings(loadId, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingDTO, result.get(0));
    }

    @Test
    void getBookingById_shouldReturnBookingDTO_whenBookingExists() {
        // Given
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        // When
        BookingDTO result = bookingService.getBookingById(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(bookingDTO, result);
    }

    @Test
    void updateBooking_shouldUpdateStatusAndKeepLoadStatusAsBooked() {
        // Given
        booking.setStatus(BookingStatus.PENDING);
        BookingDTO updatedDto = new BookingDTO();
        updatedDto.setStatus(BookingStatus.ACCEPTED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(updatedDto);

        // When
        BookingDTO result = bookingService.updateBooking(bookingId, updatedDto);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.ACCEPTED, booking.getStatus());
        verify(loadService, times(1)).updateLoadStatus(loadId, LoadStatus.BOOKED);
    }

    @Test
    void updateBooking_shouldNotUpdateLoadStatus_ifBookingStatusIsAlreadyAccepted() {
        // Given
        booking.setStatus(BookingStatus.ACCEPTED);
        BookingDTO updatedDto = new BookingDTO();
        updatedDto.setStatus(BookingStatus.ACCEPTED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(updatedDto);

        // When
        BookingDTO result = bookingService.updateBooking(bookingId, updatedDto);

        // Then
        assertNotNull(result);
        verify(loadService, never()).updateLoadStatus(any(), any());
    }

    @Test
    void deleteBooking_shouldRevertLoadStatusToCancelled_whenLastBookingIsDeleted() {
        // Given
        List<Booking> remainingBookings = Collections.emptyList();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.findByLoad(load)).thenReturn(remainingBookings);
        doNothing().when(bookingRepository).delete(any(Booking.class));

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository, times(1)).delete(booking);
        verify(loadService, times(1)).updateLoadStatus(loadId, LoadStatus.CANCELLED);
    }

    @Test
    void deleteBooking_shouldRevertLoadStatusToPosted_whenOtherBookingsRemainAndNoneAreAccepted() {
        // Given
        Booking otherBooking = new Booking();
        otherBooking.setStatus(BookingStatus.PENDING);
        List<Booking> remainingBookings = Collections.singletonList(otherBooking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.findByLoad(load)).thenReturn(remainingBookings);
        doNothing().when(bookingRepository).delete(any(Booking.class));

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository, times(1)).delete(booking);
        verify(loadService, times(1)).updateLoadStatus(loadId, LoadStatus.POSTED);
    }

    @Test
    void deleteBooking_shouldKeepLoadStatusAsBooked_whenOtherAcceptedBookingsRemain() {
        // Given
        Booking otherBooking = new Booking();
        otherBooking.setStatus(BookingStatus.ACCEPTED);
        List<Booking> remainingBookings = Collections.singletonList(otherBooking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.findByLoad(load)).thenReturn(remainingBookings);
        doNothing().when(bookingRepository).delete(any(Booking.class));

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository, times(1)).delete(booking);
        verify(loadService, never()).updateLoadStatus(any(), any());
    }
}