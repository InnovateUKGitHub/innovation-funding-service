package org.innovateuk.ifs.eugrant.service;

import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
public class EuGrantCookieService {

    private static final String EU_GRANT_ID = "EU_GRANT_ID";

    @Autowired
    private EuGrantRestService euGrantRestService;

    @Autowired
    private CookieUtil cookieUtil;

    public EuGrantResource get() {
        Optional<UUID> uuid = getIdFromCookie();
        if (!uuid.isPresent()) {
            return new EuGrantResource();
        } else {
            return euGrantRestService.findById(uuid.get()).getSuccess();
        }
    }

    public EuGrantResource save(EuGrantResource euGrant) {
        Optional<UUID> uuid = getIdFromCookie();
        if (!uuid.isPresent()) {
            UUID id = euGrantRestService.create().getSuccess().getId();
            saveToEuGrantCookie(id);
            euGrant.setId(id);
        } else {
            euGrant.setId(uuid.get());
        }
        euGrantRestService.update(euGrant).getSuccess();
        return euGrant;
    }

    public void clear() {
        cookieUtil.removeCookie(response(), EU_GRANT_ID);
    }

    private void saveToEuGrantCookie(UUID uuid) {
        cookieUtil.saveToCookie(response(), EU_GRANT_ID, uuid.toString());
    }

    private Optional<UUID> getIdFromCookie() {
//        return Optional.ofNullable(cookieUtil.getCookieValue(request(), EU_GRANT_ID))
//                .map(UUID::fromString);
//
        String cookieValue = cookieUtil.getCookieValue(request(), EU_GRANT_ID);

        if (cookieValue != "") {
            return Optional.of(UUID.fromString(cookieValue));
        }

        return Optional.empty();
    }

    private HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
    }

    private HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
    }
}
