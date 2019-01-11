package com.rental.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class ReservationPeriod {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
