package com.worth.ifs.competition.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competition.transactional.CompetitionService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
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
    protected Class<? extends CompetitionService> getClassUnderTest() {
        return TestCompetitionService.class;
    }

    @Test
    public void testFindAll() {
        setLoggedInUser(null);

        ServiceResult<List<CompetitionResource>> results = classUnderTest.findAll();
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).externalUsersCannotViewCompetitionsInSetup(isA(CompetitionResource.class), isNull(UserResource.class));
        verify(rules, times(2)).compAdminUserCanViewAllCompetitions(isA(CompetitionResource.class), isNull(UserResource.class));
        verify(rules, times(2)).projectFinanceUserCanViewAllCompetitions(isA(CompetitionResource.class), isNull(UserResource.class));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void testGetCompetitionById() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.getCompetitionById(1L), () -> {
            verify(rules).externalUsersCannotViewCompetitionsInSetup(isA(CompetitionResource.class), isNull(UserResource.class));
            verify(rules).compAdminUserCanViewAllCompetitions(isA(CompetitionResource.class), isNull(UserResource.class));
            verify(rules).projectFinanceUserCanViewAllCompetitions(isA(CompetitionResource.class), isNull(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testFindLiveCompetitions() {
        setLoggedInUser(null);

        ServiceResult<List<CompetitionSearchResultItem>> results = classUnderTest.findLiveCompetitions();
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).compAdminUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), isNull(UserResource.class));
        verify(rules, times(2)).projectFinanceUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), isNull(UserResource.class));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void testFindProjectSetupCompetitions() {
        setLoggedInUser(null);

        ServiceResult<List<CompetitionSearchResultItem>> results = classUnderTest.findProjectSetupCompetitions();
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).compAdminUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), isNull(UserResource.class));
        verify(rules, times(2)).projectFinanceUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), isNull(UserResource.class));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void testFindUpcomingCompetitions() {
        setLoggedInUser(null);

        ServiceResult<List<CompetitionSearchResultItem>> results = classUnderTest.findUpcomingCompetitions();
        assertEquals(0, results.getSuccessObject().size());

        verify(rules, times(2)).compAdminUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), isNull(UserResource.class));
        verify(rules, times(2)).projectFinanceUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), isNull(UserResource.class));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void testCountCompetitions() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.countCompetitions(), () -> {
            verifyNoMoreInteractions(rules);
        });
    }


    @Test
    public void testSearchCompetitions() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.searchCompetitions("string", 1, 1), () -> {
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
        public Competition addCategories(Competition competition) { return competition; }

        @Override
        public ServiceResult<List<CompetitionResource>> findAll() {
            return serviceSuccess(newCompetitionResource().build(2));
        }

        @Override
        public ServiceResult<List<CompetitionSearchResultItem>> findLiveCompetitions() {
            return serviceSuccess(newCompetitionSearchResultItem().build(2));
        }

        @Override
        public ServiceResult<List<CompetitionSearchResultItem>> findProjectSetupCompetitions() {
            return serviceSuccess(newCompetitionSearchResultItem().build(2));
        }

        @Override
        public ServiceResult<List<CompetitionSearchResultItem>> findUpcomingCompetitions() {
            return serviceSuccess(newCompetitionSearchResultItem().build(2));
        }

        @Override
        public ServiceResult<CompetitionSearchResult> searchCompetitions(String searchQuery, int page, int size) {
            return serviceSuccess(new CompetitionSearchResult());
        }

        @Override
        public ServiceResult<CompetitionCountResource> countCompetitions() {
            return serviceSuccess(new CompetitionCountResource());
        }

        @Override
        public ServiceResult<List<CompetitionProjectsCountResource>> countProjectsForCompetitions() {
            return serviceSuccess(Stream.of(new CompetitionProjectsCountResource()).collect(Collectors.toList()));
        }

    }
}
