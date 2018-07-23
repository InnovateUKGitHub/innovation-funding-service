package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Class for functionality in {@link OrganisationJourneyEnd}
 */
public class OrganisationJourneyEndTest extends BaseServiceUnitTest<OrganisationJourneyEnd> {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;


    @Test
    public void completeProcess_newUser() {
        UserResource user = null;
        long organisationId = 1L;

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, "redirect:/registration/register");
        verify(registrationCookieService).saveToOrganisationIdCookie(organisationId, response);
    }

    @Test
    public void completeProcess_existingLead_teamQuestion() {
        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        long organisationId = 1L;
        long competitionId = 2L;
        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();
        QuestionResource teamQuestion = newQuestionResource().build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(true);
        when(registrationCookieService.getCompetitionIdCookieValue(request)).thenReturn(Optional.of(competitionId));
        when(applicationService.createApplication(competitionId, user.getId(), organisationId, ""))
                .thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), APPLICATION_TEAM))
                .thenReturn(restSuccess(teamQuestion));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s/form/question/%s",
                application.getId(), teamQuestion.getId()));
        verify(applicationService).createApplication(competitionId, user.getId(), organisationId, "");
    }

    @Test
    public void completeProcess_existingLead_noQuestion() {
        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        long organisationId = 1L;
        long competitionId = 2L;
        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(true);
        when(registrationCookieService.getCompetitionIdCookieValue(request)).thenReturn(Optional.of(competitionId));
        when(applicationService.createApplication(competitionId, user.getId(), organisationId, ""))
                .thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), APPLICATION_TEAM))
                .thenReturn(restFailure(emptyList(), HttpStatus.NOT_FOUND));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s/team", application.getId()));
        verify(applicationService).createApplication(competitionId, user.getId(), organisationId, "");
    }

    @Test
    public void completeProcess_existingCollaborator() {
        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        long organisationId = 1L;
        long applicationId = 2L;
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
        ApplicationResource application = newApplicationResource().withCompetition(competitionId).build();

        when(registrationCookieService.isCollaboratorJourney(request)).thenReturn(false);
        when(registrationCookieService.isLeadJourney(request)).thenReturn(true);
        when(userRestService.grantRole(user.getId(), Role.APPLICANT)).thenReturn(restSuccess());
        when(registrationCookieService.getCompetitionIdCookieValue(request)).thenReturn(Optional.of(competitionId));
        when(applicationService.createApplication(competitionId, user.getId(), organisationId, ""))
                .thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), APPLICATION_TEAM))
                .thenReturn(restFailure(emptyList(), HttpStatus.NOT_FOUND));

        String result = service.completeProcess(request, response, user, organisationId);

        assertEquals(result, String.format("redirect:/application/%s/team", application.getId()));
        verify(applicationService).createApplication(competitionId, user.getId(), organisationId, "");
        verify(userRestService).grantRole(user.getId(), Role.APPLICANT);
        verify(cookieUtil).saveToCookie(response, "role", Role.APPLICANT.getName());
    }

    @Override
    protected OrganisationJourneyEnd supplyServiceUnderTest() {
        return new OrganisationJourneyEnd();
    }
}
