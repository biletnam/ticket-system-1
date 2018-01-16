package com.service.impl;

import com.service.BestSeatFindingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BestSeatFindingServiceImpl implements BestSeatFindingService {

       public List<Integer> findBestSeatsDecremental(List<Integer> filledSeats, int noOfSeats, int totalSeats){
            List<Integer> remainingSeats = new ArrayList<Integer>();
            List<Integer> seatsToBeChecked = new ArrayList<Integer>();
            for(int i=totalSeats; i>0; i--){
                if(filledSeats.contains(i)) continue;
                remainingSeats.add(i);
                seatsToBeChecked.add(i);
                if(seatsToBeChecked.size() == noOfSeats){
                    if(areConsecutiveNumbers(seatsToBeChecked)){
                        return seatsToBeChecked;
                    }else{
                        seatsToBeChecked.remove(noOfSeats-1);
                    }
                }
            }

            return remainingSeats.subList(0, noOfSeats);
        }

        public List<Integer> findBestSeatsIncremental(List<Integer> filledSeats, int noOfSeats, int totalSeats){
            List<Integer> remainingSeats = new ArrayList<Integer>();
            List<Integer> seatsToBeChecked = new ArrayList<Integer>();
            for(int i=1; i<=totalSeats; i++){
                if(filledSeats.contains(i)) continue;
                remainingSeats.add(i);
                seatsToBeChecked.add(i);
                if(seatsToBeChecked.size() == noOfSeats){
                    if(areConsecutiveNumbers(seatsToBeChecked)){
                        return seatsToBeChecked;
                    }else{
                    	seatsToBeChecked.remove(0);
                    }
                }
            }

            return remainingSeats.subList(0, noOfSeats);
        }

        public boolean areConsecutiveNumbers(List<Integer> numbers){
            int length = numbers.size();
            Collections.sort(numbers);
            return (numbers.get(length-1)-numbers.get(0)+1 == length);
        }


 }

