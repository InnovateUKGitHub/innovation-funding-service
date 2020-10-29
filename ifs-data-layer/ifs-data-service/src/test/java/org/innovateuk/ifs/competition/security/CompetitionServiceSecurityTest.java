package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.competition.transactional.CompetitionServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class CompetitionServiceSecurityTest extends BaseServiceSecurityTest<CompetitionService> {

    private CompetitionLookupStrategy competitionLookupStrategy;

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionService> getClassUnderTest() {
        return CompetitionServiceImpl.class;
    }

    @Test
    public void findAll() {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.findAll()).thenReturn(serviceSuccess(newCompetitionResource().build(2)));

        ServiceResult<List<CompetitionResource>> results = classUnderTest.findAll();
        assertEquals(0, results.getSuccess().size());

        verify(rules, times(2)).externalUsersCannotViewCompetitionsInSetup(isA(CompetitionResource.class), eq(user));
        verify(rules, times(2)).internalUserCanViewAllCompetitions(isA(CompetitionResource.class), eq(user));
        verify(rules, times(2)).innovationLeadCanViewCompetitionAssignedToThem(isA(CompetitionResource.class), eq(user));
        verify(rules, times(2)).stakeholderCanViewCompetitionAssignedToThem(isA(CompetitionResource.class), eq(user));
        verify(rules, times(2)).monitoringOfficersCanViewCompetitionAssignedToThem(isA(CompetitionResource.class), eq(user));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void getCompetitionById() {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.getCompetitionById(1L))
                .thenReturn(serviceSuccess(newCompetitionResource().build()));

        assertAccessDenied(() -> classUnderTest.getCompetitionById(1L), () -> {
            verify(rules).externalUsersCannotViewCompetitionsInSetup(isA(CompetitionResource.class), eq(user));
            verify(rules).internalUserCanViewAllCompetitions(isA(CompetitionResource.class), eq(user));
            verify(rules).innovationLeadCanViewCompetitionAssignedToThem(isA(CompetitionResource.class),  eq(user));
            verify(rules).stakeholderCanViewCompetitionAssignedToThem(isA(CompetitionResource.class), eq(user));
            verify(rules).monitoringOfficersCanViewCompetitionAssignedToThem(isA(CompetitionResource.class), eq(user));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getCompetitionOrganisationTypesById() {
        when(classUnderTestMock.getCompetitionOrganisationTypes(1L))
                .thenReturn(serviceSuccess(newOrganisationTypeResource().build(2)));

        runAsRole(SYSTEM_REGISTRATION_USER, () -> classUnderTest.getCompetitionOrganisationTypes(1L));
    }

    @Test
    public void closeAssessment() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.notifyAssessors(1L), PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void notifyAssessors() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.notifyAssessors(1L), PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void releaseFeedback() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.releaseFeedback(1L), PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void manageInformState() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.manageInformState(1L), PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void countOpenQueries() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.countAllOpenQueries(1L), PROJECT_FINANCE);
    }

    @Test
    public void findAllOpenQueries() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.findAllOpenQueries(1L), PROJECT_FINANCE);
    }

    @Test
    public void getPendingSpendProfiles() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getPendingSpendProfiles(1L), PROJECT_FINANCE);
    }

    @Test
    public void countPendingSpendProfiles() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.countPendingSpendProfiles(1L), PROJECT_FINANCE);
    }

    private void runAsRole(Role roleType, Runnable serviceCall) {
        setLoggedInUser(
                newUserResource()
                        .withRolesGlobal(singletonList(Role.getByName(roleType.getName())))
                        .build());
        serviceCall.run();
    }
}
