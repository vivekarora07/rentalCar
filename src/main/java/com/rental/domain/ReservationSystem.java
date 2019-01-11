package com.rental.domain;

import com.rental.model.*;
import com.rental.validator.ErrorCode;
import com.rental.validator.InvalidRequestException;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Data
public class ReservationSystem {

    private static AtomicLong reservationNumberGen = new AtomicLong(910000000000L);
    private static AtomicLong customerNumberGen = new AtomicLong(70000000000L);
    private static ReservationSystem instance;
    private Map<Long, Reservation> reservationMap;
    private Map<Long, Customer> customerMap;
    private Set<AvailableCars> availableCars;

    private ReservationSystem() {
        reservationMap = new ConcurrentHashMap<>();
        customerMap = new ConcurrentHashMap<>();
        availableCars = new HashSet<>();
        initializeRentalCars();
    }

    public static ReservationSystem getInstance() {
        if (instance == null) {
            instance = new ReservationSystem();
        }
        return instance;
    }

    private static Long generateReservationId() {
        return reservationNumberGen.getAndIncrement();
    }

    private static Long generateCustomerId() {
        return customerNumberGen.getAndIncrement();
    }


    public Long createNewReservation(Long custId, VehicleType vehicleType, int zipCode, ReservationPeriod reservationPeriod) throws InvalidRequestException {
        Long reservationId = generateReservationId();
        Reservation reservation = new Reservation(reservationId, custId, vehicleType, zipCode, reservationPeriod, true);
        reservationMap.putIfAbsent(reservationId, reservation);
        return reservationId;
    }

    public Reservation cancelReservation(Long reservationId) throws InvalidRequestException{
        Reservation cancelRes = reservationMap.get(reservationId);
        if(cancelRes!=null && cancelRes.isReserved()==true){
            cancelRes.setReserved(false);
        }else{
            throw new InvalidRequestException("Reservation already expired");
        }
        return cancelRes;
    }

    private AvailableCars getAvailableCar(VehicleType vehicleType, int zipCode){
        AvailableCars searchCriteria = AvailableCars.builder().vehicleType(vehicleType).vehicleCount(0).pickupZip(zipCode).build();
        return availableCars.stream().filter(car -> car.equals(searchCriteria)).findFirst().orElse(null);
    }


    public boolean isRentalAvailable(Long custId, VehicleType vehicleType, int zipCode, ReservationPeriod reservationPeriod) throws InvalidRequestException{
        boolean isAvailable = false;
        AvailableCars returnedCar = getAvailableCar(vehicleType,zipCode);
        boolean isIntervalReservationExistsForCustId = isIntervalReservationExistsForCustomerId(custId,reservationPeriod);
        long existingReservationCountForPeriod = getIntervalReservationsByVehicleTypeZipCode(vehicleType,zipCode,reservationPeriod);
        if(!isIntervalReservationExistsForCustId && returnedCar != null && returnedCar.getVehicleCount() > 0 && returnedCar.getVehicleCount()>existingReservationCountForPeriod){
             isAvailable = true;
        }

        return isAvailable;
    }

    public Reservation findExistingReservation(Long reservationId) throws InvalidRequestException {
        Reservation existingReservation = Optional.ofNullable(reservationMap.get(reservationId)).orElseThrow(() -> new InvalidRequestException(ErrorCode.ERR02.getValue() + " "));
        return existingReservation;
    }

    public Reservation updateReservation(Long reservationId, String firstName, String lastName, Long phoneNo, VehicleType vehicleType, int zipCode, ReservationPeriod reservationPeriod) throws InvalidRequestException {
        Reservation reservationTobeUpdated = findExistingReservation(reservationId);
        if(reservationTobeUpdated!=null){

            Customer returnedCustomer = customerMap.get(reservationTobeUpdated.getCustId());

            if(reservationTobeUpdated.getReservationPeriod().getStartDateTime()!=reservationPeriod.getStartDateTime()
                    || reservationTobeUpdated.getReservationPeriod().getEndDateTime()!=reservationPeriod.getEndDateTime()){
                if(isRentalAvailable(returnedCustomer.getCustomerId(),vehicleType,zipCode,reservationPeriod)){
                    reservationTobeUpdated.setReservationPeriod(reservationPeriod);
                    reservationMap.put(reservationId,reservationTobeUpdated);
                }else{
                    throw new InvalidRequestException("Rental car is not available");
                }

            }


            if(returnedCustomer.getFirstName()!=firstName) {
                returnedCustomer.setFirstName(firstName);
            }
            if(returnedCustomer.getLastName()!=lastName) {
                returnedCustomer.setLastName(lastName);
            }
            if(returnedCustomer.getPhoneNo()!=phoneNo) {
                returnedCustomer.setPhoneNo(phoneNo);
            }

            customerMap.put(returnedCustomer.getCustomerId(), returnedCustomer);
            if(reservationTobeUpdated.getVehicleType()!= vehicleType){
                reservationTobeUpdated.setVehicleType(vehicleType);
            }
            if(reservationTobeUpdated.getZipcode()!=zipCode){
                reservationTobeUpdated.setZipcode(zipCode);
            }

        }
        return reservationTobeUpdated;
    }



