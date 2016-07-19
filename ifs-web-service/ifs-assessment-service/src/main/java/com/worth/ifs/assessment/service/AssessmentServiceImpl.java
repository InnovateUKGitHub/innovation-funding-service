package com.worth.ifs.assessment.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * This class contains methods to retrieve and store {@link com.worth.ifs.assessment.resource.AssessmentResource} related data,
 * through the RestService {@link AssessmentRestService}.
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private QuestionService questionService;

    @Override
    public AssessmentResource getById(final Long id) {
        return assessmentRestService.getById(id).getSuccessObjectOrThrowException();
    }

    @Override
    public List<QuestionResource> getAllQuestionsById(Long assessmentId) throws ExecutionException, InterruptedException {
        AssessmentResource assessmentResource = this.getById(assessmentId);
        ProcessRoleResource processRoleResource = processRoleService.getById(assessmentResource.getProcessRole()).get();
        ApplicationResource applicationResource = applicationService.getById(processRoleResource.getApplication());
        return questionService.findByCompetition(applicationResource.getCompetition());
    }

    @Override
    public ServiceResult<Void> rejectApplication(Long assessmentId, String reason, String comment) {
        ProcessOutcomeResource processOutcome = new ProcessOutcomeResource();
        processOutcome.setOutcomeType(AssessmentOutcomes.REJECT.getType());
        processOutcome.setComment(comment);
        processOutcome.setDescription(reason);

        return assessmentRestService.updateStatus(assessmentId, processOutcome).toServiceResult();
    }
}
