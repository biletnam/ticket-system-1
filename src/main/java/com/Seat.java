package com;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties
@Entity


public class Seat {


    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @SequenceGenerator(name="seat_generator", sequenceName = "seat_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_generator")
    private Long id;
    private Integer number;
    private String status;
    private String reservationCode;
    private Integer seatHoldId;

    public Seat() {

    }

    public Seat(Integer number, String status, Integer seatHoldId) {
        this.number = number;
        this.status = status;
        this.seatHoldId = seatHoldId;
    }


    public Integer getSeatHoldId() {
        return seatHoldId;
    }

    public void setSeatHoldId(Integer seatHoldId) {
        this.seatHoldId = seatHoldId;
    }



    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*public SeatHold getSeatHold() {
        return seatHold;
    }

    public void setSeatHold(SeatHold seatHold) {
        this.seatHold = seatHold;
    }*/

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }



    /*public int getSeatHoldId() { return seatHoldId; }

    public void setSeatHoldId(int seatHoldId) { this.seatHoldId = seatHoldId; }*/



}
