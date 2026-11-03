package com.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {

    private Long id;

    private String name;

    private String description;

    private int price;

    private int duration; // Duration in minutes

    private Long salonId;

    private Long category;

    private String image;
}
