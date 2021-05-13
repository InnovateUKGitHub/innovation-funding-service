package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
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
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_REOPEN_ASSESSMENT_PERIOD;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsBuilder.newGrantTermsAndConditions;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompetitionServiceImplTest extends BaseServiceUnitTest<CompetitionServiceImpl> {

    @Override
    protected CompetitionServiceImpl supplyServiceUnderTest() {
        return new CompetitionServiceImpl();
    }

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private AssessmentPeriodRepository assessmentPeriodRepository;

    @Mock
    private CompetitionMapper competitionMapper;

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsService;

    @Mock
    private OrganisationTypeMapper organisationTypeMapper;

    @Mock
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProjectRepository projectRepository;

    private Long competitionId = 1L;

    @Before
    public void setUp() {
        UserResource userResource = newUserResource().withRoleGlobal(Role.COMP_ADMIN).build();
        setLoggedInUser(userResource);
    }

    @Test
    public void getCompetitionById() {
        Competition competition = new Competition();
        CompetitionResource resource = new CompetitionResource();
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionMapper.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionById(competitionId).getSuccess();

        assertEquals(resource, response);
    }

    @Test
    public void getCompetitionByApplicationId() {
        long applicationId = 456;
        Competition competition = new Competition();
        Application application = newApplication().withCompetition(competition).build();
        CompetitionResource resource = new CompetitionResource();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(competitionMapper.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionByApplicationId(applicationId).getSuccess();

        assertEquals(resource, response);
    }

    @Test
    public void getCompetitionByProjectId() {
        long projectId = 456;
        Competition competition = new Competition();
        Application application = newApplication().withCompetition(competition).build();
        Project project = newProject().withApplication(application).build();
        CompetitionResource resource = new CompetitionResource();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(competitionMapper.mapToResource(competition)).thenReturn(resource);

        CompetitionResource response = service.getCompetitionByProjectId(projectId).getSuccess();

        assertEquals(resource, response);
    }

    @Test
    public void findAll() {
        List<Competition> competitions = Lists.newArrayList(new Competition());
        List<CompetitionResource> resources = Lists.newArrayList(new CompetitionResource());
        when(competitionRepository.findAll()).thenReturn(competitions);
        when(competitionMapper.mapToResource(competitions)).thenReturn(resources);

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
                .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
                .withMilestones(milestones)
                .build();
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentPeriodRepository.findById(competition.getAssessmentPeriods().get(0).getId())).thenReturn(Optional.of(competition.getAssessmentPeriods().get(0)));

        service.closeAssessment(competitionId);

        assertEquals(CompetitionStatus.FUNDERS_PANEL, competition.getCompetitionStatus());
    }

    @Test
    public void reopenAssessmentPeriod() {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE, SUBMISSION_DATE, ASSESSORS_NOTIFIED).build(3);
        milestones.addAll(newMilestone()
                .withDate(ZonedDateTime.now().plusDays(1))
                .withType(ASSESSMENT_CLOSED)
                .build(1));
        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .withId(competitionId)
                .build();

        CompetitionFundedKeyApplicationStatisticsResource keyStatistics = new CompetitionFundedKeyApplicationStatisticsResource();
        keyStatistics.setApplicationsFunded(0);
        keyStatistics.setApplicationsNotFunded(0);
        keyStatistics.setApplicationsOnHold(0);

        when(competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));

        ServiceResult<Void> response = service.reopenAssessmentPeriod(competitionId);

        verify(milestoneRepository).deleteByTypeAndCompetitionId(ASSESSMENT_CLOSED, competitionId);

        assertTrue(response.isSuccess());
        assertEquals(CompetitionStatus.IN_ASSESSMENT, competition.getCompetitionStatus());
    }

    @Test
    public void reopenAssessmentPeriod_cantReopen() {
        List<Milestone> milestones = newMilestone()
                .withDate(ZonedDateTime.now().minusDays(1))
                .withType(OPEN_DATE, SUBMISSION_DATE, ASSESSORS_NOTIFIED, ASSESSMENT_CLOSED).build(4);
        Competition competition = newCompetition().withSetupComplete(true)
                .withMilestones(milestones)
                .build();

        CompetitionFundedKeyApplicationStatisticsResource keyStatistics = new CompetitionFundedKeyApplicationStatisticsResource();
        keyStatistics.setApplicationsFunded(1);
        keyStatistics.setApplicationsNotFunded(1);
        keyStatistics.setApplicationsOnHold(1);

        when(competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));

        ServiceResult<Void> response = service.reopenAssessmentPeriod(competitionId);

        assertTrue(response.isFailure());
        assertTrue(response.getFailure().is(new Error(COMPETITION_CANNOT_REOPEN_ASSESSMENT_PERIOD)));
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
                .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
                .withMilestones(milestones)
                .build();
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(assessmentPeriodRepository.findById(competition.getAssessmentPeriods().get(0).getId())).thenReturn(Optional.of(competition.getAssessmentPeriods().get(0)));

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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatistics));

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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(organisationTypeMapper.mapToResource(organisationTypes)).thenReturn(organisationTypeResources);

        List<OrganisationTypeResource> response = service.getCompetitionOrganisationTypes(competitionId).getSuccess();

        assertEquals(organisationTypeResources, response);
    }

    @Test
    public void getCompetitionOpenQueries() {
        List<CompetitionOpenQueryResource> openQueries = singletonList(new CompetitionOpenQueryResource(1L, 1L, "org", 1L, "proj"));
        when(competitionRepository.getOpenQueryByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE))).thenReturn(openQueries);

        List<CompetitionOpenQueryResource> response = service.findAllOpenQueries(competitionId).getSuccess();

        assertEquals(1, response.size());
    }

    @Test
    public void countCompetitionOpenQueries() {
        Long countOpenQueries = 4l;
        when(competitionRepository.countOpenQueriesByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE))).thenReturn(countOpenQueries);

        Long response = service.countAllOpenQueries(competitionId).getSuccess();

        assertEquals(countOpenQueries, response);
    }

    @Test
    public void getPendingSpendProfiles() {

        List<Object[]> pendingSpendProfiles = singletonList(new Object[]{BigInteger.valueOf(11L), BigInteger.valueOf(1L), new String("Project 1")});

        when(competitionRepository.getPendingSpendProfiles(competitionId)).thenReturn(pendingSpendProfiles);

        ServiceResult<List<SpendProfileStatusResource>> result = service.getPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        List<SpendProfileStatusResource> expectedPendingSpendProfiles = singletonList(new SpendProfileStatusResource(11L, 1L, "Project 1"));
        assertEquals(expectedPendingSpendProfiles, result.getSuccess());
    }

    @Test
    public void countPendingSpendProfiles() {

        final BigDecimal pendingSpendProfileCount = BigDecimal.TEN;
        when(competitionRepository.countPendingSpendProfiles(competitionId)).thenReturn(pendingSpendProfileCount);

        ServiceResult<Long> result = service.countPendingSpendProfiles(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(Long.valueOf(pendingSpendProfileCount.longValue()), result.getSuccess());
    }

    @Test
    public void updateTermsAndConditionsForCompetition() {
        GrantTermsAndConditions termsAndConditions = newGrantTermsAndConditions().build();

        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepository.findById(termsAndConditions.getId()))
                .thenReturn(Optional.of(termsAndConditions));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateTermsAndConditionsForCompetition(competition.getId(), termsAndConditions.getId());

        assertTrue(result.isSuccess());
        assertEquals(competition.getTermsAndConditions().getId(), termsAndConditions.getId());

        //Verify that the entity is saved
        verify(competitionRepository).findById(competition.getId());
        verify(competitionRepository).save(competition);
        verify(grantTermsAndConditionsRepository).findById(termsAndConditions.getId());
    }

    @Test
    public void updateInvalidTermsAndConditionsForCompetition() {
        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepository.findById(competition.getTermsAndConditions().getId())).thenReturn(Optional.empty());
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateTermsAndConditionsForCompetition(competitionId, competition.getTermsAndConditions().getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(GrantTermsAndConditions.class,
                competition.getTermsAndConditions().getId())));
    }

    @Test
    public void updateOtherFundingRulesTermsAndConditionsForCompetition() {
        GrantTermsAndConditions termsAndConditions = newGrantTermsAndConditions().build();

        Competition competition = newCompetition().build();

        when(grantTermsAndConditionsRepository.findById(termsAndConditions.getId()))
                .thenReturn(Optional.of(termsAndConditions));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateOtherFundingRulesTermsAndConditionsForCompetition(competition.getId(), termsAndConditions.getId());

        assertTrue(result.isSuccess());
        assertEquals(competition.getOtherFundingRulesTermsAndConditions().getId(), termsAndConditions.getId());

        //Verify that the entity is saved
        verify(competitionRepository).findById(competition.getId());
        verify(competitionRepository).save(competition);
        verify(grantTermsAndConditionsRepository).findById(termsAndConditions.getId());
    }

    @Test
    public void updateInvalidOtherFundingRulesTermsAndConditionsForCompetition() {
        Competition competition = newCompetition().build();
        long termsAndConditionsId = 999L;

        when(grantTermsAndConditionsRepository.findById(termsAndConditionsId)).thenReturn(Optional.empty());
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Void> result = service.updateOtherFundingRulesTermsAndConditionsForCompetition(competitionId, termsAndConditionsId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(GrantTermsAndConditions.class,
                termsAndConditionsId)));
    }
}