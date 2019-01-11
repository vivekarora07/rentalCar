package com.rental.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"vehicleCount"})
public class AvailableCars {
    private VehicleType vehicleType;
    private long vehicleCount;
    private int pickupZip;
}
