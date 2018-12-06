package com.rental.model;

import lombok.Data;

@Data
public class Car {
    private Long carId;
    private String make;
    private String model;
    private VehicleType type;
    private String color;
    private String isReserved;
    private float rentalPrice;
}
