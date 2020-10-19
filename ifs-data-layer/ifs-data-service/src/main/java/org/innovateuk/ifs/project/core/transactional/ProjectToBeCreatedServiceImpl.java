package org.innovateuk.ifs.project.core.transactional;

import com.newrelic.api.agent.Trace;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
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
import java.util.concurrent.ThreadLocalRandom;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectToBeCreatedServiceImpl extends BaseTransactionalService implements ProjectToBeCreatedService {
    private static final int NUMBER_OF_RECORDS_TO_CHECK = 10;

    @Autowired
    private ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationFundingService applicationFundingService;

    @Override
    public Optional<Long> findProjectToCreate() {
        Page<ProjectToBeCreated> page = projectToBeCreatedRepository.findByPendingIsTrue(PageRequest.of(0, NUMBER_OF_RECORDS_TO_CHECK, Direction.ASC, "created"));
        if (page.hasContent()) {
            int index = ThreadLocalRandom.current().nextInt(0, page.getContent().size());
            return Optional.of(page.getContent().get(index).getApplication().getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    @Trace
    public ServiceResult<Void> createProject(long applicationId) {
        return find(projectToBeCreatedRepository.findByApplicationId(applicationId), notFoundError(ProjectToBeCreated.class, applicationId))
                .andOnSuccess(projectToBeCreated -> {
                    projectToBeCreated.setPending(false);
                    return createProject(projectToBeCreated.getApplication(), projectToBeCreated.getEmailBody())
                            .andOnSuccessReturnVoid(() -> projectToBeCreated.setMessage("Success"));
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> markApplicationReadyToBeCreated(long applicationId, String emailBody) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> projectToBeCreatedRepository.save(new ProjectToBeCreated(application, emailBody)));
    }

    private ServiceResult<Void> createProject(Application application, String emailBody) {
        if (application.getCompetition().isKtp()) {
            return serviceSuccess();
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
