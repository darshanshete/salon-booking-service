package com.booking.service.impl;


import com.booking.domain.BookingStatus;
import com.booking.dto.BookingRequest;
import com.booking.dto.SalonDTO;
import com.booking.dto.ServiceDTO;
import com.booking.dto.UserDTO;
import com.booking.model.Booking;
import com.booking.model.SalonReport;
import com.booking.repository.BookingRepository;
import com.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Booking createBooking(BookingRequest booking, UserDTO user, SalonDTO salon, Set<ServiceDTO> serviceDTOSet) throws Exception {

        int totalDuration = serviceDTOSet.stream().mapToInt(ServiceDTO::getDuration).sum();

        LocalDateTime bookingStartTime = booking.getStartTime();
        LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);

        Boolean isSlotAvailable = isTimeSlotAvailable(salon, bookingStartTime, bookingEndTime);

        int totalServices = serviceDTOSet.size();

        int totalPrice = serviceDTOSet.stream().mapToInt(ServiceDTO::getPrice).sum();

        Set<Long> idList = serviceDTOSet.stream().map(ServiceDTO::getId).collect(java.util.stream.Collectors.toSet());

        Booking newBooking = new Booking();
        newBooking.setSalonId(salon.getId());
        newBooking.setCustomerId(user.getId());
        newBooking.setStartTime(bookingStartTime);
        newBooking.setEndTime(bookingEndTime);
        newBooking.setServiceIds(idList);
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setTotalPrice(totalPrice);
        newBooking.setTotalServices(totalServices);

        return bookingRepository.save(newBooking);
    }

    public Boolean isTimeSlotAvailable(SalonDTO salonDTO, LocalDateTime bookingStartTime, LocalDateTime bookingEndTime) throws Exception {

        List<Booking> existingBookings= getBookingsBySalon(salonDTO.getId());
        LocalDateTime salonOpenTime = salonDTO.getOpeningTime().atDate(bookingStartTime.toLocalDate());
        LocalDateTime salonCloseTime = salonDTO.getCloseTime().atDate(bookingStartTime.toLocalDate());

        if(bookingStartTime.isBefore(salonOpenTime) || bookingEndTime.isAfter(salonCloseTime)) {
            throw new Exception("Booking time must be withing salon's working hours");
        }

        for(Booking existingBooking: existingBookings) {
            LocalDateTime existingBookingStartTime = existingBooking.getStartTime();
            LocalDateTime existingBookingEndTime = existingBooking.getEndTime();

            if(bookingStartTime.isBefore(existingBookingEndTime) && bookingEndTime.isAfter(existingBookingStartTime)) {
                throw new Exception("The selected time slot is not available for booking. Please choose a different time.");
            }

            if(bookingStartTime.isEqual(existingBookingStartTime) || bookingEndTime.isEqual(existingBookingEndTime)) {
                throw new Exception("The selected time slot is not available for booking. Please choose a different time.");
            }
        }
        return true;
    }

    @Override
    public List<Booking> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getBookingsBySalon(Long salonId) {
        return bookingRepository.findBySalonId(salonId);
    }

    @Override
    public Booking getBookingById(Long id) throws Exception {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            throw new Exception("Booking not found with id: " + id);
        }
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookingId, BookingStatus status) throws Exception {
        Booking existingBooking =getBookingById(bookingId);
        existingBooking.setStatus(status);
        return bookingRepository.save(existingBooking);
    }

    @Override
    public List<Booking> getBookingsByDate(LocalDate date, Long salonId) {

        List<Booking> allBookings = getBookingsBySalon(salonId);

        if(date==null){
            return allBookings;
        }

       return allBookings.stream().filter(booking-> isSameDate(booking.getStartTime(), date) || isSameDate(booking.getEndTime(),date)).collect(Collectors.toList());
    }

    private boolean isSameDate(LocalDateTime dateTime, LocalDate date) {
        return dateTime.toLocalDate().isEqual(date);
    }

    @Override
    public SalonReport getSalonReport(Long salonId) {
        List<Booking> bookings = getBookingsBySalon(salonId);
        Double totalEarnings = bookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        Integer totalBookings=bookings.size();
        List<Booking> cancelledBookings = bookings.stream().filter(booking -> booking.getStatus().equals(BookingStatus.CANCELLED)).collect(Collectors.toList());

     Double totalRefund=cancelledBookings.stream().mapToDouble(Booking::getTotalPrice).sum();

     SalonReport report = new SalonReport();
     report.setSalonId(salonId);
     report.setCancelledBookings(cancelledBookings.size());
     report.setTotalBookings(totalBookings);
     report.setTotalEarnings(totalEarnings);
     report.setTotalRefund(totalRefund);


        return report;
    }
}
