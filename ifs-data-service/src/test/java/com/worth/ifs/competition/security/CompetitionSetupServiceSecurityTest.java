package com.worth.ifs.competition.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Collections.singletonList;
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
    protected Class<? extends CompetitionSetupService> getServiceClass() {
        return TestCompetitionSetupService.class;
    }

    @Test
    public void testAllAccessDenied() {
        setLoggedInUser(null);
        assertAccessDenied(() -> service.findAllCompetitionSections(), () -> {
            verifyNoMoreInteractions(rules);
        });
        Long competitionId = 2L;
        assertAccessDenied(() -> service.findAllCompetitionSectionsStatuses(competitionId), () -> {
            verifyNoMoreInteractions(rules);
        });

        assertAccessDenied(() -> service.create(), () -> {
            verifyNoMoreInteractions(rules);
        });
        Long sectionId = 3L;
        assertAccessDenied(() -> service.markSectionComplete(competitionId, sectionId), () -> {
            verifyNoMoreInteractions(rules);
        });
        assertAccessDenied(() -> service.markSectionInComplete(competitionId, sectionId), () -> {
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());

        service.findAllCompetitionSections();
        Long competitionId = 2L;
        service.findAllCompetitionSectionsStatuses(competitionId);
        service.create();
        Long sectionId = 3L;
        service.markSectionComplete(competitionId, sectionId);
        service.markSectionInComplete(competitionId, sectionId);
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
        public ServiceResult<List<CompetitionSetupCompletedSectionResource>> findAllCompetitionSectionsStatuses(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections() {
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
        public ServiceResult<Void> markSectionComplete(Long competitionId, Long sectionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> markSectionInComplete(Long competitionId, Long sectionId) {
            return null;
        }
    }
}
