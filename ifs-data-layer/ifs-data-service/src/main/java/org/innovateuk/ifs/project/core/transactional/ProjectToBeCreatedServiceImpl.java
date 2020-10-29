package org.innovateuk.ifs.project.core.transactional;

import com.newrelic.api.agent.Trace;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
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
    private ProjectNotificationService projectNotificationService;

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
                .andOnSuccess(projectToBeCreated -> {
                    projectToBeCreated.setPending(false);
                    return createProject(projectToBeCreated.getApplication(), projectToBeCreated.getEmailBody())
                            .andOnSuccessReturn(() -> {
                                projectToBeCreated.setMessage("Success");
                                return new ScheduleResponse("Project created: " + applicationId);
                            });
                });
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

    private ServiceResult<Void> createProject(Application application, String emailBody) {
        if (application.getCompetition().isKtp()) {
            return projectService.createProjectFromApplication(application.getId())
                    .andOnSuccess(() -> projectNotificationService.sendProjectSetupNotification(application.getId()));
        } else {
            return applicationFundingService.notifyApplicantsOfFundingDecisions(new FundingNotificationResource(emailBody, singleMap(application.getId(), FundingDecision.FUNDED)))
                    .andOnSuccess(() -> projectService.createProjectFromApplication(application.getId()))
                    .andOnSuccessReturnVoid();
        }
    }

    private Map<Long, FundingDecision> singleMap(long applicationId, FundingDecision decision) {
        Map<Long, FundingDecision> map = new HashMap<>();
        map.put(applicationId, decision);
        return map;
    }
}
