package com.controller;

import com.Admin;
import com.Exceptions.SeatHoldNoLongerValidException;
import com.Seat;
import com.SeatHold;
import com.repo.SeatHoldRepository;
import com.repo.SeatRepository;
import com.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticketSystem")
public class TicketSystemController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    private static final String SEATHOLDID = "seatHoldId";
    private static final String CUSTOMEREMAIL = "customerEmail";
    private static final String NUMSEATS = "numSeats";


    @RequestMapping(value = "/retreiveTotalSeats", method = RequestMethod.GET, produces = "application/Json")
    public Integer getTotalSeats() {
        return ticketService.numSeatsAvailable();
    }

    @RequestMapping(value = "/retreiveSeats", method = RequestMethod.GET, produces = "application/Json")
    public List<Seat> getSeats() {
        return seatRepository.findAll();
    }

    @RequestMapping(value = "/retreiveSeatHold", method = RequestMethod.GET, produces = "application/Json")
    public List<SeatHold> getSeatHolds() {
        return seatHoldRepository.findAll();
    }

    @RequestMapping(value = "/setAdminValues", method = RequestMethod.POST, produces = "application/Json")
    public ResponseEntity<Admin> setAdminProperties(@RequestBody Admin admin) {


            Admin result = ticketService.saveAdminProperties(admin);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(result.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);

        return new ResponseEntity<>(result, responseHeaders,  HttpStatus.CREATED);




    }

    @RequestMapping(value = "/addHold", method = RequestMethod.POST, produces = "application/Json")
    public ResponseEntity<?> addHold(@RequestBody Map<String, String> req) {

        if (isValidReq(req, NUMSEATS)) {

            SeatHold seatHold = ticketService.findAndHoldSeats(Integer.parseInt(req.get("numSeats")), req.get("customerEmail"));
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(seatHold.getId()).toUri();

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setLocation(location);

            return new ResponseEntity<>(seatHold, responseHeaders,  HttpStatus.CREATED);
        }

        return ResponseEntity.noContent().build();

    }
    /**
     * Commit seats held for a specific customer

     * @return a reservation confirmation code
     */

    @RequestMapping(value = "/reserve", method = RequestMethod.POST, produces = "application/Json")
    public ResponseEntity<?> reserve(@RequestBody Map<String, String> reserveReq) {

        if (isValidReq(reserveReq, SEATHOLDID)) {

            String resvConfirmationCode = ticketService.reserveSeats(Integer.parseInt(reserveReq.get("seatHoldId")), reserveReq.get("customerEmail"));

            return new ResponseEntity<>(resvConfirmationCode,  HttpStatus.CREATED);


        }

        throw new SeatHoldNoLongerValidException(Integer.parseInt(reserveReq.get("seatHoldId")));

    }



    public boolean isValidReq(Map<String, String> reserveReq, String key) {

        if (reserveReq != null && reserveReq.size() > 1 && reserveReq.containsKey(key) && reserveReq.containsKey(CUSTOMEREMAIL)) {

            try {
                Integer seatHoldId = Integer.parseInt(reserveReq.get(key));
                String customerEmail = reserveReq.get(CUSTOMEREMAIL);
                if (seatHoldId > 0 && customerEmail != null && !customerEmail.isEmpty()) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {

                return false;
            }

        } else {
            return false;
        }
    }





}
