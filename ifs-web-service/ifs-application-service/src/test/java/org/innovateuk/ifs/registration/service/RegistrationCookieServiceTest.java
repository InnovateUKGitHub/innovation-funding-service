package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.innovateuk.ifs.registration.service.RegistrationCookieService.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class RegistrationCookieServiceTest extends BaseServiceUnitTest<RegistrationCookieService>{

    @Mock
    private CookieUtil cookieUtil;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;


    protected RegistrationCookieService supplyServiceUnderTest() {
        return new RegistrationCookieService();
    }

    @Before
    public void setUp() {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        super.setUp();
    }

    @Test
    public void saveToOrganisationTypeCookie() throws Exception {
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();

        service.saveToOrganisationTypeCookie(organisationTypeForm, response);

        verify(cookieUtil, times(1)).saveToCookie(response, ORGANISATION_TYPE, JsonUtil.getSerializedObject(organisationTypeForm));
    }

    @Test
    public void saveToOrganisationCreationCookie() throws Exception {
        OrganisationCreationForm organisationForm = new OrganisationCreationForm();


        service.saveToOrganisationCreationCookie(organisationForm, response);

        verify(cookieUtil, times(1)).saveToCookie(response, ORGANISATION_FORM, JsonUtil.getSerializedObject(organisationForm));
    }

    @Test
    public void saveToOrganisationIdCookie() throws Exception {
        Long organisationId = 1L;

        service.saveToOrganisationIdCookie(organisationId, response);

        verify(cookieUtil, times(1)).saveToCookie(response, ORGANISATION_ID, String.valueOf(organisationId));
    }

    @Test
    public void saveToCompetitionIdCookie() throws Exception {
        Long competitionId = 1L;

        service.saveToCompetitionIdCookie(competitionId, response);

        verify(cookieUtil, times(1)).saveToCookie(response, COMPETITION_ID, String.valueOf(competitionId));
    }

    @Test
    public void saveToInviteHashCookie() throws Exception {
        String inviteHash = "123abc";

        service.saveToInviteHashCookie(inviteHash, response);

        verify(cookieUtil, times(1)).saveToCookie(response, INVITE_HASH, inviteHash);
    }

    @Test
    public void getOrganisationTypeCookieValue() throws Exception {
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();

        when(cookieUtil.getCookieValue(request, ORGANISATION_TYPE)).thenReturn(JsonUtil.getSerializedObject(organisationTypeForm));

        Optional<OrganisationTypeForm> result = service.getOrganisationTypeCookieValue(request);

        assertEquals(result.get(),organisationTypeForm);
        verify(cookieUtil, times(1)).getCookieValue(request, ORGANISATION_TYPE);
    }

    @Test
    public void getOrganisationTypeCookieValue_shouldReturnEmptyOptionalOnEmptyCookie() throws Exception {
        when(cookieUtil.getCookieValue(request, ORGANISATION_TYPE)).thenReturn("");

        Optional<OrganisationTypeForm> result = service.getOrganisationTypeCookieValue(request);

        assertFalse(result.isPresent());
        verify(cookieUtil, times(1)).getCookieValue(request, ORGANISATION_TYPE);
    }

    @Test
    public void getOrganisationCreationCookieValue() throws Exception {
        OrganisationCreationForm organisationForm = new OrganisationCreationForm();

        when(cookieUtil.getCookieValue(request, ORGANISATION_FORM)).thenReturn(JsonUtil.getSerializedObject(organisationForm));

        Optional<OrganisationCreationForm> result = service.getOrganisationCreationCookieValue(request);

        assertEquals(result.get(),organisationForm);
        verify(cookieUtil, times(1)).getCookieValue(request, ORGANISATION_FORM);
    }

    @Test
    public void getOrganisationCreationCookieValue_shouldReturnEmptyOptionalOnEmptyCookie() {
        when(cookieUtil.getCookieValue(request, ORGANISATION_FORM)).thenReturn("");

        Optional<OrganisationCreationForm> result = service.getOrganisationCreationCookieValue(request);

        assertFalse(result.isPresent());
        verify(cookieUtil, times(1)).getCookieValue(request, ORGANISATION_FORM);
    }

    @Test
    public void getOrganisationIdCookieValue() throws Exception {
        Long organisationId = 1L;

        when(cookieUtil.getCookieValue(request, ORGANISATION_ID)).thenReturn(String.valueOf(organisationId));

        Optional<Long> result = service.getOrganisationIdCookieValue(request);

        assertEquals(result.get(),organisationId);
        verify(cookieUtil, times(1)).getCookieValue(request, ORGANISATION_ID);
    }

    @Test
    public void getOrganisationIdCookieValue_shouldReturnEmptyOptionalOnEmptyCookie() {
        when(cookieUtil.getCookieValue(request, ORGANISATION_ID)).thenReturn("");

        Optional<Long> result = service.getOrganisationIdCookieValue(request);

        assertFalse(result.isPresent());
        verify(cookieUtil, times(1)).getCookieValue(request, ORGANISATION_ID);
    }

    @Test
    public void getCompetitionIdCookieValue() throws Exception {
        Long competitionId = 1L;
        when(cookieUtil.getCookieValue(request, COMPETITION_ID)).thenReturn(String.valueOf(competitionId));

        Optional<Long> result = service.getCompetitionIdCookieValue(request);

        assertEquals(result.get(),competitionId);
        verify(cookieUtil, times(1)).getCookieValue(request, COMPETITION_ID);
    }

    @Test
    public void getCompetitionIdCookieValue_shouldReturnEmptyOptionalOnEmptyCookie() {
        when(cookieUtil.getCookieValue(request, COMPETITION_ID)).thenReturn("");

        Optional<Long> result = service.getCompetitionIdCookieValue(request);

        assertFalse(result.isPresent());
        verify(cookieUtil, times(1)).getCookieValue(request, COMPETITION_ID);
    }

    @Test
    public void getInviteHashCookieValue() throws Exception {
        String inviteHash = "123abc";

        when(cookieUtil.getCookieValue(request, INVITE_HASH)).thenReturn(inviteHash);

        Optional<String> result = service.getInviteHashCookieValue(request);

        assertEquals(result.get(),inviteHash);
        verify(cookieUtil, times(1)).getCookieValue(request, INVITE_HASH);
    }

    @Test
    public void getInviteHashCookieValue_shouldReturnEmptyOptionalOnEmptyCookie() {
        when(cookieUtil.getCookieValue(request, INVITE_HASH)).thenReturn("");

        Optional<String> result = service.getInviteHashCookieValue(request);

        assertFalse(result.isPresent());
        verify(cookieUtil, times(1)).getCookieValue(request, INVITE_HASH);
    }

    @Test
    public void deleteOrganisationTypeCookie() throws Exception {
        service.deleteOrganisationTypeCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, ORGANISATION_TYPE);
    }

    @Test
    public void deleteOrganisationCreationCookie() throws Exception {
        service.deleteOrganisationCreationCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, ORGANISATION_FORM);
    }

    @Test
    public void deleteOrganisationIdCookie() throws Exception {
        service.deleteOrganisationIdCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, ORGANISATION_ID);
    }

    @Test
    public void deleteInviteHashCookie() throws Exception {
        service.deleteInviteHashCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, INVITE_HASH);
    }

    @Test
    public void deleteCompetitionIdCookie() throws Exception {
        service.deleteCompetitionIdCookie(response);

        verify(cookieUtil, times(1)).removeCookie(response, COMPETITION_ID);
    }

    @Test
    public void deleteAllRegistrationJourneyCookies() throws Exception {
        service.deleteAllRegistrationJourneyCookies(response);

        verify(cookieUtil, times(1)).removeCookie(response, ORGANISATION_TYPE);
        verify(cookieUtil, times(1)).removeCookie(response, ORGANISATION_FORM);
        verify(cookieUtil, times(1)).removeCookie(response, ORGANISATION_ID);
        verify(cookieUtil, times(1)).removeCookie(response, COMPETITION_ID);
        verify(cookieUtil, times(1)).removeCookie(response, INVITE_HASH);
    }
}