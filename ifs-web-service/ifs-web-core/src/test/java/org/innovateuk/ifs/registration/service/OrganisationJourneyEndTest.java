package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Class for functionality in {@link OrganisationJourneyEnd}
 */
public class OrganisationJourneyEndTest extends BaseServiceUnitTest<OrganisationJourneyEnd> {

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompaniesHouseRestService companiesHouseRestService;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void completeProcess_newUser() {
        UserResource user = null;
        long organisationId = 1L;

        OrganisationResource organisation = newOrganisationResource().withId(organisationId).withCompaniesHouseNumber("1").build();
        OrganisationSearchResult organisationSearchResult  = new OrganisationSearchResult();

        when(organisationRestService.getOrganisationById(organisationId))
                .thenReturn(restSuccess(organisation));
        when(companiesHouseRestService.getOrganisationById("1"))
                .thenReturn(restSuccess(organisationSearchResult));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, "redirect:/registration/register");
        verify(registrationCookieService).saveToOrganisationIdCookie(organisationId, response);
    }

    @Test
    public void completeProcess_existingLead_teamQuestion() {
        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        long organisationId = 1L;
        long competitionId = 2L;

        OrganisationResource organisation = newOrganisationResource().withId(organisationId).withCompaniesHouseNumber("1").build();
        OrganisationSearchResult organisationSearchResult  = new OrganisationSearchResult();

        when(organisationRestService.getOrganisationById(organisationId))
                .thenReturn(restSuccess(organisation));
        when(companiesHouseRestService.getOrganisationById("1"))
                .thenReturn(restSuccess(organisationSearchResult));

        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(true);
        when(registrationCookieService.getCompetitionIdCookieValue(request)).thenReturn(Optional.of(competitionId));
        when(applicationRestService.createApplication(competitionId, user.getId(), organisationId, ""))
                .thenReturn(restSuccess(application));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s",
                application.getId()));
        verify(applicationRestService).createApplication(competitionId, user.getId(), organisationId, "");
    }

    @Test
    public void completeProcess_existingLead_noQuestion() {
        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        long organisationId = 1L;
        long competitionId = 2L;

        OrganisationResource organisation = newOrganisationResource().withId(organisationId).withCompaniesHouseNumber("1").build();
        OrganisationSearchResult organisationSearchResult  = new OrganisationSearchResult();

        when(organisationRestService.getOrganisationById(organisationId))
                .thenReturn(restSuccess(organisation));
        when(companiesHouseRestService.getOrganisationById("1"))
                .thenReturn(restSuccess(organisationSearchResult));

        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(true);
        when(registrationCookieService.getCompetitionIdCookieValue(request)).thenReturn(Optional.of(competitionId));
        when(applicationRestService.createApplication(competitionId, user.getId(), organisationId, ""))
                .thenReturn(restSuccess(application));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s", application.getId()));
        verify(applicationRestService).createApplication(competitionId, user.getId(), organisationId, "");
    }

    @Test
    public void completeProcess_existingCollaborator() {
        UserResource user = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        long organisationId = 1L;
        long applicationId = 2L;

        OrganisationResource organisation = newOrganisationResource().withId(organisationId).withCompaniesHouseNumber("1").build();
        OrganisationSearchResult organisationSearchResult  = new OrganisationSearchResult();

        when(organisationRestService.getOrganisationById(organisationId))
                .thenReturn(restSuccess(organisation));
        when(companiesHouseRestService.getOrganisationById("1"))
                .thenReturn(restSuccess(organisationSearchResult));

        String inviteHash = "inviteHash";
        ApplicationInviteResource invite = newApplicationInviteResource().withApplication(applicationId).build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(true);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(false);
        when(registrationCookieService.getInviteHashCookieValue(request)).thenReturn(Optional.of(inviteHash));
        when(inviteRestService.getInviteByHash(inviteHash)).thenReturn(restSuccess(invite));
        when(inviteRestService.acceptInvite(inviteHash, user.getId(), organisationId)).thenReturn(restSuccess());

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s", applicationId));
        verify(registrationCookieService).deleteInviteHashCookie(response);
        verify(inviteRestService).acceptInvite(inviteHash, user.getId(), organisationId);
    }

    @Test
    public void completeProcess_existingAssessor() {
        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.ASSESSOR)).build();
        long organisationId = 1L;
        long competitionId = 2L;

        OrganisationResource organisation = newOrganisationResource().withId(organisationId).withCompaniesHouseNumber("1").build();
        OrganisationSearchResult organisationSearchResult  = new OrganisationSearchResult();

        when(organisationRestService.getOrganisationById(organisationId))
                .thenReturn(restSuccess(organisation));
        when(companiesHouseRestService.getOrganisationById("1"))
                .thenReturn(restSuccess(organisationSearchResult));

        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(true);
        when(userRestService.grantRole(user.getId(), Role.APPLICANT)).thenReturn(restSuccess());
        when(registrationCookieService.getCompetitionIdCookieValue(request)).thenReturn(Optional.of(competitionId));
        when(applicationRestService.createApplication(competitionId, user.getId(), organisationId, ""))
                .thenReturn(restSuccess(application));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s", application.getId()));
        verify(applicationRestService).createApplication(competitionId, user.getId(), organisationId, "");
        verify(userRestService).grantRole(user.getId(), Role.APPLICANT);
        verify(cookieUtil).saveToCookie(response, "role", Role.APPLICANT.getName());
    }

    @Override
    protected OrganisationJourneyEnd supplyServiceUnderTest() {
        return new OrganisationJourneyEnd();
    }
}
