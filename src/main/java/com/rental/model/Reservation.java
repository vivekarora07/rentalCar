package com.rental.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long carId;
    private Long customerId;
    private int numOfDaysReserved;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

}
