package com.controller;

import com.Exceptions.SeatHoldNoLongerValidException;
import com.Exceptions.SeatNotAvailableException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TicketSystemControllerAdvice {
        @ResponseBody
        @ExceptionHandler(SeatNotAvailableException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        VndErrors SeatNotAvailableExceptionHandler(SeatNotAvailableException ex) {
            return new VndErrors("error", ex.getMessage());
        }

        @ResponseBody
        @ExceptionHandler(SeatHoldNoLongerValidException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        VndErrors SeatHoldNoLongerValidExceptionHandler(SeatHoldNoLongerValidException ex) {
        return new VndErrors("error", ex.getMessage());
        }
}

