package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.PreviousCompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.UpcomingCompetitionSearchResultItem;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompetitionSearchServiceImplTest extends BaseServiceUnitTest<CompetitionSearchServiceImpl> {

    @Override
    protected CompetitionSearchServiceImpl supplyServiceUnderTest() {
        return new CompetitionSearchServiceImpl();
    }

    @Mock
    private PublicContentRepository publicContentRepository;

    @Mock
    private MilestoneService milestoneService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CompetitionMapper competitionMapperMock;

    @Mock
    private CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsServiceMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private OrganisationTypeMapper organisationTypeMapperMock;

    @Mock
    private InnovationLeadRepository innovationLeadRepositoryMock;

    @Mock
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    private Long competitionId = 1L;

    @Before
    public void setUp() {
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.COMP_ADMIN)).build();
        setLoggedInUser(userResource);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        MilestoneResource milestone = newMilestoneResource().withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now()).build();
        when(milestoneService.getMilestoneByTypeAndCompetitionId(eq(MilestoneType.OPEN_DATE), anyLong())).thenReturn(serviceSuccess(milestone));
    }

    @Test
    public void findLiveCompetitions() {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findProjectSetupCompetitions() {
        int page = 0;
        int size = 20;
        List<Competition> expectedCompetitions = newCompetition().build(2);

        when(competitionRepositoryMock.findProjectSetup(any())).thenReturn(new PageImpl<>(expectedCompetitions, PageRequest.of(page, size), 1L));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(0).getId())).thenReturn(Optional.of(newApplication().withManageFundingEmailDate(ZonedDateTime.now().minusDays(1)).build()));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(1).getId())).thenReturn(Optional.of(newApplication().withManageFundingEmailDate(ZonedDateTime.now()).build()));

        CompetitionSearchResult response = service.findProjectSetupCompetitions(page, size).getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(expectedCompetitions, response.getContent());
    }

    @Test
    public void findProjectSetupCompetitions_NoApplications() {
        int page = 0;
        int size = 20;
        List<Competition> expectedCompetitions = newCompetition().build(1);

        when(competitionRepositoryMock.findProjectSetup(any())).thenReturn(new PageImpl<>(expectedCompetitions, PageRequest.of(page, size), 1L));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(0).getId())).thenReturn(Optional.empty());

        CompetitionSearchResult response = service.findProjectSetupCompetitions(page, size).getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(expectedCompetitions, response.getContent());
    }

    @Test
    public void findProjectSetupCompetitionsWhenLoggedInAsStakeholder() {
        int page = 0;
        int size = 20;
        UserResource stakeholderUser = newUserResource().withId(1L).withRolesGlobal(singletonList(STAKEHOLDER)).build();
        User user = newUser().withId(stakeholderUser.getId()).withRoles(singleton(STAKEHOLDER)).build();
        setLoggedInUser(stakeholderUser);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        List<Competition> expectedCompetitions = newCompetition().build(2);

        when(competitionRepositoryMock.findProjectSetupForInnovationLeadOrStakeholderOrCompetitionFinance(eq(stakeholderUser.getId()), any())).thenReturn(new PageImpl<>(expectedCompetitions, PageRequest.of(page, size), 1L));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(0).getId())).thenReturn(Optional.of(newApplication().withManageFundingEmailDate(ZonedDateTime.now().minusDays(1)).build()));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(1).getId())).thenReturn(Optional.of(newApplication().withManageFundingEmailDate(ZonedDateTime.now()).build()));

        CompetitionSearchResult response = service.findProjectSetupCompetitions(page, size).getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(expectedCompetitions, response.getContent());
        verify(competitionRepositoryMock).findProjectSetupForInnovationLeadOrStakeholderOrCompetitionFinance(eq(stakeholderUser.getId()), any());
        verify(competitionRepositoryMock, never()).findProjectSetup(any());
    }

    @Test
    public void findProjectSetupCompetitionsWhenLoggedInAsCompetitionFinance() {
        int page = 0;
        int size = 20;
        UserResource competitionFinanceUser = newUserResource().withId(1L).withRolesGlobal(singletonList(EXTERNAL_FINANCE)).build();
        User user = newUser().withId(competitionFinanceUser.getId()).withRoles(singleton(EXTERNAL_FINANCE)).build();
        setLoggedInUser(competitionFinanceUser);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        List<Competition> expectedCompetitions = newCompetition().build(2);

        when(competitionRepositoryMock.findProjectSetupForInnovationLeadOrStakeholderOrCompetitionFinance(eq(competitionFinanceUser.getId()), any())).thenReturn(new PageImpl<>(expectedCompetitions, PageRequest.of(page, size), 1L));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(0).getId())).thenReturn(Optional.of(newApplication().withManageFundingEmailDate(ZonedDateTime.now().minusDays(1)).build()));
        when(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(expectedCompetitions.get(1).getId())).thenReturn(Optional.of(newApplication().withManageFundingEmailDate(ZonedDateTime.now()).build()));

        CompetitionSearchResult response = service.findProjectSetupCompetitions(page, size).getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(expectedCompetitions, response.getContent());
        verify(competitionRepositoryMock).findProjectSetupForInnovationLeadOrStakeholderOrCompetitionFinance(eq(competitionFinanceUser.getId()), any());
        verify(competitionRepositoryMock, never()).findProjectSetup(any());
    }

    @Test
    public void findUpcomingCompetitions() {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(competitionRepositoryMock.findUpcoming()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findUpcomingCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findNonIfsCompetitions() {
        int page = 0;
        int size = 20;
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentRepository.findByCompetitionId(competitionId)).thenReturn(newPublicContent().build());
        when(competitionRepositoryMock.findNonIfs(any())).thenReturn(new PageImpl<>(competitions, PageRequest.of(page, size), 1L));

        CompetitionSearchResult response = service.findNonIfsCompetitions(page, size).getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response.getContent());
    }

    @Test
    public void findPreviousCompetitions() {
        int page = 0;
        int size = 20;
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build(1));
        when(competitionRepositoryMock.findPrevious(any())).thenReturn(new PageImpl<>(competitions, PageRequest.of(page, size), 30));
        when(applicationRepository.countPrevious(competitionId)).thenReturn(1);
        when(projectRepositoryMock.countByApplicationCompetitionId(competitionId)).thenReturn(2);
        when(projectRepositoryMock.countByApplicationCompetitionIdAndProjectProcessActivityStateIn(competitionId, ProjectState.COMPLETED_STATES)).thenReturn(3);

        CompetitionSearchResult response = service.findPreviousCompetitions(page, size).getSuccess();

        assertEquals((long) competitionId, response.getContent().get(0).getId());
        assertEquals(1, ((PreviousCompetitionSearchResultItem) response.getContent().get(0)).getApplications());
        assertEquals(2, ((PreviousCompetitionSearchResultItem) response.getContent().get(0)).getProjects());
        assertEquals(3, ((PreviousCompetitionSearchResultItem) response.getContent().get(0)).getCompleteProjects());
    }

    @Test
    public void findPreviousCompetitionsWhenLoggedInAsCompetitionFinanceUser() {
        int page = 0;
        int size = 20;
        UserResource competitionFinanceUser = newUserResource().withId(1L).withRolesGlobal(singletonList(EXTERNAL_FINANCE)).build();
        User user = newUser().withId(competitionFinanceUser.getId()).withRoles(singleton(EXTERNAL_FINANCE)).build();
        setLoggedInUser(competitionFinanceUser);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build(1));
        when(competitionRepositoryMock.findPreviousForInnovationLeadOrStakeholderOrCompetitionFinance(user.getId(), PageRequest.of(page, size))).thenReturn(new PageImpl<>(competitions, PageRequest.of(page, size), 30));
        when(applicationRepository.countPrevious(competitionId)).thenReturn(1);
        when(projectRepositoryMock.countByApplicationCompetitionId(competitionId)).thenReturn(2);
        when(projectRepositoryMock.countByApplicationCompetitionIdAndProjectProcessActivityStateIn(competitionId, ProjectState.COMPLETED_STATES)).thenReturn(3);

        CompetitionSearchResult response = service.findPreviousCompetitions(page, size).getSuccess();

        assertEquals((long) competitionId, response.getContent().get(0).getId());
        assertEquals(1, ((PreviousCompetitionSearchResultItem) response.getContent().get(0)).getApplications());
        assertEquals(2, ((PreviousCompetitionSearchResultItem) response.getContent().get(0)).getProjects());
        assertEquals(3, ((PreviousCompetitionSearchResultItem) response.getContent().get(0)).getCompleteProjects());
    }

    @Test
    public void countCompetitions() {
        Long countLive = 1L;
        Long countProjectSetup = 2L;
        Long countUpcoming = 3L;
        Long countFeedbackReleased = 4L;
        when(competitionRepositoryMock.countLive()).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetup()).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countUpcoming()).thenReturn(countUpcoming);
        when(competitionRepositoryMock.countPrevious()).thenReturn(countFeedbackReleased);

        CompetitionCountResource response = service.countCompetitions().getSuccess();

        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
        assertEquals(countFeedbackReleased, response.getFeedbackReleasedCount());

        // Test for innovation lead user where only competitions they are assigned to should be counted
        // actual query tested in repository integration test, this is only testing correct repository method is called.
        UserResource innovationLeadUser = newUserResource().withRolesGlobal(singletonList(INNOVATION_LEAD)).build();
        setLoggedInUser(innovationLeadUser);

        when(competitionRepositoryMock.countLiveForInnovationLeadOrStakeholder(innovationLeadUser.getId())).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetupForInnovationLeadOrStakeholder(innovationLeadUser.getId())).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countPreviousForInnovationLeadOrStakeholder(innovationLeadUser.getId())).thenReturn(countFeedbackReleased);
        when(userRepositoryMock.findById(innovationLeadUser.getId())).thenReturn(Optional.of(newUser().withId(innovationLeadUser.getId()).withRoles(singleton(Role.INNOVATION_LEAD)).build()));

        response = service.countCompetitions().getSuccess();
        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
        assertEquals(countFeedbackReleased, response.getFeedbackReleasedCount());

        // Test for Stakeholder user
        UserResource stakeholderUser = newUserResource().withRolesGlobal(singletonList(Role.STAKEHOLDER)).build();
        setLoggedInUser(stakeholderUser);

        when(competitionRepositoryMock.countLiveForInnovationLeadOrStakeholder(stakeholderUser.getId())).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetupForInnovationLeadOrStakeholder(stakeholderUser.getId())).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countPreviousForInnovationLeadOrStakeholder(stakeholderUser.getId())).thenReturn(countFeedbackReleased);
        when(userRepositoryMock.findById(stakeholderUser.getId())).thenReturn(Optional.of(newUser().withId(stakeholderUser.getId()).withRoles(singleton(Role.STAKEHOLDER)).build()));

        response = service.countCompetitions().getSuccess();
        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
        assertEquals(countFeedbackReleased, response.getFeedbackReleasedCount());
    }

    @Test
    public void searchCompetitions() {
        String searchQuery = "SearchQuery";
        String searchLike = "%" + searchQuery + "%";
        String competitionType = "Comp type";
        int page = 1;
        int size = 20;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Competition> queryResponse = mock(Page.class);
        long totalElements = 2L;
        int totalPages = 1;
        ZonedDateTime openDate = ZonedDateTime.now();
        Milestone openDateMilestone = newMilestone().withType(MilestoneType.OPEN_DATE).withDate(openDate).build();
        Competition competition = newCompetition().withId(competitionId).withCompetitionType(newCompetitionType().withName(competitionType).build()).withMilestones(asList(openDateMilestone)).build();
        when(queryResponse.getTotalElements()).thenReturn(totalElements);
        when(queryResponse.getTotalPages()).thenReturn(totalPages);
        when(queryResponse.getNumber()).thenReturn(page);
        when(queryResponse.getNumberOfElements()).thenReturn(size);
        when(queryResponse.getContent()).thenReturn(singletonList(competition));
        when(queryResponse.getPageable()).thenReturn(pageRequest);
        when(competitionRepositoryMock.search(searchLike, pageRequest)).thenReturn(queryResponse);
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());

        CompetitionSearchResultItem expectedSearchResult = new UpcomingCompetitionSearchResultItem(competition.getId(),
                competition.getName(),
                CompetitionStatus.COMPETITION_SETUP,
                competitionType,
                EMPTY_SET,
                openDate.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));

        assertEquals(expectedSearchResult.getId(), (long) competition.getId());
    }

    @Test
    public void searchCompetitionsAsLeadTechnologist() {

        long totalElements = 2L;
        int totalPages = 1;
        int page = 1;
        int size = 20;

        String searchQuery = "SearchQuery";
        String competitionType = "Comp type";
        Competition competition = newCompetition().withId(competitionId).withCompetitionType(newCompetitionType().withName(competitionType).build()).build();

        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.INNOVATION_LEAD)).build();

        searchCompetitionsMocking(totalElements, totalPages, page, size, searchQuery, competition, userResource, user);

        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        searchCompetitionsAssertions(totalElements, totalPages, page, size, competitionType, competition, response);

        verify(competitionRepositoryMock).searchForInnovationLeadOrStakeholder(any(), anyLong(), any());
        verify(competitionRepositoryMock, never()).searchForSupportUser(any(), any());
        verify(competitionRepositoryMock, never()).search(any(), any());
    }

    private void searchCompetitionsMocking(long totalElements, int totalPages, int page, int size, String searchQuery,
                                           Competition competition, UserResource userResource, User user) {

        String searchLike = "%" + searchQuery + "%";
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Competition> queryResponse = mock(Page.class);

        setLoggedInUser(userResource);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        when(queryResponse.getTotalElements()).thenReturn(totalElements);
        when(queryResponse.getTotalPages()).thenReturn(totalPages);
        when(queryResponse.getNumber()).thenReturn(page);
        when(queryResponse.getNumberOfElements()).thenReturn(size);
        when(queryResponse.getContent()).thenReturn(singletonList(competition));
        when(queryResponse.getPageable()).thenReturn(pageRequest);

        if (user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)) {
            when(competitionRepositoryMock.searchForInnovationLeadOrStakeholder(searchLike, user.getId(), pageRequest)).thenReturn(queryResponse);
        } else if (user.hasRole(SUPPORT)) {
            when(competitionRepositoryMock.searchForSupportUser(searchLike, pageRequest)).thenReturn(queryResponse);
        }
    }

    private void searchCompetitionsAssertions(long totalElements, int totalPages, int page, int size,
                                              String competitionType, Competition competition, CompetitionSearchResult response) {

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());


        CompetitionSearchResultItem expectedSearchResult = new UpcomingCompetitionSearchResultItem(competition.getId(),
                competition.getName(),
                CompetitionStatus.COMPETITION_SETUP,
                competitionType,
                EMPTY_SET,
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));

        assertEquals(expectedSearchResult.getId(), (long) competition.getId());
    }

    @Test
    public void searchCompetitionsAsStakeholder() {

        long totalElements = 2L;
        int totalPages = 1;
        int page = 1;
        int size = 20;

        String searchQuery = "SearchQuery";
        String competitionType = "Comp type";
        Competition competition = newCompetition().withId(competitionId).withCompetitionType(newCompetitionType().withName(competitionType).build()).build();

        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.STAKEHOLDER)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.STAKEHOLDER)).build();

        searchCompetitionsMocking(totalElements, totalPages, page, size, searchQuery, competition, userResource, user);

        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        searchCompetitionsAssertions(totalElements, totalPages, page, size, competitionType, competition, response);

        verify(competitionRepositoryMock).searchForInnovationLeadOrStakeholder(any(), anyLong(), any());
        verify(competitionRepositoryMock, never()).searchForSupportUser(any(), any());
        verify(competitionRepositoryMock, never()).search(any(), any());
    }

    @Test
    public void searchCompetitionsAsSupportUser() {
        long totalElements = 2L;
        int totalPages = 1;
        int page = 1;
        int size = 20;

        String searchQuery = "SearchQuery";
        String competitionType = "Comp type";
        Competition competition = newCompetition().withId(competitionId).withCompetitionType(newCompetitionType().withName(competitionType).build()).build();

        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.SUPPORT)).build();

        searchCompetitionsMocking(totalElements, totalPages, page, size, searchQuery, competition, userResource, user);

        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        searchCompetitionsAssertions(totalElements, totalPages, page, size, competitionType, competition, response);

        verify(competitionRepositoryMock, never()).searchForInnovationLeadOrStakeholder(any(), anyLong(), any());
        verify(competitionRepositoryMock).searchForSupportUser(any(), any());
        verify(competitionRepositoryMock, never()).search(any(), any());
    }

    private void assertCompetitionSearchResultsEqualToCompetitions(List<Competition> competitions, List<CompetitionSearchResultItem> searchResults) {

        assertEquals(competitions.size(), searchResults.size());

        forEachWithIndex(searchResults, (i, searchResult) -> {

            Competition originalCompetition = competitions.get(i);
            assertEquals((long) originalCompetition.getId(), searchResult.getId());
            assertEquals(originalCompetition.getName(), ReflectionTestUtils.getField(searchResult, "name"));
        });
    }
}