package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.registration.form.*;
import org.innovateuk.ifs.util.EncryptedCookieService;
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
    public static final String ORGANISATION_INTERNATIONAL = "organisationInternational";
    public static final String ORGANISATION_INTERNATIONAL_DETAILS = "organisationInternationalDetails";
    public static final String ORGANISATION_FORM = "organisationForm";
    public static final String ORGANISATION_ID = "organisationId";
    public static final String INVITE_HASH = "invite_hash";
    public static final String PROJECT_INVITE_HASH = "project_invite_hash";
    public static final String COMPETITION_ID = "competitionId";

    @Autowired
    private EncryptedCookieService cookieUtil;

    public void saveToOrganisationTypeCookie(OrganisationTypeForm organisationTypeForm, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_TYPE, JsonUtil.getSerializedObject(organisationTypeForm));
    }

    public void saveToOrganisationInternationalCookie(OrganisationInternationalForm organisationInternationalForm, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_INTERNATIONAL, JsonUtil.getSerializedObject(organisationInternationalForm));
    }

    public void saveToOrganisationCreationCookie(OrganisationCreationForm organisationFormForCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationFormForCookie));
    }

    public void saveToOrganisationInternationalDetailsCookie(OrganisationInternationalDetailsForm organisationFormForCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_INTERNATIONAL_DETAILS, JsonUtil.getSerializedObject(organisationFormForCookie));
    }

    public void saveToOrganisationIdCookie(Long id, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, ORGANISATION_ID, String.valueOf(id));
    }

    public void saveToCompetitionIdCookie(Long id, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, COMPETITION_ID, String.valueOf(id));
    }

    public void saveToInviteHashCookie(String hash, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, INVITE_HASH, hash);
    }

    public void saveToProjectInviteHashCookie(InviteAndIdCookie inviteAndIdCookie, HttpServletResponse response) {
        cookieUtil.saveToCookie(response, PROJECT_INVITE_HASH, JsonUtil.getSerializedObject(inviteAndIdCookie));
    }

    public Optional<OrganisationTypeForm> getOrganisationTypeCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_TYPE), OrganisationTypeForm.class));
    }

    public Optional<OrganisationInternationalForm> getOrganisationInternationalCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_INTERNATIONAL), OrganisationInternationalForm.class));
    }

    public Optional<OrganisationCreationForm> getOrganisationCreationCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_FORM), OrganisationCreationForm.class));
    }

    public Optional<OrganisationInternationalDetailsForm> getOrganisationInternationalDetailsValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_INTERNATIONAL_DETAILS), OrganisationInternationalDetailsForm.class));
    }

    public Optional<Long> getOrganisationIdCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, ORGANISATION_ID), Long.class));
    }

    public Optional<Long> getCompetitionIdCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, COMPETITION_ID), Long.class));
    }

    public Optional<String> getInviteHashCookieValue(HttpServletRequest request) {
        String inviteHash = cookieUtil.getCookieValue(request, INVITE_HASH);

        return Optional.ofNullable(inviteHash).filter(s -> !s.isEmpty());
    }

    public Optional<InviteAndIdCookie> getProjectInviteHashCookieValue(HttpServletRequest request) {
        return Optional.ofNullable(getObjectFromJson(cookieUtil.getCookieValue(request, PROJECT_INVITE_HASH), InviteAndIdCookie.class));
    }

    public void deleteOrganisationTypeCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_TYPE);
    }

    public void deleteOrganisationInternationalCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_INTERNATIONAL);
    }

    public void deleteOrganisationCreationCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_FORM);
    }

    public void deleteOrganisationInternationalDetailsCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_INTERNATIONAL_DETAILS);
    }

    public void deleteOrganisationIdCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, ORGANISATION_ID);
    }

    public void deleteInviteHashCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, INVITE_HASH);
    }

    public void deleteCompetitionIdCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, COMPETITION_ID);
    }
    public void deleteProjectInviteHashCookie(HttpServletResponse response) {
        cookieUtil.removeCookie(response, PROJECT_INVITE_HASH);
    }

    public void deleteAllRegistrationJourneyCookies(HttpServletResponse response) {
        deleteOrganisationTypeCookie(response);
        deleteOrganisationCreationCookie(response);
        deleteOrganisationIdCookie(response);
        deleteInviteHashCookie(response);
        deleteCompetitionIdCookie(response);
        deleteProjectInviteHashCookie(response);
    }

    public boolean isCollaboratorJourney(HttpServletRequest request) {
        return (getInviteHashCookieValue(request).isPresent()
                || getProjectInviteHashCookieValue(request).isPresent())
                && !getCompetitionIdCookieValue(request).isPresent();
    }

    public boolean isLeadJourney(HttpServletRequest request) {
        return !isCollaboratorJourney(request);
    }

    public boolean isApplicantJourney(HttpServletRequest request) {
        return !getProjectInviteHashCookieValue(request).isPresent();
    }

    public boolean isInternationalJourney(Optional<OrganisationInternationalForm> organisationInternationalForm) {
        return organisationInternationalForm.isPresent() && organisationInternationalForm.get().getInternational();
    }
}
