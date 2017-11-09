package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;

@Service
public class RegistrationCookieService {

    public static final String ORGANISATION_TYPE = "organisationType";
    public static final String ORGANISATION_FORM = "organisationForm";
    public static final String ORGANISATION_ID = "organisationId";
    public static final String INVITE_HASH = "invite_hash";
    public static final String COMPETITION_ID = "competitionId";

    @Autowired
    private CookieUtil cookieUtil;

    @NotSecured("Not currently secured")
    public void saveToOrganisationTypeCookie(OrganisationTypeForm organisationTypeForm, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_TYPE, JsonUtil.getSerializedObject(organisationTypeForm));
    }

    @NotSecured("Not currently secured")
    public void saveToOrganisationCreationCookie(OrganisationCreationForm organisationFormForCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationFormForCookie));
    }

    @NotSecured("Not currently secured")
    public void saveToOrganisationIdCookie(Long id, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_ID, String.valueOf(id));
    }

    @NotSecured("Not currently secured")
    public void saveToCompetitionIdCookie(Long id, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, COMPETITION_ID, String.valueOf(id));
    }

    @NotSecured("Not currently secured")
    public void saveToInviteHashCookie(String hash, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, INVITE_HASH, hash);
    }

    @NotSecured("Not currently secured")
    public Optional<OrganisationTypeForm> getOrganisationTypeCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_TYPE), OrganisationTypeForm.class));
    }

    @NotSecured("Not currently secured")
    public Optional<OrganisationCreationForm> getOrganisationCreationCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_FORM), OrganisationCreationForm.class));
    }

    @NotSecured("Not currently secured")
    public Optional<Long> getOrganisationIdCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_ID), Long.class));
    }

    @NotSecured("Not currently secured")
    public Optional<Long> getCompetitionIdCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, COMPETITION_ID), Long.class));
    }

    @NotSecured("Not currently secured")
    public Optional<String> getInviteHashCookieValue(HttpServletRequest request) {
        String inviteHash = cookieUtil.getCookieValue(request, INVITE_HASH);

        return Optional.ofNullable(inviteHash).filter(s -> !s.isEmpty());
    }

    @NotSecured("Not currently secured")
    public void deleteOrganisationTypeCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_TYPE);
    }

    @NotSecured("Not currently secured")
    public void deleteOrganisationCreationCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_FORM);
    }

    @NotSecured("Not currently secured")
    public void deleteOrganisationIdCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_ID);
    }

    @NotSecured("Not currently secured")
    public void deleteInviteHashCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, INVITE_HASH);
    }

    @NotSecured("Not currently secured")
    public void deleteCompetitionIdCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, COMPETITION_ID);
    }

    @NotSecured("Not currently secured")
    public void deleteAllRegistrationJourneyCookies(HttpServletResponse response) {
        deleteOrganisationTypeCookie(response);
        deleteOrganisationCreationCookie(response);
        deleteOrganisationIdCookie(response);
        deleteInviteHashCookie(response);
        deleteCompetitionIdCookie(response);
    }
}
