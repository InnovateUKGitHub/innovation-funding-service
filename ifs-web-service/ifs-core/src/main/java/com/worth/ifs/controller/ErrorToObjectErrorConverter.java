package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import org.springframework.validation.ObjectError;

import java.util.function.Function;

/**
 * Explicitly defined interface for a class that can convert an Error into an ObjectError as expected by BindingResults
 */
public interface ErrorToObjectErrorConverter extends Function<Error, ObjectError> {
}
