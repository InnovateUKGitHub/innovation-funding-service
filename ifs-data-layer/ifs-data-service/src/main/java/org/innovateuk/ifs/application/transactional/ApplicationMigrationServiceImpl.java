package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationHiddenFromDashboard;
import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
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

@Service
public class ApplicationMigrationServiceImpl implements ApplicationMigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationMigrationServiceImpl.class);

    @Autowired
    private ApplicationMigrationRepository applicationMigrationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

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

    @Override
    public ServiceResult<Optional<ApplicationMigration>> findByApplicationIdAndStatus(long applicationId, MigrationStatus status) {
        return serviceSuccess(applicationMigrationRepository.findByApplicationIdAndStatus(applicationId, status));
    }

    @Override
    @Transactional
    public ServiceResult<Void> migrateApplication(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccess(application -> {
                    Application migratedApplication = applicationRepository.save(new Application(application));

                    LOG.debug("Migrated application : " + application.getId());

                    activityLogRepository.findByApplicationId(application.getId()).stream()
                            .forEach(activityLog -> {
                                ActivityLog migratedActivityLog = new ActivityLog(migratedApplication, activityLog.getType(),
                                        activityLog.getOrganisation().orElse(null),
                                        activityLog.getCompetitionDocument().orElse(null),
                                        activityLog.getQuery().orElse(null),
                                        activityLog.getCreatedOn(), activityLog.getCreatedBy(), activityLog.getAuthor());
                                activityLogRepository.save(migratedActivityLog);
                            });

                    LOG.debug("Migrated activity log for application : " + application.getId());

                    applicationFinanceRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationFinance -> {
                                applicationFinance.setApplication(migratedApplication);
                                applicationFinanceRepository.save(applicationFinance);
                            });

                    LOG.debug("Migrated application finance for application : " + application.getId());

                    applicationHiddenFromDashboardRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationHiddenFromDashboard -> {
                                ApplicationHiddenFromDashboard migratedApplicationHiddenFromDashboard = new ApplicationHiddenFromDashboard(migratedApplication, applicationHiddenFromDashboard.getUser());
                                migratedApplicationHiddenFromDashboard.setCreatedOn(applicationHiddenFromDashboard.getCreatedOn());
                                applicationHiddenFromDashboardRepository.save(migratedApplicationHiddenFromDashboard);
                            });

                    LOG.debug("Migrated application hidden from dashboard for application : " + application.getId());

                    applicationOrganisationAddressRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationOrganisationAddress -> {
                                applicationOrganisationAddress.setApplication(migratedApplication);
                                applicationOrganisationAddressRepository.save(applicationOrganisationAddress);
                            });

                    LOG.debug("Migrated application organisation address for application : " + application.getId());

                    averageAssessorScoreRepository.findByApplicationId(application.getId()).ifPresent(
                            averageAssessorScore -> {
                                averageAssessorScore.setApplication(migratedApplication);
                                averageAssessorScoreRepository.save(averageAssessorScore);
                            });

                    LOG.debug("Migrated average assessor score for application : " + application.getId());

                    serviceSuccess(euGrantTransferRepository.findByApplicationId(application.getId()))
                            .andOnSuccessReturnVoid(euGrantTransfer -> {
                                if (euGrantTransfer != null) {
                                    euGrantTransfer.setApplication(migratedApplication);
                                    euGrantTransferRepository.save(euGrantTransfer);
                                }
                            });

                    LOG.debug("Migrated eu grant transfer for application : " + application.getId());

                    formInputResponseRepository.findByApplicationId(application.getId()).stream()
                            .forEach(formInputResponse -> {
                                formInputResponse.setApplication(migratedApplication);
                                formInputResponseRepository.save(formInputResponse);
                            });

                    LOG.debug("Migrated form input response for application : " + application.getId());

                    processRoleRepository.findByApplicationId(application.getId()).stream()
                            .forEach(processRole -> {
                                processRole.setApplicationId(migratedApplication.getId());
                                processRoleRepository.save(processRole);
                            });

                    LOG.debug("Migrated process role for application : " + application.getId());

                    projectRepository.findByApplicationId(application.getId()).ifPresent(
                            project -> {
                                project.setApplication(migratedApplication);
                                projectRepository.save(project);
                            }
                    );

                    LOG.debug("Migrated project for application : " + application.getId());

                    projectToBeCreatedRepository.findByApplicationId(application.getId()).ifPresent(
                            projectToBeCreated -> {
                                projectToBeCreated.setApplication(migratedApplication);
                                projectToBeCreatedRepository.save(projectToBeCreated);
                            }
                    );

                    LOG.debug("Migrated project to be created for application : " + application.getId());

                    questionStatusRepository.findByApplicationId(application.getId()).stream()
                            .forEach(questionStatus -> {
                                questionStatus.setApplication(migratedApplication);
                                questionStatusRepository.save(questionStatus);
                            });

                    LOG.debug("Migrated question status for application : " + application.getId());

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

                    LOG.debug("Migrated grant process for application : " + application.getId());

                    applicationProcessRepository.findByTargetId(application.getId()).stream()
                            .forEach(applicationProcess -> {
                                applicationProcess.setTarget(migratedApplication);
                                applicationProcessRepository.save(applicationProcess);
                            });

                    LOG.debug("Migrated application process for application : " + application.getId());

                    assessmentRepository.findByTargetId(application.getId()).stream()
                            .forEach(assessmentProcess -> {
                                assessmentProcess.setTarget(migratedApplication);
                                assessmentRepository.save(assessmentProcess);
                            });

                    LOG.debug("Migrated assessment process for application : " + application.getId());

                    interviewRepository.findByTargetId(application.getId()).stream()
                            .forEach(interviewProcess -> {
                                interviewProcess.setTarget(migratedApplication);
                                interviewRepository.save(interviewProcess);
                            });

                    LOG.debug("Migrated interview process for application : " + application.getId());

                    interviewAssignmentRepository.findByTargetId(application.getId()).stream()
                            .forEach(interviewAssignmentProcess -> {
                                interviewAssignmentProcess.setTarget(migratedApplication);
                                interviewAssignmentRepository.save(interviewAssignmentProcess);
                            });

                    LOG.debug("Migrated interview assignment process for application : " + application.getId());

                    reviewRepository.findByTargetId(application.getId()).stream()
                            .forEach(reviewProcess -> {
                                reviewProcess.setTarget(migratedApplication);
                                reviewRepository.save(reviewProcess);
                            });

                    LOG.debug("Migrated review process for application : " + application.getId());

                    supporterAssignmentRepository.findByTargetId(application.getId()).stream()
                            .forEach(supporterAssignmentProcess -> {
                                supporterAssignmentProcess.setTarget(migratedApplication);
                                supporterAssignmentRepository.save(supporterAssignmentProcess);
                            });

                    LOG.debug("Migrated supporter assignment process for application : " + application.getId());

                    applicationInviteRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationInvite -> {
                                applicationInvite.setTarget(migratedApplication);
                                applicationInviteRepository.save(applicationInvite);
                            });

                    LOG.debug("Migrated application invite for application : " + application.getId());

                    applicationKtaInviteRepository.findByApplicationId(application.getId()).ifPresent(
                            applicationKtaInvite -> {
                                applicationKtaInvite.setTarget(migratedApplication);
                                applicationKtaInviteRepository.save(applicationKtaInvite);
                            }
                    );

                    LOG.debug("Migrated application kta invite for application : " + application.getId());

                    deleteApplication(application);

                    return serviceSuccess();
                });
    }

    private void deleteApplication(Application application) {
        activityLogRepository.deleteByApplicationId(application.getId());
        grantProcessRepository.deleteByApplicationId(application.getId());
        applicationHiddenFromDashboardRepository.deleteByApplicationId(application.getId());
        applicationRepository.delete(application);
        LOG.debug("Deleted application : " + application.getId());
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationMigration> updateApplicationMigrationStatus(ApplicationMigration applicationMigration) {
        applicationMigration.setUpdatedOn(ZonedDateTime.now());
        return serviceSuccess(applicationMigrationRepository.save(applicationMigration));
    }
}
