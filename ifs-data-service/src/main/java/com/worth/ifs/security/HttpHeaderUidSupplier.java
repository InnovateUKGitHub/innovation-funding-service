package com.worth.ifs.security;

import com.worth.ifs.commons.security.UidSupplier;
import com.worth.ifs.commons.security.authentication.token.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class HttpHeaderUidSupplier implements UidSupplier {

    @Override
    public String getUid(HttpServletRequest request) {
        return request.getHeader(Authentication.TOKEN);
    }
}
