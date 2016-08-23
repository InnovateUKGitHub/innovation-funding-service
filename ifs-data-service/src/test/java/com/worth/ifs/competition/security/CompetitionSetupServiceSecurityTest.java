package com.worth.ifs.competition.security;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.transactional.CompetitionSetupService;

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
    protected Class<? extends CompetitionSetupService> getServiceClass() {
        return TestCompetitionSetupService.class;
    }

    @Test
    public void testAllAccessDenied() {
        setLoggedInUser(null);
        Long competitionId = 2L;

        assertAccessDenied(() -> service.create(), () -> {
            verifyNoMoreInteractions(rules);
        });
        Long sectionId = 3L;
        assertAccessDenied(() -> service.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS), () -> {
            verifyNoMoreInteractions(rules);
        });
        assertAccessDenied(() -> service.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS), () -> {
            verifyNoMoreInteractions(rules);
        });
        assertAccessDenied(() -> service.findAllTypes(), () -> {
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());

        service.findAllTypes();
        Long competitionId = 2L;
        service.create();
        Long sectionId = 3L;
        service.markSectionComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
        service.markSectionInComplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);
    }

    @Test
    public void findAllCompetitionSectionsStatuses() {
        setLoggedInUser(null);

    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionSetupService implements CompetitionSetupService {

        @Override
        public ServiceResult<String> generateCompetitionCode(Long id, LocalDateTime dateTime) {
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

        @Override
		public ServiceResult<Void> initialiseFormForCompetitionType(Long competitionId, Long competitionType) {
			return null;
		}
    }
}
