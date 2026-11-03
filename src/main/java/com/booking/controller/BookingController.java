package com.booking.controller;

import com.booking.domain.BookingStatus;
import com.booking.dto.*;
import com.booking.mapper.BookingMapper;
import com.booking.model.Booking;
import com.booking.model.SalonReport;
import com.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestParam Long salonId, @RequestBody BookingRequest bookingRequest) throws Exception {

        UserDTO user = new UserDTO();
        user.setId(1L);
        SalonDTO salon = new SalonDTO();
        salon.setId(salonId);
        salon.setOpeningTime(LocalTime.of(9, 0));
        salon.setCloseTime(LocalTime.of(23, 0));
        Set<ServiceDTO> serviceDTOSet = new HashSet<>();
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(1L);
        serviceDTO.setPrice(399);
        serviceDTO.setDuration(45);
        serviceDTO.setName("Hair cut for men");

        serviceDTOSet.add(serviceDTO);

        Booking booking = bookingService.createBooking(bookingRequest,user,salon,serviceDTOSet);

        return ResponseEntity.ok(booking);
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Set<BookingDTO>> getBookingsByCustomer(@PathVariable Long customerId) {

        List<Booking> bookings =
                bookingService.getBookingsByCustomer(customerId);

        return ResponseEntity.ok(getBookingDTOs(bookings));
    }

    private Set<BookingDTO> getBookingDTOs(List<Booking> bookings){
        return bookings.stream()
                .map(booking -> {
            return BookingMapper.toDTO(booking);
        }).collect(Collectors.toSet());
    }

    @GetMapping("/salon")
    public ResponseEntity<Set<BookingDTO>> getBookingsBySalon() {

        List<Booking> bookings = bookingService.getBookingsBySalon(1L);
        return ResponseEntity.ok(getBookingDTOs(bookings));
    }


    @GetMapping("/slots/salon/{salonId}/date/{date}")
    public ResponseEntity<List<BookingSlotDTO>> getBookingSlot(
            @PathVariable Long salonId,
            @RequestParam(required = false) LocalDate date) {

        List<Booking> bookings = bookingService.getBookingsByDate(date,salonId);
        List<BookingSlotDTO> slotDTOs = bookings.stream()
                .map(booking -> {
                    BookingSlotDTO slotDTO = new BookingSlotDTO();
                    slotDTO.setStartTime(booking.getStartTime());
                    slotDTO.setEndTime(booking.getEndTime());
                    return slotDTO;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(slotDTOs);
    }

    @GetMapping("/report")
    public ResponseEntity<SalonReport> getSalonReport() {

        SalonReport report = bookingService.getSalonReport(1L);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{bookingid}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long bookingid ) throws Exception {

        Booking booking = bookingService.getBookingById(bookingid);
        return ResponseEntity.ok(BookingMapper.toDTO(booking));
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status) throws Exception {

        Booking booking = bookingService.updateBooking(bookingId,status);

        return ResponseEntity.ok(BookingMapper.toDTO(booking));
    }
}
