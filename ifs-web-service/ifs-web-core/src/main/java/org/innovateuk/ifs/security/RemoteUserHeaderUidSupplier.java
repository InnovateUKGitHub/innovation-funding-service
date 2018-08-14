package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UidSupplier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Component
public class RemoteUserHeaderUidSupplier implements UidSupplier {

    @Override
    public String getUid(HttpServletRequest request) {
        return request.getHeader("x-remote-user");
    }
}
