package org.innovateuk.ifs.controller;

import org.innovateuk.ifs.commons.error.Error;
import org.springframework.validation.ObjectError;

import java.util.Optional;
import java.util.function.Function;

/**
 * Explicitly defined interface for a class that can convert an Error into an ObjectError as expected by BindingResults
 */
public interface ErrorToObjectErrorConverter extends Function<Error, Optional<ObjectError>> {
}
