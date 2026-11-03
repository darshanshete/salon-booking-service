package com.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SalonDTO {

    private Long id;;

    private String name;

    private List<String> images;

    private String address;

    private String phoneNumber;

    private String email;

    private String city;

    private Long ownerId;

    private UserDTO owner;

    private LocalTime openingTime;

    private LocalTime closeTime;
}
