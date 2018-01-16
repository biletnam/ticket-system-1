package com.Exceptions;

public class SeatNotAvailableException extends RuntimeException {

        public SeatNotAvailableException() {
            super("Sorry all seats are filled up.");
        }
}
