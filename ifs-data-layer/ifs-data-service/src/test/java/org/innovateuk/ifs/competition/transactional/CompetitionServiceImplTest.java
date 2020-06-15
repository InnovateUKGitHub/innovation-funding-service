package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsBuilder.newGrantTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void setUp(){
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();
        User user = newUser().withId(userResource.getId()).withRoles(singleton(Role.COMP_ADMIN)).build();
        setLoggedInUser(userResource);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        MilestoneResource milestone = newMilestoneResource().withType(MilestoneType.OPEN_DATE).withDate(ZonedDateTime.now()).build();
        when(milestoneService.getMilestoneByTypeAndCompetitionId(eq(MilestoneType.OPEN_DATE), anyLong())).thenReturn(serviceSuccess(milestone));
    }

    @Test
    public void getCompetitionById() {
        Competition competition = new Competition();
        CompetitionResource resource = new CompetitionResource();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionMapperMock.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionById(competitionId).getSuccess();

        assertEquals(resource, response);
    }

    @Test
    public void findAll() {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        List<CompetitionResource> resources = Lists.newArrayList(new CompetitionResource());
        when(competitionRepositoryMock.findAll()).thenReturn(competitions);
        when(competitionMapperMock.mapToResource(competitions)).thenReturn(resources);

        List<CompetitionResource> response = service.findAll().getSuccess();

        assertEquals(resources, response);
    }

    @Test
    public void closeAssessment() {
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
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));

        service.closeAssessment(competitionId);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }


    @Test
    public void notifyAssessors() {
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
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));

        service.notifyAssessors(competitionId);

        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void releaseFeedback() {
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

        CompetitionType competitionType = newCompetitionType()
                .withName("Sector")
                .build();

        Competition competition = newCompetition()
                .withSetupComplete(true)
                .withCompetitionType(competitionType)
                .withMilestones(milestones)
                .build();

        CompetitionFundedKeyApplicationStatisticsResource keyStatistics = new CompetitionFundedKeyApplicationStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(5);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.releaseFeedback(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.PROJECT_SETUP, competition.getCompetitionStatus());
    }

    @Test
    public void releaseFeedback_cantRelease() {
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

        CompetitionFundedKeyApplicationStatisticsResource keyStatistics = new CompetitionFundedKeyApplicationStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(4);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.releaseFeedback(competitionId);

        assertTrue(response.isFailure());
        assertTrue(response.getFailure().is(new Error(COMPETITION_CANNOT_RELEASE_FEEDBACK)));
        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }

    @Test
    public void manageInformState() {
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

        CompetitionFundedKeyApplicationStatisticsResource keyStatistics = new CompetitionFundedKeyApplicationStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(5);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.manageInformState(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.ASSESSOR_FEEDBACK, competition.getCompetitionStatus());
    }

    @Test
    public void manageInformState_noStateChange() {
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

        CompetitionFundedKeyApplicationStatisticsResource keyStatistics = new CompetitionFundedKeyApplicationStatisticsResource();
        keyStatistics.setApplicationsAwaitingDecision(0);
        keyStatistics.setApplicationsSubmitted(5);
        keyStatistics.setApplicationsNotifiedOfDecision(4);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatistics));

        ServiceResult<Void> response = service.manageInformState(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void getCompetitionOrganisationTypesById() {
        List<OrganisationType> organisationTypes  = newOrganisationType().build(2);
        List<OrganisationTypeResource> organisationTypeResources = newOrganisationTypeResource().build(2);
        Competition competition = new Competition();
        competition.setLeadApplicantTypes(organisationTypes);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(organisationTypeMapperMock.mapToResource(organisationTypes)).thenReturn(organisationTypeResources);

        List<OrganisationTypeResource> response = service.getCompetitionOrganisationTypes(competitionId).getSuccess();

        assertEquals(organisationTypeResources, response);
    }

    @Test
    public void getCompetitionOpenQueries() {
        List<CompetitionOpenQueryResource> openQueries = singletonList(new CompetitionOpenQueryResource(1L, 1L, "org", 1L, "proj"));
        when(competitionRepositoryMock.getOpenQueryByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE))).thenReturn(openQueries);

        List<CompetitionOpenQueryResource> response = service.findAllOpenQueries(competitionId).getSuccess();

        assertEquals(1, response.size());
    }

    @Test
    public void countCompetitionOpenQueries() {
        Long countOpenQueries = 4l;
        when(competitionRepositoryMock.countOpenQueriesByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE))).thenReturn(countOpenQueries);

        Long response = service.countAllOpenQueries(competitionId).getSuccess();

        assertEquals(countOpenQueries, response);
    }

    @Test
    public void getPendingSpendProfiles() {

        List<Object[]> pendingSpendProfiles = singletonList(new Object[]{BigInteger.valueOf(11L), BigInteger.valueOf(1L), new String("Project 1")});

        when(competitionRepositoryMock.getPendingSpendProfiles(competitionId)).thenReturn(pendingSpendProfiles);

        ServiceResult<List<SpendProfileStatusResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        List<SpendProfileStatusResource> expectedPendingSpendProfiles = singletonList(new SpendProfileStatusResource(11L, 1L, "Project 1"));
        assertEquals(expectedPendingSpendProfiles, result.getSuccess());
    }

    @Test
    public void countPendingSpendProfiles() {

        final BigDecimal pendingSpendProfileCount = BigDecimal.TEN;
        when(competitionRepositoryMock.countPendingSpendProfiles(competitionId)).thenReturn(pendingSpendProfileCount);

        ServiceResult<Long> result = service.countPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(Long.valueOf(pendingSpendProfileCount.longValue()), result.getSuccess());
    }

    @Test
    public void updateTermsAndConditionsForCompetition() {
        GrantTermsAndConditions termsAndConditions = newGrantTermsAndConditions().build();

        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepositoryMock.findById(termsAndConditions.getId()))
                .thenReturn(Optional.of(termsAndConditions));
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditions.getId());

        assertTrue(result.isSuccess());
        assertEquals(competition.getTermsAndConditions().getId(), termsAndConditions.getId());

        //Verify that the entity is saved
        verify(competitionRepositoryMock).findById(competition.getId());
        verify(competitionRepositoryMock).save(competition);
        verify(grantTermsAndConditionsRepositoryMock).findById(termsAndConditions.getId());
    }

    @Test
    public void updateInvalidTermsAndConditionsForCompetition() {
        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepositoryMock.findById(competition.getTermsAndConditions().getId())).thenReturn(Optional.empty());
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateTermsAndConditionsForCompetition(competitionId, competition.getTermsAndConditions().getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(GrantTermsAndConditions.class,
                competition.getTermsAndConditions().getId())));
    }
}