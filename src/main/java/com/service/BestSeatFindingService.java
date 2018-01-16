package com.service;

import java.util.List;

public interface BestSeatFindingService {

    List<Integer> findBestSeatsDecremental(List<Integer> filledSeats, int noOfSeats, int totalSeats);
    List<Integer> findBestSeatsIncremental(List<Integer> filledSeats, int noOfSeats, int totalSeats);
}
