package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.assertj.core.util.Sets;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.user.builder.OrganisationBuilder;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionAssessmentParticipantBuilder.newCompetitionAssessmentParticipant;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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

    private Long competitionId = 1L;

    @Before
    public void setUp(){
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build();
        User user = newUser().withId(userResource.getId()).withRoles(Sets.newLinkedHashSet(newRole().withType(COMP_ADMIN).build())).build();
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

        CompetitionResource response = service.getCompetitionById(competitionId).getSuccessObjectOrThrowException();

        assertEquals(resource, response);
    }

    @Test
    public void findInnovationLeads() throws Exception {
        Long competitionId = 1L;

        User user = UserBuilder.newUser().build();
        UserResource userResource = UserResourceBuilder.newUserResource().build();
        List<CompetitionAssessmentParticipant> competitionParticipants = newCompetitionAssessmentParticipant()
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
        Long innovationLeadUserId = 2L;
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(null);
        ServiceResult<Void> result = service.addInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(Competition.class, competitionId)));
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

        CompetitionAssessmentParticipant savedCompetitionParticipant = new CompetitionAssessmentParticipant();
        savedCompetitionParticipant.setProcess(competition);
        savedCompetitionParticipant.setUser(innovationLead);
        savedCompetitionParticipant.setRole(CompetitionParticipantRole.INNOVATION_LEAD);
        savedCompetitionParticipant.setStatus(ParticipantStatus.ACCEPTED);

        // Verify that the correct CompetitionParticipant is saved
        verify(competitionParticipantRepositoryMock).save(savedCompetitionParticipant);
    }

    @Test
    public void removeInnovationLeadWhenCompetitionParticipantNotFound() throws Exception {
        Long innovationLeadUserId = 2L;

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndUserIdAndRole(competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD))
                .thenReturn(null);
        ServiceResult<Void> result = service.removeInnovationLead(competitionId, innovationLeadUserId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(CompetitionParticipant.class, competitionId, innovationLeadUserId, CompetitionParticipantRole.INNOVATION_LEAD)));
    }

    @Test
    public void removeInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;

        CompetitionAssessmentParticipant competitionParticipant = newCompetitionAssessmentParticipant().build();
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
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findLive()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findLiveCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findProjectSetupCompetitions() throws Exception {
        Competition comp1 = newCompetition().withName("Comp1").withId(competitionId).build();
        Competition comp2 = newCompetition().withName("Comp2").withId(competitionId).build();
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
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findUpcoming()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findUpcomingCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
    }

    @Test
    public void findNonIfsCompetitions() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
        when(publicContentService.findByCompetitionId(any())).thenReturn(serviceSuccess(PublicContentResourceBuilder.newPublicContentResource().build()));
        when(competitionRepositoryMock.findNonIfs()).thenReturn(competitions);

        List<CompetitionSearchResultItem> response = service.findNonIfsCompetitions().getSuccessObjectOrThrowException();

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

        List<CompetitionSearchResultItem> response = service.findFeedbackReleasedCompetitions().getSuccessObjectOrThrowException();

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

        List<CompetitionSearchResultItem> response = service.findFeedbackReleasedCompetitions().getSuccessObjectOrThrowException();

        assertCompetitionSearchResultsEqualToCompetitions(competitions, response);
        assertNull(response.get(0).getOpenDate());
    }

    @Test
    public void findUnsuccessfulApplicationsWhenNoneFound() throws Exception {

        Long competitionId = 1L;

        Page<Application> pagedResult = mock(Page.class);
        when(pagedResult.getContent()).thenReturn(emptyList());
        when(pagedResult.getTotalElements()).thenReturn(0L);
        when(pagedResult.getTotalPages()).thenReturn(0);
        when(pagedResult.getNumber()).thenReturn(0);
        when(pagedResult.getSize()).thenReturn(0);
        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(eq(competitionId), any(), any())).thenReturn(pagedResult);

        ServiceResult<ApplicationPageResource> result = service.findUnsuccessfulApplications(competitionId, 0, 20, "id");
        assertTrue(result.isSuccess());

        ApplicationPageResource unsuccessfulApplicationsPage = result.getSuccessObjectOrThrowException();
        assertTrue(unsuccessfulApplicationsPage.getContent().isEmpty());

    }

    @Test
    public void findUnsuccessfulApplications() throws Exception {

        Long competitionId = 1L;

        Long leadOrganisationId = 7L;
        String leadOrganisationName = "lead Organisation name";
        Organisation leadOrganisation = OrganisationBuilder.newOrganisation()
                .withId(leadOrganisationId)
                .withName(leadOrganisationName)
                .build();

        ProcessRole leadProcessRole = ProcessRoleBuilder.newProcessRole()
                .withRole(LEADAPPLICANT)
                .withOrganisationId(leadOrganisationId)
                .build();

        Application application1 = ApplicationBuilder.newApplication()
                .withId(11L)
                .withProcessRoles(leadProcessRole)
                .build();
        Application application2 = ApplicationBuilder.newApplication()
                .withId(12L)
                .withProcessRoles(leadProcessRole)
                .build();

        List<Application> unsuccessfulApplications = new ArrayList<>();
        unsuccessfulApplications.add(application1);
        unsuccessfulApplications.add(application2);

        ApplicationResource applicationResource1 = ApplicationResourceBuilder.newApplicationResource().build();
        ApplicationResource applicationResource2 = ApplicationResourceBuilder.newApplicationResource().build();

        Page<Application> pagedResult = mock(Page.class);
        when(pagedResult.getContent()).thenReturn(unsuccessfulApplications);
        when(pagedResult.getTotalElements()).thenReturn(2L);
        when(pagedResult.getTotalPages()).thenReturn(1);
        when(pagedResult.getNumber()).thenReturn(0);
        when(pagedResult.getSize()).thenReturn(2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateStateIn(eq(competitionId), any(), any())).thenReturn(pagedResult);
        when(applicationMapperMock.mapToResource(application1)).thenReturn(applicationResource1);
        when(applicationMapperMock.mapToResource(application2)).thenReturn(applicationResource2);
        when(organisationRepositoryMock.findOne(leadOrganisationId)).thenReturn(leadOrganisation);

        ServiceResult<ApplicationPageResource> result = service.findUnsuccessfulApplications(competitionId, 0, 20, "id");
        assertTrue(result.isSuccess());

        ApplicationPageResource unsuccessfulApplicationsPage = result.getSuccessObjectOrThrowException();
        assertTrue(unsuccessfulApplicationsPage.getSize() == 2);
        assertEquals(applicationResource1, unsuccessfulApplicationsPage.getContent().get(0));
        assertEquals(applicationResource2, unsuccessfulApplicationsPage.getContent().get(1));
        assertEquals(leadOrganisationName, unsuccessfulApplicationsPage.getContent().get(0).getLeadOrganisationName());
    }

    @Test
    public void countCompetitions() throws Exception {
        Long countLive = 1L;
        Long countProjectSetup = 2L;
        Long countUpcoming = 3L;
        Long countFeedbackReleased = 4l;
        when(competitionRepositoryMock.countLive()).thenReturn(countLive);
        when(competitionRepositoryMock.countProjectSetup()).thenReturn(countProjectSetup);
        when(competitionRepositoryMock.countUpcoming()).thenReturn(countUpcoming);
        when(competitionRepositoryMock.countFeedbackReleased()).thenReturn(countFeedbackReleased);

        CompetitionCountResource response = service.countCompetitions().getSuccessObjectOrThrowException();

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
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccessObjectOrThrowException();

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
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(INNOVATION_LEAD).build())).build();
        User user = newUser().withId(userResource.getId()).withRoles(Sets.newLinkedHashSet(newRole().withType(INNOVATION_LEAD).build())).build();
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
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccessObjectOrThrowException();

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
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(SUPPORT).build())).build();
        User user = newUser().withId(userResource.getId()).withRoles(Sets.newLinkedHashSet(newRole().withType(SUPPORT).build())).build();
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
        CompetitionSearchResult response = service.searchCompetitions(searchQuery, page, size).getSuccessObjectOrThrowException();

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

        List<OrganisationTypeResource> response = service.getCompetitionOrganisationTypes(competitionId).getSuccessObjectOrThrowException();

        assertEquals(organisationTypeResources, response);
    }

    @Test
    public void testTopLevelNavigationLinkIsSetCorrectly() throws Exception {
        List<Competition> competitions = Lists.newArrayList(newCompetition().withId(competitionId).build());
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

    @Test
    public void getCompetitionOpenQueries() throws Exception {
        List<CompetitionOpenQueryResource> openQueries = singletonList(new CompetitionOpenQueryResource(1L, 1L, "org", 1L, "proj"));
        when(competitionRepositoryMock.getOpenQueryByCompetition(competitionId)).thenReturn(openQueries);

        List<CompetitionOpenQueryResource> response = service.findAllOpenQueries(competitionId).getSuccessObjectOrThrowException();

        assertEquals(1, response.size());
    }

    @Test
    public void countCompetitionOpenQueries() throws Exception {
        Long countOpenQueries = 4l;
        when(competitionRepositoryMock.countOpenQueries(competitionId)).thenReturn(countOpenQueries);

        Long response = service.countAllOpenQueries(competitionId).getSuccessObjectOrThrowException();

        assertEquals(countOpenQueries, response);
    }

    @Test
    public void getPendingSpendProfilesWhenNoProjectsForCompetition() throws Exception {

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(null);

        ServiceResult<List<CompetitionPendingSpendProfilesResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject().isEmpty());

    }

    @Test
    public void getPendingSpendProfilesWhenAllViabilityNotYetApproved() throws Exception {

        Long projectId = 1L;

        Organisation organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

        PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

        Project project1 = newProject().
                withId(projectId).
                withDuration(3L).
                withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2)).
                build();

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(singletonList(project1));
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.REVIEW);

        ServiceResult<List<CompetitionPendingSpendProfilesResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject().isEmpty());

    }

    @Test
    public void getPendingSpendProfilesWhenAllEligibilityNotYetApproved() throws Exception {

        Long projectId = 1L;

        Organisation organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

        PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

        Project project1 = newProject().
                withId(projectId).
                withDuration(3L).
                withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2)).
                build();

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(singletonList(project1));
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(EligibilityState.REVIEW);

        ServiceResult<List<CompetitionPendingSpendProfilesResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject().isEmpty());

    }

    @Test
    public void getPendingSpendProfilesWhenSpendProfileAlreadyGenerated() throws Exception {

        Long projectId = 1L;

        Organisation organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

        PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

        Project project1 = newProject().
                withId(projectId).
                withDuration(3L).
                withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2)).
                build();

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(singletonList(project1));
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);

        SpendProfile spendProfileForOrganisation1 = new SpendProfile();
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation1.getId())).thenReturn(Optional.of(spendProfileForOrganisation1));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        ServiceResult<List<CompetitionPendingSpendProfilesResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject().isEmpty());

    }

    @Test
    public void getPendingSpendProfilesSuccess() throws Exception {

        Long projectId = 1L;

        Organisation organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

        PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

        Application application1 = newApplication().withId(11L).build();

        Project project1 = newProject()
                .withId(projectId)
                .withName("Project Name")
                .withApplication(application1)
                .withDuration(3L)
                .withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2))
                .build();

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(singletonList(project1));
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation1.getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        ServiceResult<List<CompetitionPendingSpendProfilesResource>> result = service.getPendingSpendProfiles(competitionId);
        assertTrue(result.isSuccess());

        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = result.getSuccessObject();
        assertTrue(1 == pendingSpendProfiles.size());

        CompetitionPendingSpendProfilesResource pendingSpendProfilesResource = pendingSpendProfiles.get(0);
        assertEquals(new Long(11L), pendingSpendProfilesResource.getApplicationId());
        assertEquals(new Long(1L), pendingSpendProfilesResource.getProjectId());
        assertEquals("Project Name", pendingSpendProfilesResource.getProjectName());
    }

    @Test
    public void getPendingSpendProfilesMultipleProjectsSuccess() throws Exception {

        Long projectId1 = 1L;
        Long projectId2 = 2L;

        Organisation organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation3 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation4 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

        PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();
        PartnerOrganisation partnerOrganisation3 = newPartnerOrganisation().withOrganisation(organisation3).build();
        PartnerOrganisation partnerOrganisation4 = newPartnerOrganisation().withOrganisation(organisation4).build();

        Application application1 = newApplication().withId(11L).build();

        Project project1 = newProject()
                .withId(projectId1)
                .withName("Project Name 1")
                .withApplication(application1)
                .withDuration(3L)
                .withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2))
                .build();

        Project project2 = newProject()
                .withId(projectId2)
                .withName("Project Name 2")
                .withApplication(application1)
                .withDuration(3L)
                .withPartnerOrganisations(asList(partnerOrganisation3, partnerOrganisation4))
                .build();

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(asList(project1, project2));
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);

        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation3)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation4)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation3)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation4)).thenReturn(EligibilityState.APPROVED);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation1.getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project2.getId(),
                organisation3.getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project2.getId(),
                organisation4.getId())).thenReturn(Optional.empty());

        ServiceResult<List<CompetitionPendingSpendProfilesResource>> result = service.getPendingSpendProfiles(competitionId);
        assertTrue(result.isSuccess());

        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = result.getSuccessObject();
        assertTrue(2 == pendingSpendProfiles.size());

    }

    @Test
    public void countPendingSpendProfilesSuccess() throws Exception {

        Long projectId = 1L;

        Organisation organisation1 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation organisation2 = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

        PartnerOrganisation partnerOrganisation1 = newPartnerOrganisation().withOrganisation(organisation1).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisation2).build();

        Project project1 = newProject()
                .withId(projectId)
                .withName("Project Name")
                .withDuration(3L)
                .withPartnerOrganisations(asList(partnerOrganisation1, partnerOrganisation2))
                .build();

        when(projectRepositoryMock.findByApplicationCompetitionId(competitionId)).thenReturn(singletonList(project1));
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(ViabilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(ViabilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation1)).thenReturn(EligibilityState.APPROVED);
        when(eligibilityWorkflowHandlerMock.getState(partnerOrganisation2)).thenReturn(EligibilityState.APPROVED);

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation1.getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project1.getId(),
                organisation2.getId())).thenReturn(Optional.empty());

        ServiceResult<Integer> result = service.countPendingSpendProfiles(competitionId);
        assertTrue(result.isSuccess());

        assertTrue(1 == result.getSuccessObject());
    }
}
