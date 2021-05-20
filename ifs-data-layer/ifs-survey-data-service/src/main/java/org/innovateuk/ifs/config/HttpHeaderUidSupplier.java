package org.innovateuk.ifs.config;

import org.innovateuk.ifs.commons.LibraryCandidate;
import org.innovateuk.ifs.commons.security.UidSupplier;
import org.innovateuk.ifs.commons.security.authentication.token.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
@LibraryCandidate
public class HttpHeaderUidSupplier implements UidSupplier {

    @Override
    public String getUid(HttpServletRequest request) {
        return request.getHeader(Authentication.TOKEN);
    }
}
