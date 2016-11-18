package com.worth.ifs.util;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.Optional;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_UNABLE_TO_PARSE_LONG;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * A utility class providing handy methods around parsing.
 */
public final class ParsingFunctions {

	private ParsingFunctions() {}
	
    public static Optional<Long> validLong(String string) {
        try {
            return Optional.of(Long.parseLong(string));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static ServiceResult<Long> validLongResult(String string) {
        try {
            return ServiceResult.serviceSuccess(Long.parseLong(string));
        } catch (NumberFormatException e) {
            return ServiceResult.serviceFailure(new Error(GENERAL_UNABLE_TO_PARSE_LONG, string));
        }
    }
}
