package org.innovateuk.ifs.project.core.transactional;

import com.newrelic.api.agent.Trace;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationMigrationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectToBeCreatedServiceImpl extends BaseTransactionalService implements ProjectToBeCreatedService {

    @Autowired
    private ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationFundingService applicationFundingService;

    @Autowired
    private KtpProjectNotificationService ktpProjectNotificationService;

    @Autowired
    private ApplicationMigrationService applicationMigrationService;

    @Override
    public Optional<Long> findProjectToCreate(int index) {
        Page<ProjectToBeCreated> page = projectToBeCreatedRepository.findByPendingIsTrue(PageRequest.of(index, 1, Direction.ASC, "application.id"));
        if (page.hasContent()) {
            return Optional.of(page.getContent().get(0).getApplication().getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    @Trace(dispatcher = true)
    public ServiceResult<ScheduleResponse> createProject(long applicationId) {
        return find(projectToBeCreatedRepository.findByApplicationId(applicationId), notFoundError(ProjectToBeCreated.class, applicationId))
                .andOnSuccess(this::createProject)
                .andOnSuccess(this::migrateApplicationIfRequired);
    }

    @Override
    @Transactional
    public ServiceResult<Void> markApplicationReadyToBeCreated(long applicationId, String emailBody) {
        Optional<ProjectToBeCreated> projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(applicationId);
        if (projectToBeCreated.isPresent()) {
            projectToBeCreated.get().setPending(true);
            return serviceSuccess();
        } else {
            return getApplication(applicationId)
                    .andOnSuccessReturnVoid(application -> projectToBeCreatedRepository.save(new ProjectToBeCreated(application, emailBody)));
        }
    }

    @Override
    @Transactional
    public void createAllPendingProjects() {
        Page<ProjectToBeCreated> page = projectToBeCreatedRepository.findByPendingIsTrue(PageRequest.of(0, Integer.MAX_VALUE, Direction.ASC, "application.id"));
        page.getContent().forEach(this::createProject);
    }

    private ServiceResult<ProjectToBeCreated> createProject(ProjectToBeCreated projectToBeCreated) {
        projectToBeCreated.setPending(false);
        return createProject(projectToBeCreated.getApplication(), projectToBeCreated.getEmailBody())
                .andOnSuccessReturn(() -> {
                    projectToBeCreated.setMessage("Success");
                    return projectToBeCreated;
                });
    }

    private ServiceResult<ScheduleResponse> migrateApplicationIfRequired(ProjectToBeCreated projectToBeCreated) {
        Optional<ApplicationMigration> applicationMigration = applicationMigrationService.findByApplicationIdAndStatus(projectToBeCreated.getApplication().getId(), MigrationStatus.CREATED).getSuccess();
        if (applicationMigration.isPresent()) {
            applicationMigrationService.migrateApplication(projectToBeCreated.getApplication().getId())
                    .andOnSuccess(() -> {
                        ApplicationMigration migration = applicationMigration.get();
                        migration.setStatus(MigrationStatus.MIGRATED);
                        applicationMigrationService.updateApplicationMigrationStatus(migration);
                    });
        }

        return serviceSuccess(new ScheduleResponse("Project created: " + projectToBeCreated.getApplication().getId()));
    }

    private ServiceResult<Void> createProject(Application application, String emailBody) {
        if (application.getCompetition().isKtp()) {
            return projectService.createProjectFromApplication(application.getId())
                    .andOnSuccess(() -> ktpProjectNotificationService.sendProjectSetupNotification(application.getId()));
        } else {
            return projectService.createProjectFromApplication(application.getId())
                    .andOnSuccess(() -> applicationFundingService.notifyApplicantsOfFundingDecisions(
                                    new FundingNotificationResource(emailBody, singleMap(application.getId(), FundingDecision.FUNDED))));
        }
    }

    private Map<Long, FundingDecision> singleMap(long applicationId, FundingDecision decision) {
        Map<Long, FundingDecision> map = new HashMap<>();
        map.put(applicationId, decision);
        return map;
    }
}
