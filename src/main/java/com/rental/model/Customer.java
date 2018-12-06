package com.rental.model;

import lombok.Data;

@Data
public class Customer {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String address;
    private int phoneNo;
    private int age;

}
