package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the
 * logic within those rules
 */
public class CompetitionSetupServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupService> {

    private static final EnumSet<Role> NON_COMP_ADMIN_ROLES = complementOf(of(COMP_ADMIN, PROJECT_FINANCE));
    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionSetupService> getClassUnderTest() {
        return CompetitionSetupServiceImpl.class;
    }

    @Test
    public void testAllAccessDenied() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(role)).build());
            Long competitionId = 2L;

            assertAccessDenied(() -> classUnderTest.create(), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.updateCompetitionInitialDetails(competitionId, new
                    CompetitionResource(), 7L), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.createNonIfs(), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.markSectionComplete(competitionId, CompetitionSetupSection
                    .INITIAL_DETAILS), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.markSectionIncomplete(competitionId, CompetitionSetupSection
                    .INITIAL_DETAILS), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.findAllTypes(), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.deleteCompetition(competitionId), () -> {
                verifyNoMoreInteractions(rules);
            });
        });
    }

    @Test
    public void testCompAdminAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build());

        classUnderTest.findAllTypes();
        Long competitionId = 2L;
        classUnderTest.create();
        classUnderTest.updateCompetitionInitialDetails(competitionId, new CompetitionResource(), 7L);
        classUnderTest.createNonIfs();
        classUnderTest.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
        classUnderTest.markSectionIncomplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
        classUnderTest.getSectionStatuses(competitionId);
        classUnderTest.getSubsectionStatuses(competitionId);
        classUnderTest.markSubsectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS,
                CompetitionSetupSubsection.APPLICATION_DETAILS);
        classUnderTest.markSubsectionIncomplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS,
                CompetitionSetupSubsection.APPLICATION_DETAILS);
    }

    @Test
    public void testProjectFinanceAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());

        classUnderTest.findAllTypes();
        Long competitionId = 2L;
        classUnderTest.create();
        classUnderTest.updateCompetitionInitialDetails(competitionId, new CompetitionResource(), 7L);
        classUnderTest.createNonIfs();
        classUnderTest.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
        classUnderTest.markSectionIncomplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    }
}
