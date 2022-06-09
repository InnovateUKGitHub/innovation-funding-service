package org.innovateuk.ifs.commons.service;

import org.springframework.web.server.ResponseStatusException;

/**
 * see ServiceResult.serviceFailure(ResponseStatusException responseStatusException,
 *                                  ExceptionErrorAdaptor exceptionErrorAdaptor)
 */
public interface ExceptionErrorAdaptor {

    <T> T toError(ResponseStatusException responseStatusException);

}
