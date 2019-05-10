package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.project.security.ProjectApplicationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummaryService> {

    private ApplicationPermissionRules applicationRules;
    private ProjectApplicationPermissionRules projectApplicationPermissionRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        projectApplicationPermissionRules = getMockPermissionRulesBean(ProjectApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void test_getApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getApplicationSummariesByCompetitionId(1L, null, 0, 20, empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT);
    }

    @Test
    public void test_getSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER);
    }

    @Test
    public void test_getNotSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER);
    }

    @Test
    public void test_getWithFundingDecisionApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER);
    }

    @Test
    public void test_getIneligibleApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getIneligibleApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER);
    }

    @Test
    public void test_getApplicationTeamByApplicationId() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource()
                .build());
        assertAccessDenied(
                () -> classUnderTest.getApplicationTeamByApplicationId(applicationId),
                () -> {
                    verify(applicationRules).usersConnectedToTheApplicationCanView(isA(ApplicationResource.class),
                            isA(UserResource.class));
                    verify(projectApplicationPermissionRules).projectPartnerCanViewApplicationsLinkedToTheirProjects(isA(ApplicationResource.class),
                            isA(UserResource.class));
                    verify(applicationRules).internalUsersCanViewApplications(isA(ApplicationResource.class), isA
                            (UserResource.class));
                    verify(applicationRules).innovationLeadAssignedToCompetitionCanViewApplications(isA
                            (ApplicationResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void test_getAllSubmittedApplicationIdsByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAllSubmittedApplicationIdsByCompetitionId(1L, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER);
    }

    @Test
    public void test_getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(1L, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER);
    }

    @Override
    protected Class<? extends ApplicationSummaryService> getClassUnderTest() {
        return ApplicationSummaryServiceImpl.class;
    }
}
