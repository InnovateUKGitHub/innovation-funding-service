package org.innovateuk.ifs.project.core.transactional;

import com.newrelic.api.agent.Trace;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationMigrationService;
import org.innovateuk.ifs.commons.error.Error;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
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
            Long applicationId = page.getContent().get(0).getApplication().getId();
            Long migratedApplicationId = migrateApplicationIdIfRequired(applicationId).getSuccess();
            return Optional.of(migratedApplicationId);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    @Trace(dispatcher = true)
    public ServiceResult<ScheduleResponse> createProject(long applicationId) {
        return find(projectToBeCreatedRepository.findByApplicationId(applicationId), notFoundError(ProjectToBeCreated.class, applicationId))
                //.andOnSuccess(this::migrateApplicationIfRequired)
                .andOnSuccess(this::createProject);
        /*return migrateApplicationIfRequired(applicationId)
                .andOnSuccessReturn(migratedApplicationId ->
                    find(projectToBeCreatedRepository.findByApplicationId(migratedApplicationId), notFoundError(ProjectToBeCreated.class, migratedApplicationId))
                            .andOnSuccess(this::createProject)
                )
                .andOnFailure(() ->
                        new ScheduleResponse("Application migration is unsuccessful for application id: " + applicationId)); */
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

        /*ProjectToBeCreated projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(applicationId)
                .orElseGet(() -> getApplication(applicationId)
                        .andOnSuccessReturn(application -> projectToBeCreatedRepository.save(new ProjectToBeCreated(application, emailBody))).getSuccess());
        projectToBeCreated.setPending(true);

        return migrateApplicationIfRequired(projectToBeCreated)
                .andOnSuccessReturnVoid()
                .andOnFailure(() -> serviceFailure(
                        new Error("Application migration is unsuccessful for application id: " + applicationId,
                                HttpStatus.INTERNAL_SERVER_ERROR)));*/
    }

    private ProjectToBeCreated createProjectToBeCreated(long applicationId, String emailBody) {
        return getApplication(applicationId)
                .andOnSuccessReturn(application -> projectToBeCreatedRepository.save(new ProjectToBeCreated(application, emailBody))).getSuccess();
    }

    @Override
    @Transactional
    public void createAllPendingProjects() {
        Page<ProjectToBeCreated> page = projectToBeCreatedRepository.findByPendingIsTrue(PageRequest.of(0, Integer.MAX_VALUE, Direction.ASC, "application.id"));
        page.getContent().forEach(this::createProject);
    }

    private ServiceResult<ScheduleResponse> createProject(ProjectToBeCreated projectToBeCreated) {
        projectToBeCreated.setPending(false);
        return createProject(projectToBeCreated.getApplication(), projectToBeCreated.getEmailBody())
                .andOnSuccessReturn(() -> {
                    projectToBeCreated.setMessage("Success");
                    return new ScheduleResponse("Project created: " + projectToBeCreated.getApplication().getId());
                });
    }

    private ServiceResult<ProjectToBeCreated> migrateApplicationIfRequired(ProjectToBeCreated projectToBeCreated) {
        return applicationMigrationService.findByApplicationIdAndStatus(projectToBeCreated.getApplication().getId(), MigrationStatus.CREATED).getSuccess()
                .map(applicationMigration -> applicationMigrationService.migrateApplication(projectToBeCreated.getApplication().getId())
                        .andOnSuccessReturn(migratedApplication -> {
                            applicationMigration.setStatus(MigrationStatus.MIGRATED);
                            applicationMigrationService.updateApplicationMigrationStatus(applicationMigration);
                            return projectToBeCreated;
                        }))
                .orElse(serviceSuccess(projectToBeCreated));
    }

    private ServiceResult<Long> migrateApplicationIdIfRequired(long applicationId) {
        return applicationMigrationService.findByApplicationIdAndStatus(applicationId, MigrationStatus.CREATED).getSuccess()
                .map(applicationMigration -> applicationMigrationService.migrateApplication(applicationId)
                        .andOnSuccessReturn(migratedApplication -> {
                            applicationMigration.setStatus(MigrationStatus.MIGRATED);
                            applicationMigrationService.updateApplicationMigrationStatus(applicationMigration);
                            return applicationId;
                        }))
                .orElse(serviceSuccess(applicationId));
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
