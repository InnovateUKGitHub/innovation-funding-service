package org.innovateuk.ifs.cache;


import org.springframework.stereotype.Component;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Component
public class RequestUidSupplier implements UidSupplier {

    private static final String REQUEST_UID_KEY = "REQUEST_UID_KEY";

    @Override
    public String get() {
        return (String)getRequestAttributes().getAttribute(REQUEST_UID_KEY, SCOPE_REQUEST);

    }
}
