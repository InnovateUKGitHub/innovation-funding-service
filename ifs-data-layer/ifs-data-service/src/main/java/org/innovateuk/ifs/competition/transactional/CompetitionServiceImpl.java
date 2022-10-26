package org.innovateuk.ifs.competition.transactional;

import lombok.extern.log4j.Log4j2;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.repository.AssessmentPeriodRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.BasicFileAndContents;
import org.innovateuk.ifs.file.resource.FileAndContents;
import org.innovateuk.ifs.file.service.FileEntryService;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_RELEASE_FEEDBACK;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_CANNOT_REOPEN_ASSESSMENT_PERIOD;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of Competitions
 */
@SuppressWarnings("unchecked")
@Service
@Log4j2
public class CompetitionServiceImpl extends BaseTransactionalService implements CompetitionService {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private OrganisationTypeMapper organisationTypeMapper;

    @Autowired
    private CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProjectToBeCreatedService projectToBeCreatedService;

    @Autowired
    protected AssessmentPeriodRepository assessmentPeriodRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CrmService crmService;

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionSetupCompetitionService questionSetupCompetitionService;
    public static final String EQUALITY_DIVERSITY_AND_INCLUSION = "Equality, diversity and inclusion";

    @Override
    public ServiceResult<CompetitionResource> getCompetitionById(long id) {
        return findCompetitionById(id).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    private ServiceResult<Competition> findCompetitionById(long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id));
    }

    @Override
    public ServiceResult<CompetitionResource> getCompetitionByApplicationId(long applicationId) {
        return findCompetitionByApplicationId(applicationId).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    private ServiceResult<Competition> findCompetitionByApplicationId(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(app -> app.getCompetition());
    }

    @Override
    public ServiceResult<CompetitionResource> getCompetitionByProjectId(long projectId) {
        return findCompetitionByProjectId(projectId).andOnSuccess(comp -> serviceSuccess(competitionMapper.mapToResource(comp)));
    }

    private ServiceResult<Competition> findCompetitionByProjectId(long projectId) {
        return find(projectRepository.findById(projectId), notFoundError(Project.class, projectId))
                .andOnSuccessReturn(proj -> proj.getApplication().getCompetition());
    }

    @Override
    public ServiceResult<List<OrganisationTypeResource>> getCompetitionOrganisationTypes(long id) {
        return find(competitionRepository.findById(id), notFoundError(OrganisationType.class, id)).andOnSuccess(comp -> serviceSuccess((List) organisationTypeMapper.mapToResource(comp.getLeadApplicantTypes())));
    }

    @Override
    public ServiceResult<List<CompetitionResource>> findAll() {
        return serviceSuccess((List) competitionMapper.mapToResource(
                competitionRepository.findAll()
        ));
    }

    @Override
    @Transactional
    public ServiceResult<Void> closeAssessment(long competitionId) {
        ServiceResult<Competition> competitionResult = getCompetition(competitionId);
        ServiceResult<Void> result = competitionResult.andOnSuccessReturn(competition -> competition.getAssessmentPeriods().get(0).getId())
                .andOnSuccess(assessmentPeriodId -> closeAssessmentByAssessmentPeriod(assessmentPeriodId));
        return result.andOnSuccess(() -> competitionResult.getSuccess().isLoan() ? crmService.syncCrmCompetitionAssessment(competitionId) : result);
    }

    @Override
    @Transactional
    public ServiceResult<Void> closeAssessmentByAssessmentPeriod(long assessmentPeriodId) {
        return find(assessmentPeriodRepository.findById(assessmentPeriodId), notFoundError(Application.class, assessmentPeriodId))
                .andOnSuccess(assessmentPeriod -> {
                    Competition competition = assessmentPeriod.getCompetition();
                    ServiceResult<Void> result = competition.isKtp() ? markApplicationsToBeCreatedOnCloseAssessmentKtp(competition, assessmentPeriod) : serviceSuccess();
                    competition.closeAssessment(ZonedDateTime.now(), assessmentPeriod);
                    return result;
                });
    }

    private ServiceResult<Void> markApplicationsToBeCreatedOnCloseAssessmentKtp(Competition competition, AssessmentPeriod assessmentPeriod) {
        List<Application> applicationsToMark = competition.isAlwaysOpen() ?
                applicationRepository.findByCompetitionIdAndAssessmentPeriodIdAndApplicationProcessActivityStateIn(competition.getId(), assessmentPeriod.getId(), newArrayList(SUBMITTED)) :
                applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateIn(competition.getId(), newArrayList(SUBMITTED));
        return aggregate(applicationsToMark.stream()
                .map(Application::getId)
                .map(id -> projectToBeCreatedService.markApplicationReadyToBeCreated(id, null))
                .collect(toList())).andOnSuccessReturnVoid();
    }

    @Override
    @Transactional
    public ServiceResult<Void> reopenAssessmentPeriod(long competitionId) {
        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccess();
        if (!keyStatisticsResource.isCanManageFundingNotifications()) {
            milestoneRepository.deleteByTypeAndCompetitionId(MilestoneType.ASSESSMENT_CLOSED, competitionId);
            return serviceSuccess();
        } else {
            return serviceFailure(new Error(COMPETITION_CANNOT_REOPEN_ASSESSMENT_PERIOD));
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyAssessors(long competitionId) {
        return getCompetition(competitionId)
                .andOnSuccessReturn(competition -> competition.getAssessmentPeriods().get(0).getId())
                .andOnSuccess(assessmentPeriodId -> notifyAssessorsByAssessmentPeriodId(assessmentPeriodId));
    }

    @Override
    @Transactional
    public ServiceResult<Void> notifyAssessorsByAssessmentPeriodId(long id) {
        return find(assessmentPeriodRepository.findById(id), notFoundError(Application.class, id))
                .andOnSuccess(assessmentPeriod -> {
                    assessmentPeriod.getCompetition().notifyAssessors(ZonedDateTime.now(), assessmentPeriod);
                    return serviceSuccess();
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> releaseFeedback(long competitionId) {
        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccess();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId).get();
            competition.releaseFeedback(ZonedDateTime.now());
            return serviceSuccess();
        } else {
            return serviceFailure(new Error(COMPETITION_CANNOT_RELEASE_FEEDBACK));
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> manageInformState(long competitionId) {
        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(competitionId)
                        .getSuccess();
        if (keyStatisticsResource.isCanReleaseFeedback()) {
            Competition competition = competitionRepository.findById(competitionId).get();
            // at the moment, only Always open competition cannot be in inform state if there is an application On Hold
            if (!competition.isAlwaysOpen() || (competition.isAlwaysOpen() && keyStatisticsResource.getApplicationsOnHold() == 0)) {
                competition.setFundersPanelEndDate(ZonedDateTime.now());
            }
        }
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<CompetitionOpenQueryResource>> findAllOpenQueries(long competitionId) {
        return serviceSuccess(competitionRepository.getOpenQueryByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE)));
    }

    @Override
    public ServiceResult<Long> countAllOpenQueries(long competitionId) {
        return serviceSuccess(competitionRepository.countOpenQueriesByCompetitionAndProjectStateNotIn(competitionId, asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE)));
    }

    @Override
    public ServiceResult<List<SpendProfileStatusResource>> getPendingSpendProfiles(long competitionId) {

        List<Object[]> pendingSpendProfiles = competitionRepository.getPendingSpendProfiles(competitionId);
        return serviceSuccess(simpleMap(pendingSpendProfiles, object ->
                new SpendProfileStatusResource(((BigInteger) object[0]).longValue(), ((BigInteger) object[1]).longValue(), (String) object[2])));
    }

    @Override
    public ServiceResult<Long> countPendingSpendProfiles(long competitionId) {

        return serviceSuccess(competitionRepository.countPendingSpendProfiles(competitionId).longValue());
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateTermsAndConditionsForCompetition(long competitionId, long termsAndConditionsId) {
        Optional<GrantTermsAndConditions> termsAndConditions = grantTermsAndConditionsRepository.findById(termsAndConditionsId);
        if (termsAndConditions.isPresent()) {
            return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                    .andOnSuccess(competition -> {
                        competition.setTermsAndConditions(termsAndConditions.get());
                        competitionRepository.save(competition);
                        return serviceSuccess();
                    });
        }
        return serviceFailure(notFoundError(GrantTermsAndConditions.class, termsAndConditionsId));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateOtherFundingRulesTermsAndConditionsForCompetition(long competitionId, long termsAndConditionsId) {
        Optional<GrantTermsAndConditions> termsAndConditions = grantTermsAndConditionsRepository.findById(termsAndConditionsId);
        if (termsAndConditions.isPresent()) {
            return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                    .andOnSuccess(competition -> {
                        competition.setOtherFundingRulesTermsAndConditions(termsAndConditions.get());
                        competitionRepository.save(competition);
                        return serviceSuccess();
                    });
        }
        return serviceFailure(notFoundError(GrantTermsAndConditions.class, termsAndConditionsId));
    }

    @Override
    public ServiceResult<FileAndContents> downloadTerms(long competitionId) {
        return findCompetitionById(competitionId)
                .andOnSuccess(c -> find(c.getCompetitionTerms(), notFoundError(FileEntry.class))
                        .andOnSuccess(fe -> fileEntryService.findOne(fe.getId()))
                        .andOnSuccess(fe -> fileService.getFileByFileEntryId(fe.getId())
                                .andOnSuccessReturn(is -> new BasicFileAndContents(fe, is))
                        )
                );
    }

    @Override
    public ServiceResult<Boolean> hasEDIQuestion(long competitionId) {
        return serviceSuccess(questionService.findByCompetition(competitionId)
                .getSuccess()
                .stream()
                .anyMatch(this::isEDIQuestion));
    }

    private boolean isEDIQuestion(QuestionResource question) {
        return (QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION.equals(question.getQuestionSetupType()))
                || (question.getShortName() != null && question.getShortName().contains(EQUALITY_DIVERSITY_AND_INCLUSION));
    }

    @Override
    public ServiceResult<Void> includeSupportingInformationSectionForCompetition(long competitionId, boolean projectImpactSurveyApplicable) {
        Competition competition = getCompetition(competitionId).getSuccess();


        if (projectImpactSurveyApplicable) {
            boolean supportingInformationSectionPresent = competition.getSections().stream().anyMatch(section -> SectionType.SUPPORTING_INFORMATION.equals(section.getType()));
            if (!supportingInformationSectionPresent) {

                Optional<Section> tAndCSupportingInformation =
                        competition.getSections().stream()
                                .filter(section -> SectionType.TERMS_AND_CONDITIONS.equals(section.getType()))
                                .findFirst();

                Section supportingInformation = CommonBuilders.supportingInformation().build();
                if (tAndCSupportingInformation.isEmpty()) {
                    return serviceFailure(new Error(String.format("Cannot continue, section: %s is missing", SectionType.TERMS_AND_CONDITIONS), HttpStatus.BAD_REQUEST));
                } else {
                    supportingInformation.setPriority(tAndCSupportingInformation.get().getPriority() - 1);
                    supportingInformation.setCompetition(competition);
                }


                Section savedSupportingInformation = sectionRepository.save(supportingInformation);
                AtomicInteger supportDocumentQuestionPriority = new AtomicInteger(0);
                savedSupportingInformation.getQuestions().forEach(question -> {
                    question.setPriority(supportDocumentQuestionPriority.getAndIncrement());
                    question.setCompetition(competition);
                    question.setSection(savedSupportingInformation);
                    questionRepository.save(question);
                });
            } else {
                log.info("Section:{} already configured, do nothing", SectionType.SUPPORTING_INFORMATION);
            }
        } else {
            competition.getSections().stream().filter(section -> SectionType.SUPPORTING_INFORMATION.equals(section.getType())).findFirst().ifPresentOrElse(section -> {
                        section.getQuestions().forEach(question -> questionRepository.deleteUsingId(question.getId()));
                        sectionRepository.deleteUsingId(section.getId());
                    },
                    () -> log.info("Nothing to delete, section:{} not configured", SectionType.SUPPORTING_INFORMATION));
        }

        return serviceSuccess();
    }
}

