package org.innovateuk.ifs.config.security;

import org.apache.tika.io.IOUtils;
import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;

/**
 * Responsible for authenticating requests by inspecting the request header for a valid authentication token.
 */
@Service
public class TokenAuthenticationService {

    private String secretKey;
    private HashBasedMacTokenHandler hashBasedMacTokenHandler;

    @Autowired
    public TokenAuthenticationService(@Value("${ifs.finance-totals.authSecretKey}") String secretKey,
                                      HashBasedMacTokenHandler tokenHandler) {
        this.secretKey = secretKey;
        this.hashBasedMacTokenHandler = tokenHandler;
    }

    public AuthenticationToken getAuthentication(HttpServletRequest request) {
        if (isHashValidForRequest(request)) {
            return new AuthenticationToken();
        }
        return null;
    }

    private boolean isHashValidForRequest(HttpServletRequest request) {
        String token = getTokenFromRequestHeader(request);
        if (token != null) {
            try {
                return token.equals(
                        hashBasedMacTokenHandler.calculateHash(secretKey, getContentAsString(request)));
            } catch (InvalidKeyException e) {
                throw new IllegalStateException("Caught InvalidKeyException while trying to calculate hash", e);
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException while trying to retrieve request content", e);
            }
        }
        return false;
    }

    private String getTokenFromRequestHeader(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    private String getContentAsString(HttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getReader());
    }
}
