package com.Exceptions;

public class SeatHoldNoLongerValidException extends RuntimeException {

        public SeatHoldNoLongerValidException(int seatHoldId) {
            super("Sorry the seat hold " +seatHoldId+ "expired ");
        }
}
