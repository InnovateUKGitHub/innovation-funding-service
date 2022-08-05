package org.innovateuk.ifs.application.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Slf4j
@Service
public class ApplicationMigrationServiceImpl implements ApplicationMigrationService {

    @Autowired
    private ApplicationMigrationRepository applicationMigrationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Autowired
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Autowired
    private EuGrantTransferRepository euGrantTransferRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private GrantProcessRepository grantProcessRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Autowired
    private ApplicationHorizonWorkProgrammeRepository applicationHorizonWorkProgrammeRepository;

    @Autowired
    private ApplicationExpressionOfInterestConfigRepository applicationExpressionOfInterestConfigRepository;

    @Override
    public ServiceResult<Optional<ApplicationMigration>> findByApplicationIdAndStatus(long applicationId, MigrationStatus status) {
        return serviceSuccess(applicationMigrationRepository.findByApplicationIdAndStatus(applicationId, status));
    }

    @Override
    @Transactional
    public ServiceResult<Void> migrateApplication(long applicationId) {
        return migrateApplication(applicationId, true)
                .andOnSuccessReturnVoid();
    }

    @Override
    @Transactional
    public ServiceResult<Long> migrateApplication(long applicationId, boolean isDeleteApplication) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess(application -> {
                    Application migratedApplication = migrateApplication(application);

                    migrateApplicationExpressionOfInterestConfig(application, migratedApplication);

                    migrateApplicationHorizonWorkProgramme(application, migratedApplication);

                    migrateActivityLog(application, migratedApplication);

                    migrateApplicationFinance(application, migratedApplication);

                    migrateApplicationHiddenFromDashboard(application, migratedApplication);

                    migrateApplicationOrganisationAddress(application, migratedApplication);

                    migrateAverageAssessorScore(application, migratedApplication);

                    migrateEuGrantTransfer(application, migratedApplication);

                    migrateFormInputResponse(application, migratedApplication);

                    migrateProcessRole(application, migratedApplication);

                    migrateProject(application, migratedApplication);

                    migrateProjectToBeCreated(application, migratedApplication);

                    migrateQuestionStatus(application, migratedApplication);

                    migrateGrantProcess(application, migratedApplication);

                    migrateApplicationProcess(application, migratedApplication);

                    migrateAssessmentProcess(application, migratedApplication);

                    migrateInterviewProcess(application, migratedApplication);

                    migrateInterviewAssignmentProcess(application, migratedApplication);

                    migrateReviewProcess(application, migratedApplication);

                    migrateSupporterAssignment(application, migratedApplication);

                    migrateApplicationInvite(application, migratedApplication);

                    migrateApplicationKtaInvite(application, migratedApplication);

                    deleteApplicationDependency(application);

                    if (isDeleteApplication) {
                        deleteApplication(application);
                    }

                    return serviceSuccess(migratedApplication.getId());
                });
    }

    private void deleteApplication(Application application) {
        applicationRepository.delete(application);

        log.debug("Deleted application : " + application.getId());
    }

    private void deleteApplicationDependency(Application application) {
        activityLogRepository.deleteByApplicationId(application.getId());
        grantProcessRepository.deleteByApplicationId(application.getId());
        applicationHiddenFromDashboardRepository.deleteByApplicationId(application.getId());
        applicationExpressionOfInterestConfigRepository.deleteByApplicationId(application.getId());

        log.debug("Deleted application dependency for application : " + application.getId());
    }

    private void migrateApplicationKtaInvite(Application application, Application migratedApplication) {
        applicationKtaInviteRepository.findByApplicationId(application.getId()).ifPresent(
                applicationKtaInvite -> {
                    applicationKtaInvite.setTarget(migratedApplication);
                    applicationKtaInviteRepository.save(applicationKtaInvite);
                }
        );

        log.debug("Migrated application kta invite for application : " + application.getId());
    }

    private void migrateApplicationInvite(Application application, Application migratedApplication) {
        applicationInviteRepository.findByApplicationId(application.getId()).stream()
                .forEach(applicationInvite -> {
                    applicationInvite.setTarget(migratedApplication);
                    applicationInviteRepository.save(applicationInvite);
                });

        log.debug("Migrated application invite for application : " + application.getId());
    }

    private void migrateSupporterAssignment(Application application, Application migratedApplication) {
        supporterAssignmentRepository.findByTargetId(application.getId()).stream()
                .forEach(supporterAssignmentProcess -> {
                    supporterAssignmentProcess.setTarget(migratedApplication);
                    supporterAssignmentRepository.save(supporterAssignmentProcess);
                });

        log.debug("Migrated supporter assignment process for application : " + application.getId());
    }

    private void migrateReviewProcess(Application application, Application migratedApplication) {
        reviewRepository.findByTargetId(application.getId()).stream()
                .forEach(reviewProcess -> {
                    reviewProcess.setTarget(migratedApplication);
                    reviewRepository.save(reviewProcess);
                });

        log.debug("Migrated review process for application : " + application.getId());
    }

    private void migrateInterviewAssignmentProcess(Application application, Application migratedApplication) {
        interviewAssignmentRepository.findByTargetId(application.getId()).stream()
                .forEach(interviewAssignmentProcess -> {
                    interviewAssignmentProcess.setTarget(migratedApplication);
                    interviewAssignmentRepository.save(interviewAssignmentProcess);
                });

        log.debug("Migrated interview assignment process for application : " + application.getId());
    }

    private void migrateInterviewProcess(Application application, Application migratedApplication) {
        interviewRepository.findByTargetId(application.getId()).stream()
                .forEach(interviewProcess -> {
                    interviewProcess.setTarget(migratedApplication);
                    interviewRepository.save(interviewProcess);
                });

        log.debug("Migrated interview process for application : " + application.getId());
    }

    private void migrateAssessmentProcess(Application application, Application migratedApplication) {
        assessmentRepository.findByTargetId(application.getId()).stream()
                .forEach(assessmentProcess -> {
                    assessmentProcess.setTarget(migratedApplication);
                    assessmentRepository.save(assessmentProcess);
                });

        log.debug("Migrated assessment process for application : " + application.getId());
    }

    private void migrateApplicationProcess(Application application, Application migratedApplication) {
        applicationProcessRepository.findByTargetId(application.getId()).stream()
                .forEach(applicationProcess -> {
                    applicationProcess.setTarget(migratedApplication);
                    applicationProcessRepository.save(applicationProcess);
                });

        log.debug("Migrated application process for application : " + application.getId());
    }

    private void migrateGrantProcess(Application application, Application migratedApplication) {
        serviceSuccess(grantProcessRepository.findOneByApplicationId(application.getId()))
                .andOnSuccessReturnVoid(grantProcess -> {
                    if (grantProcess != null) {
                        GrantProcess migratedGrantProcess = new GrantProcess(migratedApplication.getId(), grantProcess.isPending());
                        migratedGrantProcess.setMessage(grantProcess.getMessage());
                        migratedGrantProcess.setLastProcessed(grantProcess.getLastProcessed());
                        migratedGrantProcess.setSentRequested(grantProcess.getSentRequested());
                        migratedGrantProcess.setSentSucceeded(grantProcess.getSentSucceeded());
                        grantProcessRepository.save(migratedGrantProcess);
                    }
                });

        log.debug("Migrated grant process for application : " + application.getId());
    }

    private void migrateQuestionStatus(Application application, Application migratedApplication) {
        questionStatusRepository.findByApplicationId(application.getId()).stream()
                .forEach(questionStatus -> {
                    questionStatus.setApplication(migratedApplication);
                    questionStatusRepository.save(questionStatus);
                });

        log.debug("Migrated question status for application : " + application.getId());
    }

    private void migrateProjectToBeCreated(Application application, Application migratedApplication) {
        projectToBeCreatedRepository.findByApplicationId(application.getId()).ifPresent(
                projectToBeCreated -> {
                    projectToBeCreated.setApplication(migratedApplication);
                    projectToBeCreatedRepository.save(projectToBeCreated);
                }
        );

        log.debug("Migrated project to be created for application : " + application.getId());
    }

    private void migrateProject(Application application, Application migratedApplication) {
        projectRepository.findByApplicationId(application.getId()).ifPresent(
                project -> {
                    project.setApplication(migratedApplication);
                    projectRepository.save(project);
                }
        );

        log.debug("Migrated project for application : " + application.getId());
    }

    private void migrateProcessRole(Application application, Application migratedApplication) {
        processRoleRepository.findByApplicationId(application.getId()).stream()
                .forEach(processRole -> {
                    processRole.setApplicationId(migratedApplication.getId());
                    processRoleRepository.save(processRole);
                });

        log.debug("Migrated process role for application : " + application.getId());
    }

    private void migrateFormInputResponse(Application application, Application migratedApplication) {
        formInputResponseRepository.findByApplicationId(application.getId()).stream()
                .forEach(formInputResponse -> {
                    formInputResponse.setApplication(migratedApplication);
                    formInputResponseRepository.save(formInputResponse);
                });

        log.debug("Migrated form input response for application : " + application.getId());
    }

    private void migrateEuGrantTransfer(Application application, Application migratedApplication) {
        serviceSuccess(euGrantTransferRepository.findByApplicationId(application.getId()))
                .andOnSuccessReturnVoid(euGrantTransfer -> {
                    if (euGrantTransfer != null) {
                        euGrantTransfer.setApplication(migratedApplication);
                        euGrantTransferRepository.save(euGrantTransfer);
                    }
                });

        log.debug("Migrated eu grant transfer for application : " + application.getId());
    }

    private void migrateAverageAssessorScore(Application application, Application migratedApplication) {
        averageAssessorScoreRepository.findByApplicationId(application.getId()).ifPresent(
                averageAssessorScore -> {
                    averageAssessorScore.setApplication(migratedApplication);
                    averageAssessorScoreRepository.save(averageAssessorScore);
                });

        log.debug("Migrated average assessor score for application : " + application.getId());
    }

    private void migrateApplicationOrganisationAddress(Application application, Application migratedApplication) {
        applicationOrganisationAddressRepository.findByApplicationId(application.getId()).stream()
                .forEach(applicationOrganisationAddress -> {
                    applicationOrganisationAddress.setApplication(migratedApplication);
                    applicationOrganisationAddressRepository.save(applicationOrganisationAddress);
                });

        log.debug("Migrated application organisation address for application : " + application.getId());
    }

    private void migrateApplicationHiddenFromDashboard(Application application, Application migratedApplication) {
        applicationHiddenFromDashboardRepository.findByApplicationId(application.getId()).stream()
                .forEach(applicationHiddenFromDashboard -> {
                    ApplicationHiddenFromDashboard migratedApplicationHiddenFromDashboard = new ApplicationHiddenFromDashboard(migratedApplication, applicationHiddenFromDashboard.getUser());
                    migratedApplicationHiddenFromDashboard.setCreatedOn(applicationHiddenFromDashboard.getCreatedOn());
                    applicationHiddenFromDashboardRepository.save(migratedApplicationHiddenFromDashboard);
                });

        log.debug("Migrated application hidden from dashboard for application : " + application.getId());
    }

    private void migrateApplicationFinance(Application application, Application migratedApplication) {
        applicationFinanceRepository.findByApplicationId(application.getId()).stream()
                .forEach(applicationFinance -> {
                    Long applicationFinanceId = applicationFinance.getId();
                    applicationFinance.setApplication(migratedApplication);
                    ApplicationFinance migratedApplicationFinance = applicationFinanceRepository.save(applicationFinance);

                    applicationFinanceRowRepository.findByTargetId(applicationFinanceId).stream()
                            .forEach(applicationFinanceRow -> {
                                applicationFinanceRow.setTarget(migratedApplicationFinance);
                                ApplicationFinanceRow savedClonedApplicationFinanceRow = applicationFinanceRowRepository.save(applicationFinanceRow);

                                financeRowMetaValueRepository.financeRowId(applicationFinanceRow.getId()).stream()
                                        .forEach(financeRowMetaValue -> {
                                            financeRowMetaValue.setFinanceRowId(savedClonedApplicationFinanceRow.getId());
                                        });
                            });
                });

        log.debug("Migrated application finance for application : " + application.getId());
    }

    private void migrateActivityLog(Application application, Application migratedApplication) {
        activityLogRepository.findByApplicationId(application.getId()).stream()
                .forEach(activityLog -> {
                    ActivityLog migratedActivityLog = new ActivityLog(migratedApplication, activityLog.getType(),
                            activityLog.getOrganisation().orElse(null),
                            activityLog.getCompetitionDocument().orElse(null),
                            activityLog.getQuery().orElse(null),
                            activityLog.getCreatedOn(), activityLog.getCreatedBy(), activityLog.getAuthor());
                    activityLogRepository.save(migratedActivityLog);
                });

        log.debug("Migrated activity log for application : " + application.getId());
    }

    private void migrateApplicationHorizonWorkProgramme(Application application, Application migratedApplication) {
        if (migratedApplication.getCompetition().isHorizonEuropeGuarantee()) {
            applicationHorizonWorkProgrammeRepository.findByApplicationId(application.getId()).stream()
                    .forEach(applicationHorizonWorkProgramme -> {
                        applicationHorizonWorkProgramme.setApplicationId(migratedApplication.getId());
                        applicationHorizonWorkProgrammeRepository.save(applicationHorizonWorkProgramme);
                    });

            log.debug("Migrated Horizon Work Programme for application : " + application.getId());
        }
    }

    private void migrateApplicationExpressionOfInterestConfig(Application application, Application migratedApplication) {
        if (application.getApplicationExpressionOfInterestConfig() != null) {
            Long applicationExpressionOfInterestConfigId =  application.getApplicationExpressionOfInterestConfig().getId();
            applicationExpressionOfInterestConfigRepository.findById(applicationExpressionOfInterestConfigId).ifPresent(
                    applicationExpressionOfInterestConfig -> {
                        ApplicationExpressionOfInterestConfig migratedApplicationExpressionOfInterestConfig = ApplicationExpressionOfInterestConfig.builder()
                                .application(migratedApplication)
                                .enabledForExpressionOfInterest(applicationExpressionOfInterestConfig.isEnabledForExpressionOfInterest())
                                .build();
                        applicationExpressionOfInterestConfigRepository.save(migratedApplicationExpressionOfInterestConfig);
                        migratedApplication.setApplicationExpressionOfInterestConfig(migratedApplicationExpressionOfInterestConfig);
                    });

            log.debug("Migrated application expression of interest config for application : " + application.getId());
        }
    }

    private Application migrateApplication(Application application) {
        Application migratedApplication = applicationRepository.save(new Application(application));

        log.debug("Migrated application : " + application.getId());

        return migratedApplication;
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationMigration> updateApplicationMigrationStatus(ApplicationMigration applicationMigration) {
        applicationMigration.setUpdatedOn(ZonedDateTime.now());
        return serviceSuccess(applicationMigrationRepository.save(applicationMigration));
    }
}
