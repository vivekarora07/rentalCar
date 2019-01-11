package com.rental.model;

public enum VehicleType {

    SEDANS("SEDANS"), SUVS("SUVS"), VANS("VANS"), TRUCKS("TRUCKS");

    private String value;

    VehicleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
