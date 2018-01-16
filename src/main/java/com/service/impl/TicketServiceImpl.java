package com.service.impl;

import com.*;
import com.Exceptions.SeatHoldNoLongerValidException;
import com.Exceptions.SeatNotAvailableException;
import com.repo.AdminRepository;
import com.repo.SeatHoldRepository;
import com.repo.SeatRepository;
import com.service.BestSeatFindingService;
import com.service.TicketService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TicketServiceImpl implements TicketService {


    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BestSeatFindingService bestSeatFindingService;

    @Value("${total.seats}")
    private Integer defaultTotalNumberOfSeats;

    @Value("${hold.expiry.time}")
    private Integer defaultExpiryTimeInMinutes;

    @Value("${best.Seat.Criteria}")
    private String defaultBestSeatCriteria;




    @Override
    public Admin saveAdminProperties(Admin admin) {

        return adminRepository.save(admin);
    }




    /**
     * The number of seats in the venue that are neither held nor reserved
     *
     * @return the number of tickets available in the venue
     */
    public int numSeatsAvailable(){

        Integer filledSeats = getFilledSeats().size();
        Integer totalSeats = getTotalNumberOfSeats();

        return (totalSeats - filledSeats.intValue()) ;

    }
    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related
    information
     */
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) throws SeatNotAvailableException{

        return holdSeats(numSeats, customerEmail);

    }

    public void checkForExpiryOfHoldSeats() {
        Instant currentTime = Instant.now();
        Instant holdExpiryCutOfTime = currentTime.minus(getExpiryTime(), ChronoUnit.MINUTES);
        List<SeatHold> expiredSeatHoldList = seatHoldRepository.findByHoldStatusExpiry(SeatStatus.HOLD.toString(), holdExpiryCutOfTime);

        if(expiredSeatHoldList != null && expiredSeatHoldList.size() > 0) {
            updateExpireHoldReleaseSeats(expiredSeatHoldList);
        }

    }


    public void updateExpireHoldReleaseSeats(List<SeatHold> expiredSeatHoldList) {

        String reservationCode = null;

        for(SeatHold seatHold : expiredSeatHoldList) {
            seatHold.setStatus(SeatStatus.EXPIRED.toString());
            seatHoldRepository.save(seatHold);
        }

        int[]  expiredSeatHoldIds = expiredSeatHoldList.stream().mapToInt(x -> x.getId()).toArray();

        for(int seatHoldId : expiredSeatHoldIds) {
            updateSeat(seatHoldId, SeatStatus.FREE.toString(), reservationCode);
        }
    }


    public void updateSeat(Integer seatHoldId, String status, String reservationCode) {

        List<Seat> seatHoldList = seatRepository.findBySeatHoldId(seatHoldId);

        if(seatHoldList != null && seatHoldList.size() > 0) {
            seatHoldList = seatHoldList.stream().map( x -> updateSeatStatus(x, status, reservationCode)).collect(Collectors.toList());
        }

        seatRepository.save(seatHoldList);

    }

    public Seat updateSeatStatus(Seat seat, String status, String reservationCode) {
            if(seat != null) {
                seat.setSeatHoldId(null);
                seat.setStatus(status);
                seat.setReservationCode(reservationCode);
            }
            return seat;
    }


    SeatHold holdSeats(int numSeats, String customerEmail) {

        String bestSeatCriteria = getBestSeatCriteria();
        List<Seat> filledSeats =  getFilledSeats();
        Integer totalSeats = getTotalNumberOfSeats();
        Integer availableSeats = totalSeats - filledSeats.size();
        SeatHold seatHold = null;

        if(availableSeats >= numSeats) {
            seatHold = new SeatHold(numSeats, customerEmail, SeatStatus.HOLD.toString());
            seatHold = seatHoldRepository.save(seatHold);
            List<Seat> seatList = findSeats(numSeats, seatHold, getSeatNumberList(filledSeats), bestSeatCriteria, totalSeats);
            seatRepository.save(seatList);
            return seatHold;

        } else {
            throw new SeatNotAvailableException();
        }
    }


    List<Seat> findSeats(int numSeats, SeatHold seatHold, List<Integer> reservedSeatNumbers, String bestSeatCriteria, int totalSeats) {

        List<Integer> seatNumbersToHoldList;
        if(bestSeatCriteria.equalsIgnoreCase(BestSeatCriteria.TOPTOBOTTOM.toString())) {
            seatNumbersToHoldList = bestSeatFindingService.findBestSeatsDecremental(reservedSeatNumbers, numSeats, totalSeats);
        } else {
            seatNumbersToHoldList = bestSeatFindingService.findBestSeatsIncremental(reservedSeatNumbers, numSeats, totalSeats);
        }

        return createHoldOnSeats(seatNumbersToHoldList, seatHold);

    }


    List<Seat> createHoldOnSeats(List<Integer> seatNumbersToHoldList, SeatHold seatHold) {
        List<Seat> holdSeatList = new ArrayList<>();
        List<Seat> existingSeatList = seatRepository.findByNumberIn(seatNumbersToHoldList);
        if(existingSeatList != null && existingSeatList.size() > 0) {
            existingSeatList.forEach(x -> {x.setStatus(SeatStatus.HOLD.toString()); x.setSeatHoldId(seatHold.getId());});
            holdSeatList.addAll(existingSeatList);
            List<Integer> existingSeatNumberList = getSeatNumberList(existingSeatList);
            if(seatNumbersToHoldList.size() > existingSeatNumberList.size()){
                List<Integer> newSeatList = ListUtils.subtract(seatNumbersToHoldList, existingSeatNumberList);
                createNewHoldSeats(holdSeatList, newSeatList, seatHold);
            }

        } else {
            createNewHoldSeats(holdSeatList, seatNumbersToHoldList, seatHold);
        }


        return holdSeatList;
    }


    public void createNewHoldSeats(List<Seat> holdSeatList, List<Integer> newSeatsReserved, SeatHold seatHold) {
        for(Integer seatNumber : newSeatsReserved) {
            Seat seat = new Seat(seatNumber, SeatStatus.HOLD.toString(), seatHold.getId());
            holdSeatList.add(seat);
        }
    }


   /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId the seat hold identifier
     * @param customerEmail the email address of the customer to which the
    seat hold is assigned
     * @return a reservation confirmation code
     */
   public String reserveSeats(int seatHoldId, String customerEmail) {
           Boolean seatHoldStatus = checkValidSeatHold(seatHoldId);
           if(seatHoldStatus) {
               String reservationCode= generateRandomUUID();
               updateSeat(seatHoldId, SeatStatus.RESERVED.toString(), reservationCode);
               //triggerCustomerEmail(customerEmail, reservationCode);
               return reservationCode;
           } else {
               throw new SeatHoldNoLongerValidException(seatHoldId);
           }

    }

    public boolean checkValidSeatHold(int seatHoldId) {
        SeatHold seatHold = seatHoldRepository.findOne(seatHoldId);
        Instant currentTime = Instant.now();
        int holdExpiryTime = getExpiryTime();
        Instant holdExpiryCutOfTime = currentTime.minus(holdExpiryTime, ChronoUnit.MINUTES);
        if(seatHold != null) {
            if (seatHold.getHoldPlacedTime().isAfter(holdExpiryCutOfTime)) {
                updateSeatHoldStatus(seatHold, SeatStatus.RESERVED.toString());
                return true;
            } else {
                updateSeatHoldStatus(seatHold, SeatStatus.EXPIRED.toString());
                return false;
            }
        }

        return false;
    }



    public void updateSeatHoldStatus(SeatHold seatHold, String status) {
        seatHold.setStatus(status);
        seatHoldRepository.save(seatHold);
    }


    public String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }


   public  List<Integer> getSeatNumberList(List<Seat> seatList) {
       return seatList.stream().map(x -> x.getNumber()).collect(Collectors.toList());
    }

    private Admin getAdmin() {
        return adminRepository.findFirstByOrderByIdDesc();
    }


    /**
     * The number of seats in the venue that are neither held nor reserved
     *
     * @return the number of tickets available in the venue
     */
    public int getTotalNumberOfSeats() {
        Admin admin = getAdmin();

        if(admin != null){
            return admin.getTotalNumberOfSeats();
        } else {
            return defaultTotalNumberOfSeats;
        }

    }


    /**
     * The number of seats in the venue that are neither held nor reserved
     *
     * @return the number of tickets available in the venue
     */
    public int getExpiryTime() {
        Admin admin = getAdmin();
        if(admin != null){
            return admin.getExpiryTimeInMinutes();
        } else {
            return defaultExpiryTimeInMinutes;
        }
    }


    /**
     * The number of seats in the venue that are neither held nor reserved
     *
     * @return the number of tickets available in the venue
     */
    public String getBestSeatCriteria(){
        Admin admin = getAdmin();

        if(admin != null){
            return admin.getBestSeatCriteria();
        } else {
            return defaultBestSeatCriteria;
        }

    }


    public List<Seat> getFilledSeats(){

        checkForExpiryOfHoldSeats();
        List<Seat> filledSeats = seatRepository.findByStatusIn(Arrays.asList(new String[]{SeatStatus.HOLD.toString(), SeatStatus.RESERVED.toString()}));
        return filledSeats;

    }
}
