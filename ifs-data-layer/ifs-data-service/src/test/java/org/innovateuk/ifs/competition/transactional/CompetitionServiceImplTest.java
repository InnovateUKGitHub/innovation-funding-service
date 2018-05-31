package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsBuilder.newGrantTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.InnovationLeadBuilder.newInnovationLead;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CompetitionServiceImplTest extends BaseServiceUnitTest<CompetitionServiceImpl> {

    @Override
    protected CompetitionServiceImpl supplyServiceUnderTest() {
        return new CompetitionServiceImpl();
    }

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private MilestoneService milestoneService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private CompetitionMapper competitionMapperMock;

    @Mock
    private CompetitionKeyStatisticsService competitionKeyStatisticsServiceMock;

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
    public void setUp(){
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.COMP_ADMIN)).build();
        setLoggedInUser(userResource);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        MilestoneResource milestone = newMilestoneResource().withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now()).build();
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competitionId)).thenReturn(serviceSuccess(milestone));
    }

    @Test
    public void getCompetitionById() throws Exception {
        Competition competition = new Competition();
        CompetitionResource resource = new CompetitionResource();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionMapperMock.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionById(competitionId).getSuccess();

        assertEquals(resource, response);
    }

    @Test
    public void findInnovationLeads() throws Exception {
        Long competitionId = 1L;

        User user = UserBuilder.newUser().build();
        UserResource userResource = UserResourceBuilder.newUserResource().build();
        List<InnovationLead> innovationLeads = newInnovationLead()
                .withUser(user)
                .build(4);

        when(innovationLeadRepositoryMock.findInnovationsLeads(competitionId)).thenReturn(innovationLeads);
        when(userMapperMock.mapToResource(user)).thenReturn(userResource);
        List<UserResource> result = service.findInnovationLeads(competitionId).getSuccess();

        assertEquals(4, result.size());
        assertEquals(userResource, result.get(0));
    }

    @Test
    public void addInnovationLeadWhenCompetitionNotFound() throws Exception {
        Long innovationLeadUserId = 2L;
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(null);
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Competition.class, competitionId)));
    }

    @Test
    public void addInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;

        Competition competition = CompetitionBuilder.newCompetition().build();
        User innovationLead = UserBuilder.newUser().build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(userRepositoryMock.findOne(innovationLeadUserId)).thenReturn(innovationLead);
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isSuccess());

        InnovationLead savedCompetitionParticipant = new InnovationLead(competition, innovationLead);

        // Verify that the correct CompetitionParticipant is saved
        verify(innovationLeadRepositoryMock).save(savedCompetitionParticipant);
    }

    @Test
    public void removeInnovationLeadWhenCompetitionParticipantNotFound() throws Exception {
        Long innovationLeadUserId = 2L;

        when(innovationLeadRepositoryMock.findInnovationLead(competitionId, innovationLeadUserId)).thenReturn(null);
        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(InnovationLead.class, competitionId,
                innovationLeadUserId)));
    }

    @Test
    public void removeInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;

        InnovationLead innovationLead = newInnovationLead().build();
        when(innovationLeadRepositoryMock.findInnovationLead(competitionId, innovationLeadUserId)).thenReturn
                (innovationLead);

        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isSuccess());

        //Verify that the entity is deleted
        verify(innovationLeadRepositoryMock).delete(innovationLead);
    }

    @Test
    public void findAll() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        List<CompetitionResource> resources = Lists.newArrayList(new CompetitionResource());
        when(competitionRepositoryMock.findAll()).thenReturn(competitions);
        when(competitionMapperMock.mapToResource(competitions)).thenReturn(resources);

        List<CompetitionResource> response = service.findAll().getSuccess();

        assertEquals(resources, response);
    }

    @Test
    public void findLiveCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findProjectSetupCompetitions() throws Exception {
        CompetitionType progcompetitionType = newCompetitionType().withName("Programme").build();
        CompetitionType eoicompetitionType = newCompetitionType().withName("Expression of interest").build();
        Competition comp1 = newCompetition().withName("Comp1").withId(competitionId).withCompetitionType(progcompetitionType).build();
        Competition comp2 = newCompetition().withName("Comp2").withId(competitionId).withCompetitionType(progcompetitionType).build();

        List<Competition> expectedCompetitions = Lists.newArrayList(comp1, comp2);

        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findProjectSetup()).thenReturn(expectedCompetitions);

        List<CompetitionSearchResultItem> response = service.findProjectSetupCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(CollectionFunctions.reverse(expectedCompetitions), response);
    }

    @Test
    public void findUpcomingCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findUpcoming()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findUpcomingCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findNonIfsCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findNonIfs()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findNonIfsCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findPreviousCompetitions() throws Exception {
        Long competition2Id = 2L;
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId, competition2Id).build(2));
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findFeedbackReleased()).thenReturn(competitions);

        MilestoneResource milestone = newMilestoneResource().withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.of(2017, 11, 3, 23, 0, 0, 0, ZoneId.systemDefault())).build();
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competitionId)).thenReturn(serviceSuccess(milestone));

        milestone = newMilestoneResource().withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.of(2017, 11, 5, 23, 0, 0, 0, ZoneId.systemDefault())).build();
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competition2Id)).thenReturn(serviceSuccess(milestone));

        List<CompetitionSearchResultItem> response = service.findFeedbackReleasedCompetitions().getSuccess();

        //Ensure sorted by open date
        assertEquals(competition2Id, response.get(0).getId());
        assertEquals(ZonedDateTime.of(2017, 11, 5, 23, 0, 0, 0, ZoneId.systemDefault()), response.get(0).getOpenDate());

        assertEquals(competitionId, response.get(1).getId());
        assertEquals(ZonedDateTime.of(2017, 11, 3, 23, 0, 0, 0, ZoneId.systemDefault()), response.get(1).getOpenDate());
    }

    @Test
    public void findPreviousCompetitions_NoOpenDate() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findFeedbackReleased()).thenReturn(competitions);
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competitionId)).thenReturn(serviceFailure(GENERAL_UNEXPECTED_ERROR));

        List<CompetitionSearchResultItem> response = service.findFeedbackReleasedCompetitions().getSuccess();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
        assertNull(response.get(0).getOpenDate());
    }

    @Test
    public void countCompetitions() throws Exception {
        Long countLive = 1L;
        Long countProjectSetup = 2L;
        Long countUpcoming = 3L;
        Long countFeedbackReleased = 4L;
        when(competitionRepositoryMock.countLive()).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetup()).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countUpcoming()).thenReturn(countUpcoming);
        when(competitionRepositoryMock.countFeedbackReleased()).thenReturn(countFeedbackReleased);

        CompetitionCountResource response = service.countCompetitions().getSuccess();

        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
        assertEquals(countFeedbackReleased, response.getFeedbackReleasedCount());

        // Test for innovation lead user where only competitions they are assigned to should be counted
        // actual query tested in repository integration test, this is only testing correct repostiory method is called.
        UserResource innovationLeadUser = newUserResource().withRolesGlobal(asList(Role.INNOVATION_LEAD)).build();
        setLoggedInUser(innovationLeadUser);

        when(competitionRepositoryMock.countLiveForInnovationLead(innovationLeadUser.getId())).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetupForInnovationLead(innovationLeadUser.getId())).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countFeedbackReleasedForInnovationLead(innovationLeadUser.getId())).thenReturn(countFeedbackReleased);
        when(userRepositoryMock.findOne(innovationLeadUser.getId())).thenReturn(newUser().withId(innovationLeadUser.getId()).withRoles(singleton(Role.INNOVATION_LEAD)).build());

        response = service.countCompetitions().getSuccess();
        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
        assertEquals(countFeedbackReleased, response.getFeedbackReleasedCount());
    }

    @Test
    public void searchCompetitions() throws Exception {
        String searchQuery = "SearchQuery";
        String searchLike = "%" + searchQuery + "%";
        String competitionType = "Comp type";
        int page = 1;
        int size = 20;
        PageRequest pageRequest = new PageRequest(page, size);
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
        when(competitionRepositoryMock.search(searchLike, pageRequest)).thenReturn(queryResponse);
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());

        CompetitionSearchResultItem expectedSearchResult = new CompetitionSearchResultItem(competition.getId(),
                competition.getName(),
                EMPTY_SET,
                0,
                openDate.format(DateTimeFormatter.ofPattern("dd/MM/YYYY")),
                CompetitionStatus.COMPETITION_SETUP,
                competitionType,
                0,
                null,
                null,
                openDate);
        // check actual open date is as expected then copy into expected structure (avoids time dependency in tests)
        ZonedDateTime now = ZonedDateTime.now();
        assertTrue((response.getContent().get(0)).getOpenDate().isBefore(now) || (response.getContent().get(0)).getOpenDate().isEqual(now));
        response.getContent().get(0).setOpenDate(expectedSearchResult.getOpenDate());
        assertEquals(singletonList(expectedSearchResult), response.getContent());
    }

    @Test
    public void searchCompetitionsAsLeadTechnologist() throws Exception {
        String searchQuery = "SearchQuery";
        String searchLike = "%" + searchQuery + "%";
        String competitionType = "Comp type";
        int page = 1;
        int size = 20;
        PageRequest pageRequest = new PageRequest(page, size);
        Page<Competition> queryResponse = mock(Page.class);
        long totalElements = 2L;
        int totalPages = 1;
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.INNOVATION_LEAD)).build();
        setLoggedInUser(userResource);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        Competition competition = newCompetition().withId(competitionId).withCompetitionType(newCompetitionType().withName(competitionType).build()).build();
        when(queryResponse.getTotalElements()).thenReturn(totalElements);
        when(queryResponse.getTotalPages()).thenReturn(totalPages);
        when(queryResponse.getNumber()).thenReturn(page);
        when(queryResponse.getNumberOfElements()).thenReturn(size);
        when(queryResponse.getContent()).thenReturn(singletonList(competition));
        when(competitionRepositoryMock.searchForLeadTechnologist(searchLike, user.getId(), pageRequest)).thenReturn(queryResponse);
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());

        CompetitionSearchResultItem expectedSearchResult = new CompetitionSearchResultItem(competition.getId(),
                competition.getName(),
                EMPTY_SET,
                0,
                "",
                CompetitionStatus.COMPETITION_SETUP,
                competitionType,
                0,
                null,
                null,
                ZonedDateTime.now());
        // check actual open date is as expected then copy into expected structure (avoids time dependency in tests)
        ZonedDateTime now = ZonedDateTime.now();
        assertTrue((response.getContent().get(0)).getOpenDate().isBefore(now) || (response.getContent().get(0)).getOpenDate().isEqual(now));
        response.getContent().get(0).setOpenDate(expectedSearchResult.getOpenDate());
        assertEquals(singletonList(expectedSearchResult), response.getContent());
    }

    @Test
    public void searchCompetitionsAsSupportUser() throws Exception {
        String searchQuery = "SearchQuery";
        String searchLike = "%" + searchQuery + "%";
        String competitionType = "Comp type";
        int page = 1;
        int size = 20;
        PageRequest pageRequest = new PageRequest(page, size);
        Page<Competition> queryResponse = mock(Page.class);
        long totalElements = 2L;
        int totalPages = 1;
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.SUPPORT)).build();
        setLoggedInUser(userResource);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        Competition competition = newCompetition().withId(competitionId).withCompetitionType(newCompetitionType().withName(competitionType).build()).build();
        when(queryResponse.getTotalElements()).thenReturn(totalElements);
        when(queryResponse.getTotalPages()).thenReturn(totalPages);
        when(queryResponse.getNumber()).thenReturn(page);
        when(queryResponse.getNumberOfElements()).thenReturn(size);
        when(queryResponse.getContent()).thenReturn(singletonList(competition));
        when(competitionRepositoryMock.searchForSupportUser(searchLike, pageRequest)).thenReturn(queryResponse);
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccess();

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());

        CompetitionSearchResultItem expectedSearchResult = new CompetitionSearchResultItem(competition.getId(),
                competition.getName(),
                EMPTY_SET,
                0,
                "",
                CompetitionStatus.COMPETITION_SETUP,
                competitionType,
                0,
                null,
                null,
                ZonedDateTime.now());
        // check actual open date is as expected then copy into expected structure (avoids time dependency in tests)
        ZonedDateTime now = ZonedDateTime.now();
        assertTrue((response.getContent().get(0)).getOpenDate().isBefore(now) || (response.getContent().get(0)).getOpenDate().isEqual(now));
        response.getContent().get(0).setOpenDate(expectedSearchResult.getOpenDate());
        assertEquals(singletonList(expectedSearchResult), response.getContent());
    }

    private void assertCompetitionSearchResultsEqualToCompetitions(List<Competition> competitions, List<CompetitionSearchResultItem> searchResults) {

        assertEquals(competitions.size(), searchResults.size());

        forEachWithIndex(searchResults, (i, searchResult) -> {

            Competition originalCompetition = competitions.get(i);
            assertEquals(originalCompetition.getId(), searchResult.getId());
            assertEquals(originalCompetition.getName(), searchResult.getName());
        });
    }

    @Test
    public void closeAssessment() throws Exception {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE, SUBMISSION_DATE, ASSESSORS_NOTIFIED).build(3);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(NOTIFICATIONS, ASSESSOR_DEADLINE)
                .build(2));
        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        service.closeAssessment(competitionId);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }


    @Test
    public void notifyAssessors() throws Exception {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE, SUBMISSION_DATE, ALLOCATE_ASSESSORS).build(3);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(ASSESSMENT_CLOSED)
                .build(1));

        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        service.notifyAssessors(competitionId);

        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void releaseFeedback() throws Exception {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE,
                        SUBMISSION_DATE,
                        ALLOCATE_ASSESSORS,
                        ASSESSORS_NOTIFIED,
                        ASSESSMENT_CLOSED,
                        ASSESSMENT_PANEL,
                        PANEL_DATE,
                        FUNDERS_PANEL,
                        NOTIFICATIONS)
                .build(9);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(RELEASE_FEEDBACK)
                .build(1));

        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();

        CompetitionFundedKeyStatisticsResource keyStatistics = new CompetitionFundedKeyStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(5);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionKeyStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.releaseFeedback(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.PROJECT_SETUP, competition.getCompetitionStatus());
    }

    @Test
    public void releaseFeedback_cantRelease() throws Exception {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE,
                        SUBMISSION_DATE,
                        ALLOCATE_ASSESSORS,
                        ASSESSORS_NOTIFIED,
                        ASSESSMENT_CLOSED,
                        ASSESSMENT_PANEL,
                        PANEL_DATE,
                        FUNDERS_PANEL,
                        NOTIFICATIONS)
                .build(9);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(RELEASE_FEEDBACK)
                .build(1));

        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();

        CompetitionFundedKeyStatisticsResource keyStatistics = new CompetitionFundedKeyStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(4);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionKeyStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.releaseFeedback(competitionId);

        assertTrue(response.isFailure());
        assertTrue(response.getFailure().is(new Error(COMPETITION_CANNOT_RELEASE_FEEDBACK)));
        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }

    @Test
    public void manageInformState() throws Exception {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE,
                        SUBMISSION_DATE,
                        ALLOCATE_ASSESSORS,
                        ASSESSORS_NOTIFIED,
                        ASSESSMENT_CLOSED,
                        ASSESSMENT_PANEL,
                        PANEL_DATE,
                        FUNDERS_PANEL)
                .build(9);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(RELEASE_FEEDBACK)
                .build(1));

        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());

        CompetitionFundedKeyStatisticsResource keyStatistics = new CompetitionFundedKeyStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(5);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionKeyStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.manageInformState(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }

    @Test
    public void manageInformState_noStateChange() throws Exception {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE,
                        SUBMISSION_DATE,
                        ALLOCATE_ASSESSORS,
                        ASSESSORS_NOTIFIED,
                        ASSESSMENT_CLOSED,
                        ASSESSMENT_PANEL,
                        PANEL_DATE,
                        FUNDERS_PANEL)
                .build(9);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(RELEASE_FEEDBACK)
                .build(1));

        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());

        CompetitionFundedKeyStatisticsResource keyStatistics = new CompetitionFundedKeyStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(4);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionKeyStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.manageInformState(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void getCompetitionOrganisationTypesById() throws Exception {
        List<OrganisationType> organisationTypes  = newOrganisationType().build(2);
        List<OrganisationTypeResource> organisationTypeResources = newOrganisationTypeResource().build(2);
        Competition competition = new Competition();
        competition.setLeadApplicantTypes(organisationTypes);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(organisationTypeMapperMock.mapToResource(organisationTypes)).thenReturn(organisationTypeResources);

        List<OrganisationTypeResource> response = service.getCompetitionOrganisationTypes(competitionId).getSuccess();

        assertEquals(organisationTypeResources, response);
    }

    @Test
    public void testTopLevelNavigationLinkIsSetCorrectly() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccess();
        assertTopLevelFlagForNonSupportUser(competitions, response);

        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.SUPPORT)).build();
        when(userRepositoryMock.findOne(userResource.getId())).thenReturn(user);
        setLoggedInUser(userResource);
        response = service.findLiveCompetitions().getSuccess();
        assertTopLevelFlagForSupportUser(competitions, response);
    }

    private void assertTopLevelFlagForNonSupportUser(List<Competition> competitions, List<CompetitionSearchResultItem> searchResults) {

        forEachWithIndex(searchResults, (i, searchResult) -> {
            Competition c = competitions.get(i);
            assertEquals("/competition/"+ c.getId(), searchResult.getTopLevelNavigationLink());
        });
    }

    private void assertTopLevelFlagForSupportUser(List<Competition> competitions, List<CompetitionSearchResultItem> searchResults) {

        forEachWithIndex(searchResults, (i, searchResult) -> {
            Competition c = competitions.get(i);
            assertEquals("/competition/" + c.getId() + "/applications/all", searchResult.getTopLevelNavigationLink());
        });
    }

    @Test
    public void getCompetitionOpenQueries() throws Exception {
        List<CompetitionOpenQueryResource> openQueries = singletonList(new CompetitionOpenQueryResource(1L, 1L, "org", 1L, "proj"));
        when(competitionRepositoryMock.getOpenQueryByCompetition(competitionId)).thenReturn(openQueries);

        List<CompetitionOpenQueryResource> response = service.findAllOpenQueries(competitionId).getSuccess();

        assertEquals(1, response.size());
    }

    @Test
    public void countCompetitionOpenQueries() throws Exception {
        Long countOpenQueries = 4l;
        when(competitionRepositoryMock.countOpenQueries(competitionId)).thenReturn(countOpenQueries);

        Long response = service.countAllOpenQueries(competitionId).getSuccess();

        assertEquals(countOpenQueries, response);
    }

    @Test
    public void getPendingSpendProfiles() throws Exception {

        List<Object[]> pendingSpendProfiles = singletonList(new Object[]{BigInteger.valueOf(11L), BigInteger.valueOf(1L), new String("Project 1")});

        when(competitionRepositoryMock.getPendingSpendProfiles(competitionId)).thenReturn(pendingSpendProfiles);

        ServiceResult<List<SpendProfileStatusResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        List<SpendProfileStatusResource> expectedPendingSpendProfiles = singletonList(new SpendProfileStatusResource(11L, 1L, "Project 1"));
        assertEquals(expectedPendingSpendProfiles, result.getSuccess());
    }

    @Test
    public void countPendingSpendProfiles() throws Exception {

        final BigDecimal pendingSpendProfileCount = BigDecimal.TEN;
        when(competitionRepositoryMock.countPendingSpendProfiles(competitionId)).thenReturn(pendingSpendProfileCount);

        ServiceResult<Long> result = service.countPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(Long.valueOf(pendingSpendProfileCount.longValue()), result.getSuccess());
    }

    @Test
    public void updateTermsAndConditionsForCompetition() throws Exception {
        GrantTermsAndConditions termsAndConditions = newGrantTermsAndConditions().build();

        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepositoryMock.findOne(termsAndConditions.getId()))
                .thenReturn(termsAndConditions);
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        ServiceResult<Void> result = service.updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditions.getId());

        assertTrue(result.isSuccess());
        assertEquals(competition.getTermsAndConditions().getId(), termsAndConditions.getId());

        //Verify that the entity is saved
        verify(competitionRepositoryMock).findOne(competition.getId());
        verify(competitionRepositoryMock).save(competition);
        verify(grantTermsAndConditionsRepositoryMock).findOne(termsAndConditions.getId());
    }

    @Test
    public void updateInvalidTermsAndConditionsForCompetition() throws Exception {
        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepositoryMock.findOne(competition.getTermsAndConditions().getId())).thenReturn(null);
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        ServiceResult<Void> result = service.updateTermsAndConditionsForCompetition(competitionId, competition.getTermsAndConditions().getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(GrantTermsAndConditions.class,
                competition.getTermsAndConditions().getId())));

    }
}
