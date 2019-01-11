package com.rental.service;

import com.rental.domain.ReservationSystem;
import com.rental.model.Customer;
import com.rental.model.Reservation;
import com.rental.model.ReservationPeriod;
import com.rental.model.VehicleType;
import com.rental.validator.InvalidRequestException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;
@RunWith(JUnit4.class)
public class RentalServiceTest {

    Customer customer;
    ReservationPeriod reservationPeriod;

    RentalServiceInterface service = new RentalService();
    private static AtomicLong customerNumberGen = new AtomicLong(70000000000L);
    LocalDateTime localDateTime = LocalDateTime.now();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        ReservationSystem.getInstance();
        customer = generateCustomer();
        reservationPeriod = generateReservation();
    }


    private Customer generateCustomer(){
        Customer customer = Customer.builder().firstName("Vivek").lastName("Arora").phoneNo(6106794402L).age(35).email("vivek.arora@gmail.com").build();
        return customer;
    }

    private ReservationPeriod generateReservation(){
       return new ReservationPeriod(localDateTime.plusDays(2), localDateTime.plusDays(4));
    }

    @Test
    public void createSingleReservation() throws Exception{
      Long reservationId= service.createReservation(customer,VehicleType.VANS,19701,reservationPeriod);
      assertNotNull(reservationId);
    }


    @Test
    public void createOverlappingReservationForSameCustomer() throws InvalidRequestException{
        customer.setEmail("abc@gmail.com");
        Long reservationId1 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        ReservationPeriod reservationPeriod2 = new ReservationPeriod(localDateTime.plusDays(3), localDateTime.plusDays(5));
        Long reservationId2 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod2);
        assertNotNull(reservationId1);
        assertNull(reservationId2);
    }

    @Test
    public void createNonOverlappingReservationForSameCustomer() throws InvalidRequestException{
        customer.setEmail("def@gmail.com");
        reservationPeriod = generateReservation();
        Long reservationId1 = service.createReservation(customer, VehicleType.VANS, 19701, reservationPeriod);
        ReservationPeriod reservationPeriod2 = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        Long reservationId2 = service.createReservation(customer, VehicleType.VANS, 19701, reservationPeriod2);
        assertNotNull(reservationId1);
        assertNotNull(reservationId2);
    }

    @Test
    public void createOverlappingReservationForDifferentCustomer() throws InvalidRequestException{
        customer.setEmail("ghi@gmail.com");
        reservationPeriod = generateReservation();
        Long reservationId1 = service.createReservation(customer, VehicleType.VANS, 19701, reservationPeriod);
        customer.setEmail("ijk@gmail.com");
        Long reservationId2 = service.createReservation(customer, VehicleType.VANS, 19701, reservationPeriod);
        assertNotNull(reservationId1);
        assertNotNull(reservationId2);
    }

    @Test
    public void createOverlappingReservationForDifferentCustomerWithMaxCapacity() throws InvalidRequestException{
        customer.setEmail("hij@gmail.com");
        ReservationPeriod reservationPeriodMaxCapacity = new ReservationPeriod(localDateTime.plusDays(2), localDateTime.plusDays(4));
        customer.setEmail("jkl@gmail.com");
        Long reservationId1 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriodMaxCapacity);
        customer.setEmail("lmn@gmail.com");
        Long reservationId2 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriodMaxCapacity);
        customer.setEmail("nop@gmail.com");
        Long reservationId3 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriodMaxCapacity);
        customer.setEmail("pqr@gmail.com");
        Long reservationId4 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriodMaxCapacity);
        assertNotNull(reservationId1);
        assertNotNull(reservationId2);
        assertNotNull(reservationId3);
        assertNull(reservationId4);
    }

    @Test
    public void createNonOverlappingReservationForDifferentCustomer() throws InvalidRequestException{
        customer.setEmail("rst@gmail.com");
        reservationPeriod = generateReservation();
        Long reservationId1 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        customer.setEmail("tuv@gmail.com");
        ReservationPeriod resPeriodNonOverlapping1 = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        Long reservationId2 = service.createReservation(customer, VehicleType.SUVS, 19701, resPeriodNonOverlapping1);
        ReservationPeriod resPeriodNonOverlapping2 = new ReservationPeriod(localDateTime.plusDays(6), localDateTime.plusDays(8));
        customer.setEmail("vwx@gmail.com");
        Long reservationId3 = service.createReservation(customer, VehicleType.SUVS, 19701, resPeriodNonOverlapping2);
        ReservationPeriod resPeriodNonOverlapping3 = new ReservationPeriod(localDateTime.plusDays(8), localDateTime.plusDays(10));
        customer.setEmail("xyz@gmail.com");
        Long reservationId4 = service.createReservation(customer, VehicleType.SUVS, 19701, resPeriodNonOverlapping3);
        assertNotNull(reservationId1);
        assertNotNull(reservationId2);
        assertNotNull(reservationId3);
        assertNotNull(reservationId4);
    }

    @Test
    public void updateReservationForNonOverlappingPeriod() throws InvalidRequestException{
        customer.setEmail("abcd@gmail.com");
        reservationPeriod = generateReservation();
        Long reservationId1 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        customer.setEmail("defg@gmail.com");
        Long reservationId2 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        customer.setEmail("ghij@gmail.com");
        Long reservationId3 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);

        ReservationPeriod resPeriodNonOverlapping = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        service.updateReservation(reservationId3,"Gary", "Hermans",123456789L,VehicleType.SUVS,19701,resPeriodNonOverlapping);
        Reservation returnedReservation = service.getReservationById(reservationId3);
        assertEquals(returnedReservation.getReservationPeriod().getStartDateTime(), resPeriodNonOverlapping.getStartDateTime());
        assertEquals(returnedReservation.getReservationPeriod().getEndDateTime(), resPeriodNonOverlapping.getEndDateTime());
    }

    @Test
    public void updateReservationForOverlappingPeriod() throws InvalidRequestException{
        customer.setEmail("jklm@gmail.com");
        reservationPeriod = generateReservation();
        expectedException.expect(InvalidRequestException.class);
        Long reservationId1 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        customer.setEmail("mnop@gmail.com");
        Long reservationId2 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        customer.setEmail("pqrs@gmail.com");
        Long reservationId3 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);

        ReservationPeriod resOverlappingPeriod = new ReservationPeriod(localDateTime.plusDays(3), localDateTime.plusDays(5));
        service.updateReservation(reservationId3,"Gary", "Hermans",123456789L,VehicleType.SUVS,19701,resOverlappingPeriod);

    }

    @Test
    public void cancelReservation() throws InvalidRequestException{
        customer.setEmail("rstu@gmail.com");
        reservationPeriod = generateReservation();
        Long reservationId1 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriod);
        service.cancelReservation(reservationId1);
        assertEquals(service.getReservationById(reservationId1).isReserved(), false);
    }

    @Test
    public void cancelReservationForNonExistentId() throws InvalidRequestException{
        expectedException.expect(InvalidRequestException.class);
        service.cancelReservation(7000000L);
    }


    @Test
    public void returnCustomerId() throws  InvalidRequestException{
        customer.setEmail("uvwx@gmail.com");
        ReservationPeriod reservationPeriodForReturnedCust = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        Long reservationId1 = service.createReservation(customer, VehicleType.SUVS, 19701, reservationPeriodForReturnedCust);
        Long returnedCustId = service.returnCustomerId("Megha", "Arora", 6106794401L, "uvwx@gmail.com", 37);
        assertEquals(service.getReservationById(reservationId1).getCustId(), returnedCustId);
    }

    @Test
    public void isRentalAvailableInOverlappingPeriodForSameCustomer() throws InvalidRequestException{
        customer.setEmail("wxyz@gmail.com");
        ReservationPeriod reservationPeriodForReturnedCust = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        Long reservationId1 = service.createReservation(customer, VehicleType.VANS, 19701, reservationPeriodForReturnedCust);
        boolean isAvailable = service.isRentalAvailable(service.getReservationById(reservationId1).getCustId(),VehicleType.SEDANS, 19701, reservationPeriodForReturnedCust);
        assertEquals(isAvailable, false);
    }

    @Test
    public void isRentalAvailableInDifferentPeriodForSameCustomer() throws InvalidRequestException{
        customer.setEmail("xyza@gmail.com");
        ReservationPeriod reservationPeriodForReturnedCust = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        Long reservationId1 = service.createReservation(customer, VehicleType.TRUCKS, 19701, reservationPeriodForReturnedCust);
        ReservationPeriod reservationNonOverlappingPeriod = new ReservationPeriod(localDateTime.plusDays(6), localDateTime.plusDays(8));
        boolean isAvailable = service.isRentalAvailable(service.getReservationById(reservationId1).getCustId(),VehicleType.SEDANS, 19701, reservationNonOverlappingPeriod);
        assertEquals(isAvailable, true);
    }

    @Test
    public void isRentalAvailableInOverlappingPeriodForDifferentCustomer() throws InvalidRequestException{
        customer.setEmail("qwer@gmail.com");
        Long reservationId1 = service.createReservation(customer, VehicleType.VANS, 19701, reservationPeriod);
        customer.setEmail("wert@gmail.com");
        ReservationPeriod reservationNonOverlappingPeriod = new ReservationPeriod(localDateTime.plusDays(4), localDateTime.plusDays(6));
        Long reservationId2 = service.createReservation(customer, VehicleType.VANS, 19701, reservationNonOverlappingPeriod);
        boolean isAvailableForCustomer1 = service.isRentalAvailable(service.getReservationById(reservationId2).getCustId(),VehicleType.VANS, 19701, reservationPeriod);
        boolean isAvailableForCustomer2 = service.isRentalAvailable(service.getReservationById(reservationId1).getCustId(),VehicleType.VANS, 19701, reservationNonOverlappingPeriod);
        assertEquals(isAvailableForCustomer1, true);
        assertEquals(isAvailableForCustomer2, true);
    }

    @Test
    public void rentalNotAvailableForMaxAvailableOutOfLimit() throws InvalidRequestException{
        Long reservationId1 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriod);
        customer.setEmail("abc123@gmail.com");
        Long reservationId2 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriod);
        customer.setEmail("xyz123@gmail.com");
        Long reservationId3 = service.createReservation(customer, VehicleType.SEDANS, 19701, reservationPeriod);
        customer.setEmail("def123@gmail.com");
        customer.setCustomerId(customerNumberGen.getAndIncrement()+10);
        boolean isAvailable = service.isRentalAvailable(customer.getCustomerId(),VehicleType.SEDANS, 19701, reservationPeriod);
        assertEquals(isAvailable,false);
    }

    @Test
    public void markReservationsExpired() throws InvalidRequestException{

       ReservationPeriod reservationPeriod1 = new ReservationPeriod(localDateTime.plusDays(-4), localDateTime.plusDays(-1));
       Reservation reservatrion1 = Reservation.builder().custId(1000L).isReserved(true).reservationId(4000L).vehicleType(VehicleType.SUVS).zipcode(19701).reservationPeriod(reservationPeriod1).build();

        ReservationPeriod reservationPeriod2 = new ReservationPeriod(localDateTime.plusDays(-3), localDateTime.plusDays(-2));
        Reservation reservatrion2 = Reservation.builder().custId(2000L).isReserved(true).reservationId(3000L).vehicleType(VehicleType.SUVS).zipcode(19701).reservationPeriod(reservationPeriod2).build();

        ReservationPeriod reservationPeriod3 = new ReservationPeriod(localDateTime.plusDays(3), localDateTime.plusDays(6));
        Reservation reservatrion3 = Reservation.builder().custId(6000L).isReserved(true).reservationId(7000L).vehicleType(VehicleType.SUVS).zipcode(19701).reservationPeriod(reservationPeriod3).build();

        Map<Long, Reservation> reservationMap = new HashMap<>();
        reservationMap.put(4000L,reservatrion1);
        reservationMap.put(3000L,reservatrion2);
        reservationMap.put(7000L,reservatrion3);


        ReservationSystem.getInstance().setReservationMap(reservationMap);

        service.markReservationsExpired();

        assertEquals(service.getReservationById(4000L).isReserved(),false);
        assertEquals(service.getReservationById(3000L).isReserved(),false);
        assertEquals(service.getReservationById(7000L).isReserved(),true);
    }

}