package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.assertj.core.util.Sets;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.SUPPORT;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionServiceImplTest extends BaseServiceUnitTest<CompetitionServiceImpl> {

    @Override
    protected CompetitionServiceImpl supplyServiceUnderTest() {
        return new CompetitionServiceImpl();
    }

    @Mock
    private PublicContentService publicContentService;

    @Before
    public void setUp(){
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build();
        User user = newUser().withId(userResource.getId()).withRoles(Sets.newLinkedHashSet(newRole().withType(COMP_ADMIN).build())).build();
        setLoggedInUser(userResource);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
    }

    @Test
    public void getCompetitionById() throws Exception {
        Long competitionId = 1L;
        Competition competition = new Competition();
        CompetitionResource resource = new CompetitionResource();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(competitionMapperMock.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionById(competitionId).getSuccessObjectOrThrowException();

        assertEquals(resource, response);
    }

    @Test
    public void findInnovationLeads() throws Exception {
        Long competitionId = 1L;

        User user = UserBuilder.newUser().build();
        UserResource userResource = UserResourceBuilder.newUserResource().build();
        List<CompetitionParticipant> competitionParticipants = CompetitionParticipantBuilder.newCompetitionParticipant()
                .withUser(user)
                .build(4);

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(competitionId, CompetitionParticipantRole.INNOVATION_LEAD)).thenReturn(competitionParticipants);
        when(userMapperMock.mapToResource(user)).thenReturn(userResource);
        List<UserResource> result = service.findInnovationLeads(competitionId).getSuccessObjectOrThrowException();

        assertEquals(4, result.size());
        assertEquals(userResource, result.get(0));
    }

    @Test
    public void addInnovationLeadWhenCompetitionNotFound() throws Exception {
        Long competitionId = 1L;
        Long innovationLeadUserId = 2L;
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(null);
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(Competition.class, competitionId)));
    }

    @Test
    public void addInnovationLead() throws Exception {
        Long competitionId = 1L;
        Long innovationLeadUserId = 2L;

        Competition competition = CompetitionBuilder.newCompetition().build();
        User innovationLead = UserBuilder.newUser().build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(userRepositoryMock.findOne(innovationLeadUserId)).thenReturn(innovationLead);
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isSuccess());

        CompetitionParticipant savedCompetitionParticipant = new CompetitionParticipant();
        savedCompetitionParticipant.setProcess(competition);
        savedCompetitionParticipant.setUser(innovationLead);
        savedCompetitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
        savedCompetitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

        // Verify that the correct CompetitionParticipant is saved
        verify(competitionParticipantRepositoryMock).save(savedCompetitionParticipant);
    }

    @Test
    public void removeInnovationLeadWhenCompetitionParticipantNotFound() throws Exception {
        Long competitionId = 1L;
        Long innovationLeadUserId = 2L;

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndUserIdAndRole(competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD))
                .thenReturn(null);
        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(CompetitionParticipant.class, competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD)));
    }

    @Test
    public void removeInnovationLead() throws Exception {
        Long competitionId = 1L;
        Long innovationLeadUserId = 2L;

        CompetitionParticipant competitionParticipant = CompetitionParticipantBuilder.newCompetitionParticipant().build();
        when(competitionParticipantRepositoryMock.getByCompetitionIdAndUserIdAndRole(competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD))
                .thenReturn(competitionParticipant);

        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isSuccess());

        //Verify that the entity is deleted
        verify(competitionParticipantRepositoryMock).delete(competitionParticipant);
    }

    @Test
    public void getCompetitionsByUserId() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        List<CompetitionResource> resources = Lists.newArrayList(new CompetitionResource());
        List<Long> competitionIds = asList(1L,2L,3L);
        Long userId = 9421L;

        when(applicationServiceMock.findByUserId(userId)).thenReturn(serviceSuccess(newApplicationResource().withCompetition(1L, 2L, 2L, 3L, 3L, 3L).build(6)));
        when(competitionRepositoryMock.findByIdIsIn(competitionIds)).thenReturn(competitions);
        when(competitionMapperMock.mapToResource(competitions)).thenReturn(resources);

        List<CompetitionResource> response = service.getCompetitionsByUserId(userId).getSuccessObjectOrThrowException();

        assertEquals(resources, response);
    }

    @Test
    public void findAll() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        List<CompetitionResource> resources = Lists.newArrayList(new CompetitionResource());
        when(competitionRepositoryMock.findAll()).thenReturn(competitions);
        when(competitionMapperMock.mapToResource(competitions)).thenReturn(resources);

        List<CompetitionResource> response = service.findAll().getSuccessObjectOrThrowException();

        assertEquals(resources, response);
    }

    @Test
    public void findLiveCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findProjectSetupCompetitions() throws Exception {
        Competition comp1 = newCompetition().withName("Comp1").build();
        Competition comp2 = newCompetition().withName("Comp2").build();
        Application fundedAndInformedApplication1 = newApplication().withCompetition(comp1).withManageFundingEmailDate(ZonedDateTime.now()).withFundingDecision(FundingDecisionStatus.FUNDED).build();
        comp1.setApplications(singletonList(fundedAndInformedApplication1));
        Application fundedAndInformedApplication2 = newApplication().withCompetition(comp2).withManageFundingEmailDate(ZonedDateTime.now().plusHours(1L)).withFundingDecision(FundingDecisionStatus.FUNDED).build();
        comp2.setApplications(singletonList(fundedAndInformedApplication2));
        List<Competition> competitions = Lists.newArrayList(comp1, comp2);
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findProjectSetup()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findProjectSetupCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(CollectionFunctions.reverse(competitions), response);
    }

    @Test
    public void findUpcomingCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findUpcoming()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findUpcomingCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findNonIfsCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findNonIfs()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findNonIfsCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void countCompetitions() throws Exception {
        Long countLive = 1L;
        Long countProjectSetup = 2L;
        Long countUpcoming = 3L;
        when(competitionRepositoryMock.countLive()).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetup()).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countUpcoming()).thenReturn(countUpcoming);

        CompetitionCountResource response = service.countCompetitions().getSuccessObjectOrThrowException();

        assertEquals(countLive, response.getLiveCount());
        assertEquals(countProjectSetup, response.getProjectSetupCount());
        assertEquals(countUpcoming, response.getUpcomingCount());
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
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName(competitionType).build()).build();
        when(queryResponse.getTotalElements()).thenReturn(totalElements);
        when(queryResponse.getTotalPages()).thenReturn(totalPages);
        when(queryResponse.getNumber()).thenReturn(page);
        when(queryResponse.getNumberOfElements()).thenReturn(size);
        when(queryResponse.getContent()).thenReturn(singletonList(competition));
        when(competitionRepositoryMock.search(searchLike, pageRequest)).thenReturn(queryResponse);
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccessObjectOrThrowException();

        assertEquals(totalElements, response.getTotalElements());
        assertEquals(totalPages, response.getTotalPages());
        assertEquals(page, response.getNumber());
        assertEquals(size, response.getSize());

        CompetitionSearchResultItem expectedSearchResult = new CompetitionSearchResultItem(competition.getId(),
                competition.getName(), Collections.EMPTY_SET, 0, "", CompetitionStatus.COMPETITION_SETUP, competitionType, 0, null, null);
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
        Long competitionId = 1L;
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
        Long competitionId = 1L;
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
        Long competitionId = 1L;
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
        Long competitionId = 1L;
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
        Long competitionId = 1L;
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
        Long competitionId = 1L;
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
        Long competitionId = 1L;
        List<OrganisationType> organisationTypes  = newOrganisationType().build(2);
        List<OrganisationTypeResource> organisationTypeResources = newOrganisationTypeResource().build(2);
        Competition competition = new Competition();
        competition.setLeadApplicantTypes(organisationTypes);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(organisationTypeMapperMock.mapToResource(organisationTypes)).thenReturn(organisationTypeResources);

        List<OrganisationTypeResource> response = service.getCompetitionOrganisationTypes(competitionId).getSuccessObjectOrThrowException();

        assertEquals(organisationTypeResources, response);
    }

    @Test
    public void testTopLevelNavigationLinkIsSetCorrectly() throws Exception {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccessObjectOrThrowException();
        assertTopLevelFlagForNonSupportUser(competitions, response);

        UserResource userResource = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(SUPPORT).build())).build();
        User user = newUser().withId(userResource.getId()).withRoles(Sets.newLinkedHashSet(newRole().withType(SUPPORT).build())).build();
        when(userRepositoryMock.findOne(userResource.getId())).thenReturn(user);
        setLoggedInUser(userResource);
        response = service.findLiveCompetitions().getSuccessObjectOrThrowException();
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
}
