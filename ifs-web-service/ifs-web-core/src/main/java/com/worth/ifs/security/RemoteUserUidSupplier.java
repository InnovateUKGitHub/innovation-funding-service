package com.worth.ifs.security;

import com.worth.ifs.commons.security.UidSupplier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Component
public class RemoteUserUidSupplier implements UidSupplier {

    @Override
    public String getUid(HttpServletRequest request) {
        return request.getRemoteUser();
    }
}
