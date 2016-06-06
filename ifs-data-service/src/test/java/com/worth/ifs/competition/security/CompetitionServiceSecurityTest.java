package com.worth.ifs.competition.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class CompetitionServiceSecurityTest extends BaseServiceSecurityTest<CompetitionService> {

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionService> getServiceClass() {
        return TestCompetitionService.class;
    }

    @Test
    public void testFindAll() {

        setLoggedInUser(null);

        ServiceResult<List<CompetitionResource>> results = service.findAll();
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).anyoneCanViewCompetitions(isA(CompetitionResource.class), isNull(UserResource.class));
        verifyNoMoreInteractions(rules);
    }

    public void testGetCompetitionById() {

        setLoggedInUser(null);

        assertAccessDenied(() -> service.getCompetitionById(1L), () -> {
            verify(rules).anyoneCanViewCompetitions(isA(CompetitionResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionService implements CompetitionService {

        @Override
        public ServiceResult<CompetitionResource> getCompetitionById(Long id) {
            return serviceSuccess(newCompetitionResource().build());
        }

        @Override
        public ServiceResult<CompetitionResource> update(Long id, CompetitionResource competitionResource) {
            return serviceSuccess(competitionResource);
        }

        @Override
        public ServiceResult<CompetitionResource> create() {
            return serviceSuccess(newCompetitionResource().build());
        }

        @Override
        public ServiceResult<List<CompetitionResource>> findAll() {
            return serviceSuccess(newCompetitionResource().build(2));
        }

        @Override
        public ServiceResult<List<CompetitionSetupCompletedSectionResource>> findAllCompetitionSectionsStatuses(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<CompetitionSetupSectionResource>> findAllCompetitionSections() {
            return null;
        }
    }
}
