package com.worth.ifs.security;

import com.worth.ifs.commons.security.TokenAuthenticationService;
import com.worth.ifs.commons.security.TokenSupplier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Component
public class HttpHeaderTokenSupplier implements TokenSupplier {

    @Override
    public String getToken(HttpServletRequest request) {
        return request.getHeader(TokenAuthenticationService.AUTH_TOKEN);
    }
}
