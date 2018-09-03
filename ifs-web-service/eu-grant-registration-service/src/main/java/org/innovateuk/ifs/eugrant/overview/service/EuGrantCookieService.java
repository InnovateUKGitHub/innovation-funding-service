package org.innovateuk.ifs.eugrant.overview.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServlet;
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

    public ServiceResult<EuGrantResource> save(EuGrantResource euGrant) {
        Optional<UUID> uuid = getIdFromCookie();
        if (!uuid.isPresent()) {
            UUID id = euGrantRestService.create().getSuccess().getId();
            saveToEuGrantCookie(id);
            euGrant.setId(id);
        } else {
            euGrant.setId(uuid.get());
        }
        return euGrantRestService.update(euGrant)
                .toServiceResult()
                .andOnSuccessReturn(() -> euGrant);
    }

    public void clear() {
        cookieUtil.removeCookie(EuGrantHttpServlet.response(), EU_GRANT_ID);
    }

    private void saveToEuGrantCookie(UUID uuid) {
        cookieUtil.saveToCookie(EuGrantHttpServlet.response(), EU_GRANT_ID, uuid.toString());
    }

    private Optional<UUID> getIdFromCookie() {
        String cookie = cookieUtil.getCookieValue(EuGrantHttpServlet.request(), EU_GRANT_ID);

        if (!cookie.isEmpty()) {
            return Optional.of(UUID.fromString(cookie));
        }

        return Optional.empty();
    }
}
