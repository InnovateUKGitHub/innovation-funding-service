package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.transactional.CompetitionSearchService;
import org.innovateuk.ifs.competition.transactional.CompetitionSearchServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.LiveCompetitionSearchResultItemBuilder.newLiveCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class CompetitionSearchServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSearchService> {

    private CompetitionLookupStrategy competitionLookupStrategy;

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionSearchService> getClassUnderTest() {
        return CompetitionSearchServiceImpl.class;
    }

    @Test
    public void findLiveCompetitions() {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.findLiveCompetitions())
                .thenReturn(serviceSuccess(new ArrayList<>(newLiveCompetitionSearchResultItem().build(2))));

        ServiceResult<List<CompetitionSearchResultItem>> results = classUnderTest.findLiveCompetitions();
        assertEquals(0, results.getSuccess().size());

        verify(rules, times(2)).internalUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).innovationLeadCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).stakeholderCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void findProjectSetupCompetitions() {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.findProjectSetupCompetitions(1, 1))
                .thenReturn(serviceSuccess(new CompetitionSearchResult()));

        classUnderTest.findProjectSetupCompetitions(1, 1);

        verify(rules, times(2)).internalUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).innovationLeadCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).stakeholderCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void findUpcomingCompetitions() {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.findUpcomingCompetitions()).thenReturn(serviceSuccess(new ArrayList<>(newUpcomingCompetitionSearchResultItem().build(2))));

        ServiceResult<List<CompetitionSearchResultItem>> results = classUnderTest.findUpcomingCompetitions();
        assertEquals(0, results.getSuccess().size());

        verify(rules, times(2)).internalUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).innovationLeadCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).stakeholderCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void countCompetitions() {


        assertAccessDenied(() -> classUnderTest.countCompetitions(), () -> verifyNoMoreInteractions(rules));
    }

    @Test
    public void searchCompetitions() {
        setLoggedInUser(new UserResource());

        assertAccessDenied(() -> classUnderTest.searchCompetitions("string", 1, 1), () -> verifyNoMoreInteractions(rules));
    }

    @Test
    public void findFeedbackReleasedCompetitions() {
        UserResource user = new UserResource();
        setLoggedInUser(user);

        when(classUnderTestMock.findPreviousCompetitions(1, 1))
                .thenReturn(serviceSuccess(new CompetitionSearchResult()));

        classUnderTest.findPreviousCompetitions(1, 1);

        verify(rules, times(2)).internalUserCanViewAllCompetitionSearchResults(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).innovationLeadCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verify(rules, times(2)).stakeholderCanViewCompetitionAssignedToThem(isA(CompetitionSearchResultItem.class), eq(user));
        verifyNoMoreInteractions(rules);
    }
}
