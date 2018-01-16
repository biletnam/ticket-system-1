package com;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.Instant;


@JsonIgnoreProperties
@Entity
public class SeatHold {


    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @SequenceGenerator(name="seat_generator", sequenceName = "seat_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_generator")
    private int id;
    private String customerEmail;
    private long numberOfSeats;
    private boolean isExpired;
    private String status;
    private Instant holdPlacedTime;



    public SeatHold() {

    }


    public SeatHold (long numSeats, String customerEmail, String status) {
        this.numberOfSeats = numSeats;
        this.customerEmail = customerEmail;
        this.status = status;
    }

    @PrePersist
    public void init() {
        holdPlacedTime = Instant.now();

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Long getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Long numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getHoldPlacedTime() {
        return holdPlacedTime;
    }

    public void setHoldPlacedTime(Instant holdPlacedTime) {
        this.holdPlacedTime = holdPlacedTime;
    }


}
