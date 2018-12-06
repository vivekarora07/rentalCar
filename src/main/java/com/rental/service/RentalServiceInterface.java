package com.rental.service;

import com.rental.model.Car;
import com.rental.model.Customer;

public interface RentalServiceInterface {

        boolean makeReservation(Long carId, Long customerId);
}
