package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class CompetitionSetupServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupService> {

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionSetupService> getClassUnderTest() {
        return TestCompetitionSetupService.class;
    }

    @Test
    public void testAllAccessDenied() {
        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            Long competitionId = 2L;

            assertAccessDenied(() -> classUnderTest.create(), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.createNonIfs(), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS), () -> {
                verifyNoMoreInteractions(rules);
            });
            assertAccessDenied(() -> classUnderTest.findAllTypes(), () -> {
                verifyNoMoreInteractions(rules);
            });
        });
    }

    @Test
    public void testCompAdminAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());

        classUnderTest.findAllTypes();
        Long competitionId = 2L;
        classUnderTest.create();
        classUnderTest.createNonIfs();
        Long sectionId = 3L;
        classUnderTest.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
        classUnderTest.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    }
    @Test
    public void testProjectFinanceAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());

        classUnderTest.findAllTypes();
        Long competitionId = 2L;
        classUnderTest.create();
        classUnderTest.createNonIfs();
        Long sectionId = 3L;
        classUnderTest.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
        classUnderTest.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionSetupService implements CompetitionSetupService {

        @Override
        public ServiceResult<String> generateCompetitionCode(Long id, ZonedDateTime dateTime) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionResource> create() {
            return null;
        }

        @Override
        public ServiceResult<List<CompetitionTypeResource>> findAllTypes() {
            return null;
        }

        @Override
        public ServiceResult<Void> copyFromCompetitionTypeTemplate(Long competitionId, Long competitionTypeId) {
            return null;
        }

        @Override
        public ServiceResult<Void> copyFromCompetitionTemplate(Long competitionId, Long templateId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionResource> createNonIfs() {
            return null;
        }

        @Override
		public ServiceResult<Void> markSectionComplete(Long competitionId, CompetitionSetupSection section) {
			return null;
		}

		@Override
		public ServiceResult<Void> markSectionInComplete(Long competitionId, CompetitionSetupSection section) {
			return null;
		}

        @Override
        public ServiceResult<Void> returnToSetup(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> markAsSetup(Long competitionId) {
            return null;
        }

    }
}
