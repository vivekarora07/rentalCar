package com.rental.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class Customer {
    private Long customerId;
    @EqualsAndHashCode.Exclude private String firstName;
    @EqualsAndHashCode.Exclude private String lastName;
    @EqualsAndHashCode.Exclude private Long phoneNo;
    private String email;
    @EqualsAndHashCode.Exclude private int age;

}
