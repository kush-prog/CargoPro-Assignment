package com.kush.cargoProAssignment.service;

import com.kush.cargoProAssignment.dto.BookingDTO;
import com.kush.cargoProAssignment.exceptions.BusinessException;
import com.kush.cargoProAssignment.exceptions.ResourceNotFoundException;
import com.kush.cargoProAssignment.model.Booking;
import com.kush.cargoProAssignment.model.Load;
import com.kush.cargoProAssignment.model.enums.BookingStatus;
import com.kush.cargoProAssignment.model.enums.LoadStatus;
import com.kush.cargoProAssignment.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final LoadService loadService;

    private final ModelMapper modelMapper;

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Load load = loadService.findEntityById(bookingDTO.getLoadId());

        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw new BusinessException("Cannot create booking for a cancelled load");
        }

        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        booking.setLoad(load);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Update load status to BOOKED when any booking is created
        loadService.updateLoadStatus(load.getId(), LoadStatus.BOOKED);

        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    public List<BookingDTO> getBookings(UUID loadId, String transporterId, BookingStatus status) {
        return bookingRepository.findByFilters(loadId, transporterId, status)
                .stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(UUID id) {
        Booking booking = findEntityById(id);
        return modelMapper.map(booking, BookingDTO.class);
    }

    public BookingDTO updateBooking(UUID id, BookingDTO bookingDTO) {
        Booking existingBooking = findEntityById(id);
        BookingStatus previousStatus = existingBooking.getStatus();

        // Only update modifiable fields (avoid overriding load and ID)
        existingBooking.setTransporterId(bookingDTO.getTransporterId());
        existingBooking.setProposedRate(bookingDTO.getProposedRate());
        existingBooking.setComment(bookingDTO.getComment());
        existingBooking.setStatus(bookingDTO.getStatus());

        Booking updatedBooking = bookingRepository.save(existingBooking);

        // If accepted, ensure load status is BOOKED
        if (updatedBooking.getStatus() == BookingStatus.ACCEPTED &&
                previousStatus != BookingStatus.ACCEPTED) {
            loadService.updateLoadStatus(updatedBooking.getLoad().getId(), LoadStatus.BOOKED);
        }

        return modelMapper.map(updatedBooking, BookingDTO.class);
    }

    public void deleteBooking(UUID id) {
        Booking booking = findEntityById(id);
        Load load = booking.getLoad();

        bookingRepository.delete(booking);

        // Check remaining bookings
        List<Booking> remainingBookings = bookingRepository.findByLoad(load);
        boolean hasAcceptedBookings = remainingBookings.stream()
                .anyMatch(b -> b.getStatus() == BookingStatus.ACCEPTED);

        if (!hasAcceptedBookings) {
            // If no accepted bookings remain, revert load status
            LoadStatus newStatus = remainingBookings.isEmpty() ? LoadStatus.CANCELLED : LoadStatus.POSTED;
            loadService.updateLoadStatus(load.getId(), newStatus);
        }
    }

    protected Booking findEntityById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }
}
