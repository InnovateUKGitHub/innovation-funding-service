package org.innovateuk.ifs.eugrant.organisation.service;

import org.innovateuk.ifs.eugrant.organisation.form.OrganisationCreationForm;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationTypeForm;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class OrganisationCookieService {

    private static final String ORGANISATION_TYPE = "organisationType";
    private static final String ORGANISATION_FORM = "organisationForm";

    @Autowired
    private CookieUtil cookieUtil;

    public void saveToOrganisationTypeCookie(OrganisationTypeForm organisationTypeForm, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_TYPE, JsonUtil.getSerializedObject(organisationTypeForm));
    }

    public void saveToOrganisationCreationCookie(OrganisationCreationForm organisationFormForCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationFormForCookie));
    }

    public Optional<OrganisationTypeForm> getOrganisationTypeCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_TYPE), OrganisationTypeForm.class));
    }

    public Optional<OrganisationCreationForm> getOrganisationCreationCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_FORM), OrganisationCreationForm.class));
    }
}
