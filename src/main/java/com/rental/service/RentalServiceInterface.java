package com.rental.service;

import com.rental.model.Customer;
import com.rental.model.Reservation;
import com.rental.model.ReservationPeriod;
import com.rental.model.VehicleType;
import com.rental.validator.InvalidRequestException;

import java.time.LocalDateTime;

public interface RentalServiceInterface {

        Long createReservation(Customer customer, VehicleType vehicleType, int pickupZip, ReservationPeriod reservationPeriod) throws InvalidRequestException;

        Reservation updateReservation(Long reservationId, String firstName, String lastName, Long phoneNo, VehicleType vehicleType, int pickupZip, ReservationPeriod reservationPeriod) throws InvalidRequestException;

        Reservation  cancelReservation(Long reservationId) throws InvalidRequestException;

        Long returnCustomerId(String firstName, String lastName, Long phoneNo, String email, int age) throws InvalidRequestException;

        boolean isRentalAvailable(Long custId, VehicleType vehicleType, int zipCode,ReservationPeriod reservationPeriod) throws InvalidRequestException;

        void markReservationsExpired();

        Reservation getReservationById(Long reservationId) throws InvalidRequestException;

        boolean checkReservationPeriodWithSysDateTime(ReservationPeriod reservationPeriod);

}

