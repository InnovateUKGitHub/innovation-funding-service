package org.innovateuk.ifs.application.finance.populator.util;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.HttpServletUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FinanceLinksUtilTest {

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private HttpServletUtil httpServletUtil;

    @Mock
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @InjectMocks
    private FinanceLinksUtil financeLinksUtil;

    private CompetitionResource competition;

    private ApplicationResource application;

    private OrganisationResource organisation;

    private ProcessRoleResource processRole;

    private  long userId = 4L;

    private long applicationId = 1L;

    @Before
    public void setup() {
        long competitionId = 2L;
        long organisationId = 3L;

        competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(GRANT)
                .withFinanceRowTypes(Collections.singletonList(FinanceRowType.FINANCE))
                .build();
        application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationState(ApplicationState.CREATED)
                .build();
        organisation = newOrganisationResource().withId(organisationId).build();
        processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(organisationId)
                .build();
    }

    @Test
    public void financesLinkForInternalUser() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }

    @Test
    public void financesLinkForSupport() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.SUPPORT)
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }

    @Test
    public void financesLinkForExternalFinance() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.EXTERNAL_FINANCE)
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }

    @Test
    public void financesLinkForStakeHolder() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.STAKEHOLDER)
                .build();
        application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }

    @Test
    public void financesLinkForLeadApplicant() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.LEADAPPLICANT)
                .build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE", financeLink.get());
    }

    @Test
    public void financesLinkForCollaborator() {
        long organisationId = 4L;

        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.COLLABORATOR)
                .build();
        organisation = newOrganisationResource().withId(organisationId).build();
        CompetitionAssessmentConfigResource assessmentConfigResource = newCompetitionAssessmentConfigResource().build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(anyLong())).thenReturn(RestResult.restSuccess(assessmentConfigResource));

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertFalse(financeLink.isPresent());
    }

    @Test
    public void financesLinkForAssessorWithDetailedFinanceView() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.ASSESSOR)
                .build();
        processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.ASSESSOR)
                .build();
        CompetitionAssessmentConfigResource assessmentConfigResource = newCompetitionAssessmentConfigResource()
                .withAssessorFinanceView(AssessorFinanceView.DETAILED).build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(anyLong())).thenReturn(RestResult.restSuccess(assessmentConfigResource));

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/assessment/application/1/detailed-finances/organisation/3", financeLink.get());
    }

    @Test
    public void financesLinkForAssessorWithAllFinanceView() {
        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.ASSESSOR)
                .build();
        processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.ASSESSOR)
                .build();
        CompetitionAssessmentConfigResource assessmentConfigResource = newCompetitionAssessmentConfigResource()
                .withAssessorFinanceView(AssessorFinanceView.ALL).build();

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(anyLong())).thenReturn(RestResult.restSuccess(assessmentConfigResource));

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }

    @Test
    public void financesLinkForSupporterWithAssignment() {
        competition.setFundingType(FundingType.KTP);

        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.SUPPORTER)
                .build();

        SupporterAssignmentResource supporterAssignment = new SupporterAssignmentResource();
        supporterAssignment.setState(SupporterState.ACCEPTED);

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(supporterAssignmentRestService.getAssignment(userId, applicationId)).thenReturn(RestResult.restSuccess(supporterAssignment));

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.emptyList(), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }

    @Test
    public void noFinancesLinkForSupporterWithNoAssignment() {
        competition.setFundingType(FundingType.KTP);

        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.SUPPORTER)
                .build();

        SupporterAssignmentResource supporterAssignment = new SupporterAssignmentResource();
        supporterAssignment.setState(SupporterState.REJECTED);

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);
        when(supporterAssignmentRestService.getAssignment(userId, applicationId)).thenReturn(RestResult.restFailure(HttpStatus.NOT_FOUND));

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.emptyList(), user, application, competition);

        assertFalse(financeLink.isPresent());
    }
}
