package com.rental.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Reservation {
    private Long reservationId;
    private Long custId;
    private VehicleType vehicleType;
    private int zipcode;
    private ReservationPeriod reservationPeriod;
    private boolean isReserved;

}
