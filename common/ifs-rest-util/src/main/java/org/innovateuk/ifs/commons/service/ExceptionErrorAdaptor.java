package org.innovateuk.ifs.commons.service;

import org.springframework.web.server.ResponseStatusException;

public interface ExceptionErrorAdaptor {

    <T> T toError(ResponseStatusException responseStatusException);

}
