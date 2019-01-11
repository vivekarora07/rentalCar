#################################################################################
     Car Rental Reservation System

################################################################################
1. RentalServiceTest is a Junit class to test Reservation flow for the car rental

2. ReservationService via  ReservationServiceInterface calls the domain object ReservationSystem which is singleton object

3. There are APIs for create, update and cancel reservation

4. Every customer has unique emailId  and is identified by unique customerId

5. There is markReservationsExpired API which set isReserved flag to false for expired reservations (reservation period end datetime < sysdatetime)

6. There is checkReservationPeriodWithSysDateTime API which checks input reservation period is valid. There is assumption that entered reservation period start  datetime is 2 hours after current datetime and
reservation end datetime should be 1 day ahead of reservation start date time

7. There is overlapTimeFrame function  which checks overlap b/w 2 reservation periods. It returns true if there is overlap otherwise false

8. There is getIntervalReservationsByVehicleTypeZipCode function which returns count of rent cars based on isReserved flag = true, vehicletype, zipcode and overlapping reservation period

9. There is isIntervalReservationExistsForCustomerId function which true/false if any overlapping reservation exists for customer based on vehicletype and zipcode

10. There is initializeRentalCars function which loads  when RentalSystem domain singleton object is created. It contains total count of availablecars, vehicleType and zipcode. We can load from filesystem or in memory DB

11. There is isRentalAvailable API which returns tru/false based on isIntervalReservationExistsForCustomerId function , getIntervalReservationsByVehicleTypeZipCode function > total available car count check

12. There is returnCustomerId which returns new or existing customerId based on email check. Same email for existing customer and new email for new customer

13. There is createReservation API which calls makeReservationExpired then check reservation period validity, return customerId for new/existing customer, check isRental available and finally creates and return new reservationId

14. There is updateReservation API which calls markReservationsExpired, checks reservation period validity and updates customer details if there are any changes. For updating reservation period there is check for isRentalAvailable then update reservation period

15. There is cancelReservation API which calls markReservationsExpired and cancels reservation based on reservationId