    public Long returnCustomerId(String firstName, String lastName, Long phoneNo, String email, int age) throws InvalidRequestException {
        //Optional<Long> customerId = customerMap.entrySet().stream().filter(custMap->custMap.getKey().equals(email)).map(custMap->custMap.getValue().getCustomerId()).findAny();
        List<Customer> customerList = customerMap.entrySet().stream()
                .map(custMap->custMap.getValue())
                .filter(cust -> cust.getEmail().equals(email)).collect(Collectors.toList());
        if (customerList != null && customerList.size() == 1) {
            Customer existingCustomer = Optional.ofNullable(customerList.get(0)).orElseThrow(() -> new InvalidRequestException(ErrorCode.ERR02.getValue() + " "));
            if(existingCustomer.getAge()!=age){
                existingCustomer.setAge(age);
            }
            if(existingCustomer.getFirstName()!=firstName) {
                existingCustomer.setFirstName(firstName);
            }
            if(existingCustomer.getLastName()!=lastName) {
                existingCustomer.setLastName(lastName);
            }
            if(existingCustomer.getPhoneNo()!=phoneNo) {
                existingCustomer.setPhoneNo(phoneNo);
            }

            return existingCustomer.getCustomerId();
        } else {
            Long newCustId = generateCustomerId();
            customerMap.putIfAbsent(newCustId, Customer.builder().customerId(newCustId).firstName(firstName).lastName(lastName).email(email).age(age).phoneNo(phoneNo).build());
            return newCustId;
        }

    }

    private void initializeRentalCars() {
        AvailableCars availableCars1 = AvailableCars.builder().vehicleType(VehicleType.SEDANS).vehicleCount(3).pickupZip(19701).build();
        AvailableCars availableCars2 = AvailableCars.builder().vehicleType(VehicleType.SUVS).vehicleCount(10).pickupZip(19701).build();
        AvailableCars availableCars3 = AvailableCars.builder().vehicleType(VehicleType.TRUCKS).vehicleCount(30).pickupZip(19701).build();
        AvailableCars availableCars4 = AvailableCars.builder().vehicleType(VehicleType.VANS).vehicleCount(40).pickupZip(19701).build();
        availableCars.add(availableCars1);
        availableCars.add(availableCars2);
        availableCars.add(availableCars3);
        availableCars.add(availableCars4);
    }

    public Map<Long, Customer> getCustomerMap() {
        return customerMap;
    }

    public void setCustomerMap(Map<Long, Customer> customerMap) {
        this.customerMap = customerMap;
    }


    public Map<Long, Reservation> getReservationMap() {
        return reservationMap;
    }

    public void setReservationMap(Map<Long, Reservation> reservationMap) {
        this.reservationMap = reservationMap;
    }

    public void markReservationsExpired(){
        if(reservationMap!=null && reservationMap.size()>0) {
            Map<Long, Reservation> reservations = reservationMap.entrySet().stream().filter(resMap -> (resMap.getValue().isReserved() == true &&
                    resMap.getValue().getReservationPeriod() != null &&
                    (resMap.getValue().getReservationPeriod().getEndDateTime()).isBefore(LocalDateTime.now()))).collect(Collectors.toMap(resMap -> resMap.getKey(), resMap -> resMap.getValue()));

            if(reservations !=null && reservations.size()>0) {
                reservations.forEach((id, reservation) -> {
                    reservation.setReserved(false);
                    reservationMap.put(id, reservation);
                });
            }
        }
    }

    public boolean isIntervalReservationExistsForCustomerId(Long custId, ReservationPeriod reservationPeriod) {
        boolean reservationExists = reservationMap.entrySet().stream().filter(resMap -> (resMap.getValue().isReserved()==true &&
                resMap.getValue().getCustId().equals(custId) && resMap.getValue().getReservationPeriod()!=null &&
                overlapTimeFrame(reservationPeriod,resMap.getValue().getReservationPeriod())==true)).findFirst().isPresent();

        return  reservationExists;
    }

    public long getIntervalReservationsByVehicleTypeZipCode(VehicleType vehicleType, int zipCode, ReservationPeriod reservationPeriod){
        long reservationCount = reservationMap.entrySet().stream().filter(resMap -> (resMap.getValue().isReserved()==true &&
                resMap.getValue().getVehicleType().equals(vehicleType) && resMap.getValue().getZipcode()==zipCode && resMap.getValue().getReservationPeriod()!=null &&
                overlapTimeFrame(reservationPeriod,resMap.getValue().getReservationPeriod())==true)).count();
        return  reservationCount;
    }

