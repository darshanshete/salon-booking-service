package com.booking.mapper;

import com.booking.dto.BookingDTO;
import com.booking.model.Booking;

public class BookingMapper {

    public static BookingDTO toDTO(Booking booking){

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setSalonId(booking.getSalonId());
        bookingDTO.setCustomerId(booking.getCustomerId());
        bookingDTO.setStartTime(booking.getStartTime());
        bookingDTO.setEndTime(booking.getEndTime());
        bookingDTO.setServiceIds(booking.getServiceIds());
        bookingDTO.setStatus(booking.getStatus());

        return bookingDTO;
    }

}
