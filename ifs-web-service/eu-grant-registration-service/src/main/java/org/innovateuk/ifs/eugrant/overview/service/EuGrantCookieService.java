package org.innovateuk.ifs.eugrant.overview.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eu.grant.EuGrantRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class EuGrantCookieService {

    private static final String EU_GRANT_ID = "EU_GRANT_ID";
    private static final String PREVIOUS_EU_GRANT = "PREVIOUS_EU_GRANT";

    @Autowired
    private EuGrantRestService euGrantRestService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private EuGrantHttpServlet euGrantHttpServlet;


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

    public void setPreviouslySubmitted(EuGrantResource euGrantResource) {
        cookieUtil.saveToCookie(euGrantHttpServlet.response(), PREVIOUS_EU_GRANT, JsonUtil.getSerializedObject(euGrantResource));
    }

    public Optional<EuGrantResource> getPreviouslySubmitted() {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(euGrantHttpServlet.request(), PREVIOUS_EU_GRANT), EuGrantResource.class));
    }

    private void saveToEuGrantCookie(UUID uuid) {
        cookieUtil.saveToCookie(euGrantHttpServlet.response(), EU_GRANT_ID, uuid.toString());
    }

    private Optional<UUID> getIdFromCookie() {
        String cookie = cookieUtil.getCookieValue(euGrantHttpServlet.request(), EU_GRANT_ID);

        if (!cookie.isEmpty()) {
            return Optional.of(UUID.fromString(cookie));
        }

        return Optional.empty();
    }

    public void clear() {
        cookieUtil.removeCookie(euGrantHttpServlet.response(), EU_GRANT_ID);
    }
}
