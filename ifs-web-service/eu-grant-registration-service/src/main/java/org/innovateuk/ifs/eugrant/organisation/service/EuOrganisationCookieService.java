package org.innovateuk.ifs.eugrant.organisation.service;

import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationTypeForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantHttpServlet;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class EuOrganisationCookieService {

    private static final String ORGANISATION_TYPE = "euOrganisationType";

    @Autowired
    private CookieUtil cookieUtil;

    public void saveToOrganisationTypeCookie(EuOrganisationTypeForm organisationTypeForm) {
        cookieUtil.saveToCookie(EuGrantHttpServlet.response(), ORGANISATION_TYPE, JsonUtil.getSerializedObject(organisationTypeForm));
    }

    public Optional<EuOrganisationTypeForm> getOrganisationTypeCookieValue() {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(EuGrantHttpServlet.request(), ORGANISATION_TYPE), EuOrganisationTypeForm.class));
    }

    public void clear() {
        cookieUtil.removeCookie(EuGrantHttpServlet.response(), ORGANISATION_TYPE);
    }

}
