package com.rental.service;

import com.rental.domain.ReservationSystem;
import com.rental.model.Customer;
import com.rental.model.Reservation;
import com.rental.model.ReservationPeriod;
import com.rental.model.VehicleType;
import com.rental.validator.InvalidRequestException;
import lombok.NonNull;

/**

 * 1.       The system should let a customer reserve a car of a given type at a desired date and time for a given number of days
 *
 * 2.     The number of cars of each type is limited, but customers should be able to reserve a single rental car for multiple, non-overlapping time frames
 *
 * 3.       Provide a Junit test that illustrates the core reservation workflow and demonstrates its correctness
 *
 */

public class RentalService  implements RentalServiceInterface{


    @Override
    public Long createReservation(@NonNull Customer customer, @NonNull VehicleType vehicleType, @NonNull int zipCode, @NonNull ReservationPeriod reservationPeriod) throws InvalidRequestException {
        Long reservationId = null;
        //mark reservation expired for reservation period end datetime < sysdatetime
        markReservationsExpired();

        //check reservation period should be greater than sysdatetime
        boolean isPeriodValid = checkReservationPeriodWithSysDateTime(reservationPeriod);
        if(isPeriodValid) {
            //Generate or get customer Id
            Long custId = returnCustomerId(customer.getFirstName(), customer.getLastName(), customer.getPhoneNo(), customer.getEmail(), customer.getAge());
            //Check if rental is available (Total Vehicle Count from Available cars based on zipcode and vehicleType > Reserved count based on zipcode and vehicle  for a given interval
            boolean isAvailable = isRentalAvailable(custId, vehicleType, zipCode, reservationPeriod);
            if (isAvailable) {
                reservationId = ReservationSystem.getInstance().createNewReservation(custId, vehicleType, zipCode, reservationPeriod);
            }
        }else{
            throw new InvalidRequestException("Invalid reservation Period");
        }

        return reservationId;
    }

    @Override
    public boolean checkReservationPeriodWithSysDateTime(@NonNull ReservationPeriod reservationPeriod) {
        return ReservationSystem.getInstance().checkReservationPeriodWithSysDateTime(reservationPeriod);
    }

    @Override
    public Reservation updateReservation(@NonNull Long reservationId, @NonNull String firstName, @NonNull String lastName, @NonNull Long phoneNo, @NonNull VehicleType vehicleType, @NonNull int zipCode, @NonNull ReservationPeriod reservationPeriod) throws InvalidRequestException{
        //mark reservation expired for reservation period end datetime < sysdatetime
        markReservationsExpired();
        //check reservation period should be greater than sysdatetime
        boolean isPeriodValid = checkReservationPeriodWithSysDateTime(reservationPeriod);
        if(isPeriodValid) {
            Reservation reservation = ReservationSystem.getInstance().updateReservation(reservationId, firstName, lastName, phoneNo, vehicleType, zipCode, reservationPeriod);
            return reservation;
        }else{
            throw new InvalidRequestException("Invalid reservation Period");
        }
    }

    @Override
    public Reservation cancelReservation(@NonNull Long reservationId) throws InvalidRequestException{
        //mark reservation expired for reservation period end datetime < sysdatetime
        markReservationsExpired();
        return ReservationSystem.getInstance().cancelReservation(reservationId);
    }

    @Override
    public Long returnCustomerId(@NonNull String firstName, @NonNull String lastName, @NonNull Long phoneNo, @NonNull String email, @NonNull int age)  throws InvalidRequestException{
        return ReservationSystem.getInstance().returnCustomerId(firstName,lastName,phoneNo,email,age);
    }

    @Override
    public boolean isRentalAvailable(@NonNull Long custId, @NonNull VehicleType vehicleType, @NonNull int pickupZip,@NonNull ReservationPeriod reservationPeriod) throws InvalidRequestException{
        return ReservationSystem.getInstance().isRentalAvailable(custId, vehicleType,pickupZip,reservationPeriod);
    }

    @Override
    public void markReservationsExpired() {
        ReservationSystem.getInstance().markReservationsExpired();
    }

    @Override
    public Reservation getReservationById(@NonNull Long reservationId) throws InvalidRequestException{
        return ReservationSystem.getInstance().findExistingReservation(reservationId);
    }
}
