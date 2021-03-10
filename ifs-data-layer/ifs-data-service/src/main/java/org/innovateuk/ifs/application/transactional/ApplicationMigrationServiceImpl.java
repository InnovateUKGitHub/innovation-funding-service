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
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
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

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationMigrationServiceImpl implements ApplicationMigrationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMigrationRepository applicationMigrationRepository;

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

    @Override
    public ServiceResult<Optional<ApplicationMigration>> findByApplicationIdAndStatus(long applicationId, MigrationStatus status) {
        return serviceSuccess(applicationMigrationRepository.findByApplicationIdAndStatus(applicationId, status));
    }

    @Override
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

                    EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(application.getId());
                    euGrantTransfer.setApplication(migratedApplication);
                    euGrantTransferRepository.save(euGrantTransfer);

                    formInputResponseRepository.findByApplicationId(application.getId()).stream()
                            .forEach(formInputResponse -> {
                                formInputResponse.setApplication(migratedApplication);
                                formInputResponseRepository.save(formInputResponse);
                            });

                    return migratedApplication;
                });
    }
}
