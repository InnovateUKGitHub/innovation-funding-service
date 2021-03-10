package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
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

    @Autowired
    private ApplicationMigrationRepository applicationMigrationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private DeletedApplicationRepository deletedApplicationRepository;

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
    private ApplicationDeletionService applicationDeletionService;

    @Override
    public ServiceResult<Optional<ApplicationMigration>> findByApplicationIdAndStatus(long applicationId, MigrationStatus status) {
        return serviceSuccess(applicationMigrationRepository.findByApplicationIdAndStatus(applicationId, status));
    }

    @Override
    @Transactional
    public ServiceResult<Application> migrateApplication(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(application -> {
                    Application migratedApplication = applicationRepository.save(new Application(application));

                    activityLogRepository.findByApplicationIdOrderByCreatedOnDesc(application.getId()).stream()
                            .forEach(activityLog -> {
                                activityLog.setApplication(migratedApplication);
                                activityLogRepository.save(activityLog);
                            });

                    applicationFinanceRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationFinance -> {
                                applicationFinance.setApplication(migratedApplication);
                                applicationFinanceRepository.save(applicationFinance);
                            });

                    deletedApplicationRepository.findByApplicationId(application.getId()).stream()
                            .forEach(deletedApplicationAudit -> {
                                deletedApplicationAudit.setApplicationId(migratedApplication.getId());
                                deletedApplicationRepository.save(deletedApplicationAudit);
                            });

                    applicationHiddenFromDashboardRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationHiddenFromDashboard -> {
                                applicationHiddenFromDashboard.setApplication(migratedApplication);
                                applicationHiddenFromDashboardRepository.save(applicationHiddenFromDashboard);
                            });

                    applicationOrganisationAddressRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationOrganisationAddress -> {
                                applicationOrganisationAddress.setApplication(migratedApplication);
                                applicationOrganisationAddressRepository.save(applicationOrganisationAddress);
                            });

                    averageAssessorScoreRepository.findByApplicationId(application.getId()).ifPresent(
                            averageAssessorScore -> {
                                averageAssessorScore.setApplication(migratedApplication);
                                averageAssessorScoreRepository.save(averageAssessorScore);
                            });

                    serviceSuccess(euGrantTransferRepository.findByApplicationId(application.getId()))
                            .andOnSuccessReturnVoid(euGrantTransfer -> {
                                if (euGrantTransfer != null) {
                                    euGrantTransfer.setApplication(migratedApplication);
                                    euGrantTransferRepository.save(euGrantTransfer);
                                }
                            });

                    formInputResponseRepository.findByApplicationId(application.getId()).stream()
                            .forEach(formInputResponse -> {
                                formInputResponse.setApplication(migratedApplication);
                                formInputResponseRepository.save(formInputResponse);
                            });

                    processRoleRepository.findByApplicationId(application.getId()).stream()
                            .forEach(processRole -> {
                                processRole.setApplicationId(migratedApplication.getId());
                                processRoleRepository.save(processRole);
                            });

                    projectRepository.findByApplicationId(application.getId()).ifPresent(
                            project -> {
                                project.setApplication(migratedApplication);
                                projectRepository.save(project);
                            }
                    );

                    projectToBeCreatedRepository.findByApplicationId(application.getId()).ifPresent(
                            projectToBeCreated -> {
                                projectToBeCreated.setApplication(migratedApplication);
                                projectToBeCreatedRepository.save(projectToBeCreated);
                            }
                    );

                    questionStatusRepository.findByApplicationId(application.getId()).stream()
                            .forEach(questionStatus -> {
                                questionStatus.setApplication(migratedApplication);
                                questionStatusRepository.save(questionStatus);
                            });

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

                    applicationProcessRepository.findByTargetId(application.getId()).stream()
                            .forEach(applicationProcess -> {
                                applicationProcess.setTarget(migratedApplication);
                                applicationProcessRepository.save(applicationProcess);
                            });

                    assessmentRepository.findByTargetId(application.getId()).stream()
                            .forEach(assessment -> {
                                assessment.setTarget(migratedApplication);
                                assessmentRepository.save(assessment);
                            });

                    interviewRepository.findByTargetId(application.getId()).stream()
                            .forEach(interview -> {
                                interview.setTarget(migratedApplication);
                                interviewRepository.save(interview);
                            });

                    interviewAssignmentRepository.findByTargetId(application.getId()).stream()
                            .forEach(interviewAssignment -> {
                                interviewAssignment.setTarget(migratedApplication);
                                interviewAssignmentRepository.save(interviewAssignment);
                            });

                    reviewRepository.findByTargetId(application.getId()).stream()
                            .forEach(review -> {
                                review.setTarget(migratedApplication);
                                reviewRepository.save(review);
                            });

                    supporterAssignmentRepository.findByTargetId(application.getId()).stream()
                            .forEach(supporterAssignment -> {
                                supporterAssignment.setTarget(migratedApplication);
                                supporterAssignmentRepository.save(supporterAssignment);
                            });

                    applicationInviteRepository.findByApplicationId(application.getId()).stream()
                            .forEach(applicationInvite -> {
                                applicationInvite.setTarget(migratedApplication);
                                applicationInviteRepository.save(applicationInvite);
                            });

                    applicationKtaInviteRepository.findByApplicationId(application.getId()).ifPresent(
                            applicationKtaInvite -> {
                                applicationKtaInvite.setTarget(migratedApplication);
                                applicationKtaInviteRepository.save(applicationKtaInvite);
                            }
                    );

                    // TODO: Identify the reason why it is failing
                    //applicationDeletionService.deleteApplication(application.getId());

                    return migratedApplication;
                });
    }

    @Override
    public ServiceResult<ApplicationMigration> updateApplicationMigrationStatus(ApplicationMigration applicationMigration) {
        applicationMigration.setUpdatedOn(ZonedDateTime.now());
        return serviceSuccess(applicationMigrationRepository.save(applicationMigration));
    }
}
