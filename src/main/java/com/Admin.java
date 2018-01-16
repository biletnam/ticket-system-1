package com;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties
@Entity
public class Admin {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @SequenceGenerator(name="admin_generator", sequenceName = "admin_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_generator")
    private long id;
    private int totalNumberOfSeats;
    private int expiryTimeInMinutes;
    private String bestSeatCriteria;

    public Admin() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTotalNumberOfSeats() {
        return totalNumberOfSeats;
    }

    public void setTotalNumberOfSeats(int totalNumberOfSeats) {
        this.totalNumberOfSeats = totalNumberOfSeats;
    }

    public int getExpiryTimeInMinutes() {
        return expiryTimeInMinutes;
    }

    public void setExpiryTimeInMinutes(int expiryTimeInMinutes) {
        this.expiryTimeInMinutes = expiryTimeInMinutes;
    }

    public String getBestSeatCriteria() {
        return bestSeatCriteria;
    }

    public void setBestSeatCriteria(String bestSeatCriteria) {
        this.bestSeatCriteria = bestSeatCriteria;
    }

}