    private boolean overlapTimeFrame(ReservationPeriod period1, ReservationPeriod period2){
        boolean overlap =true;
        LocalDateTime p1StartTime=period1.getStartDateTime();
        LocalDateTime p1EndTime= period1.getEndDateTime();
        LocalDateTime p2StartTime=period2.getStartDateTime();
        LocalDateTime p2EndTime=period2.getEndDateTime();

        if(((p1StartTime.isBefore(p2StartTime) && (p1EndTime.isBefore(p2StartTime) || p1EndTime.isEqual(p2StartTime)))
           || (p1EndTime.isAfter(p2EndTime) && (p1StartTime.isEqual(p2EndTime) || p1StartTime.isAfter(p2EndTime))))

           || ((p2StartTime.isBefore(p1StartTime) && (p2EndTime.isBefore(p1StartTime) || p2EndTime.isEqual(p1StartTime)))
                        || (p2EndTime.isAfter(p1EndTime) && (p2StartTime.isEqual(p1EndTime) || p2StartTime.isAfter(p1EndTime))))
                ){
            overlap=false;
        }
        return overlap;

    }

    public boolean checkReservationPeriodWithSysDateTime(ReservationPeriod reservationPeriod){
        boolean isReservationValid = false;
        if((reservationPeriod.getStartDateTime().isAfter(LocalDateTime.now().minus(Duration.ofHours(2))))
                && (reservationPeriod.getEndDateTime().isAfter(reservationPeriod.getStartDateTime().plus(Duration.ofDays(1))))){
            isReservationValid = true;

        }
        return isReservationValid;
    }


    public static void main(String args[]) {
        try {

            LocalDateTime l = LocalDateTime.now();
            ReservationPeriod reservationPeriod = new ReservationPeriod(l.minusHours(3L), l.minusHours(2L));
            boolean isAvailable = ReservationSystem.getInstance().isRentalAvailable(generateCustomerId(),VehicleType.SEDANS, 19702,reservationPeriod);
            System.out.println(isAvailable);



            Long reservationId = generateReservationId();
            Long custId= generateCustomerId();

            ReservationPeriod reservationPeriod1 = new ReservationPeriod(l.minusHours(3L), l.minusHours(2L));
            Reservation r1 = Reservation.builder().custId(custId).isReserved(true).reservationId(reservationId).vehicleType(VehicleType.SEDANS).zipcode(19701).reservationPeriod(reservationPeriod1).build();

            ReservationPeriod reservationPeriod2 = new ReservationPeriod(l.minusHours(4L), l.minusHours(2L));
            Reservation r2 = Reservation.builder().custId(custId+1).isReserved(false).reservationId(reservationId+1).vehicleType(VehicleType.VANS).zipcode(19702).reservationPeriod(reservationPeriod2).build();

            ReservationPeriod reservationPeriod3 = new ReservationPeriod(l.minusHours(5L), l.minusHours(4L));
            Reservation r3 = Reservation.builder().custId(custId+2).isReserved(true).reservationId(reservationId+2).vehicleType(VehicleType.TRUCKS).zipcode(19703).reservationPeriod(reservationPeriod3).build();


            ReservationPeriod reservationPeriod4 = new ReservationPeriod(l.minusDays(5L), l.plusDays(4L));
            Reservation r4 = Reservation.builder().custId(custId+3).isReserved(true).reservationId(reservationId+3).vehicleType(VehicleType.SUVS).zipcode(19704).reservationPeriod(reservationPeriod4).build();

            ReservationPeriod reservationPeriod5 = new ReservationPeriod(l.plusHours(3L), l.plusHours(6L));
            Reservation r5 = Reservation.builder().custId(custId+4).isReserved(true).reservationId(reservationId+4).vehicleType(VehicleType.TRUCKS).zipcode(19705).reservationPeriod(reservationPeriod5).build();

            ReservationSystem.getInstance().getReservationMap().putIfAbsent(reservationId,r1);
            ReservationSystem.getInstance().getReservationMap().putIfAbsent(reservationId+1,r2);
            ReservationSystem.getInstance().getReservationMap().putIfAbsent(reservationId+2,r3);
            ReservationSystem.getInstance().getReservationMap().putIfAbsent(reservationId+3,r4);
            ReservationSystem.getInstance().getReservationMap().putIfAbsent(reservationId+4,r5);

            ReservationPeriod reservationPeriod6 = new ReservationPeriod(l.plusHours(4L), l.plusHours(7L));
            ReservationSystem.getInstance().isIntervalReservationExistsForCustomerId(custId+4,reservationPeriod6);


            ReservationSystem.getInstance().markReservationsExpired();

            Customer customer = Customer.builder().customerId(generateCustomerId()).age(25).firstName("Vivek").lastName("Arora").email("vivek16.arora@gmail.com").phoneNo(6106794402L).build();
            ReservationSystem.getInstance().getCustomerMap().putIfAbsent(customer.getCustomerId(), customer);

            ReservationSystem.getInstance().returnCustomerId("Vivek", "Arora", 6106794402L, "vivek18.arora@gmail.com", 25);
        } catch (Exception e) {

        }
    }

}
