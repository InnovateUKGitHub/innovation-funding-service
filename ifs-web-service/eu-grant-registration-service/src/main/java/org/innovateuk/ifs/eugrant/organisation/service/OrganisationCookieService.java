package org.innovateuk.ifs.eugrant.organisation.service;

import org.innovateuk.ifs.eugrant.organisation.form.OrganisationTypeForm;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class OrganisationCookieService {

    private static final String ORGANISATION_TYPE = "organisationType";

    @Autowired
    private CookieUtil cookieUtil;

    public void saveToOrganisationTypeCookie(OrganisationTypeForm organisationTypeForm) {
        cookieUtil.saveToCookie(response(), ORGANISATION_TYPE, JsonUtil.getSerializedObject(organisationTypeForm));
    }

    public Optional<OrganisationTypeForm> getOrganisationTypeCookieValue() {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request(), ORGANISATION_TYPE), OrganisationTypeForm.class));
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
