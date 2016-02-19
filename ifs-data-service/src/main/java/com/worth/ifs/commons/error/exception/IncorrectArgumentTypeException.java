package com.worth.ifs.commons.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rav on 18/02/2016.
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Argument was of an incorrect type")
public class IncorrectArgumentTypeException extends IllegalArgumentException {
}
