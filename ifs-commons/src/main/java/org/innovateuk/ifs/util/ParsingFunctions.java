package org.innovateuk.ifs.util;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNABLE_TO_PARSE_LONG;

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
