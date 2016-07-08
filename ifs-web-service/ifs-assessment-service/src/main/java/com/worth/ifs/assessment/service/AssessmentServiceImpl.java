package com.worth.ifs.assessment.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


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
    private CompetitionService competitionService;
    @Autowired
    private QuestionService questionService;



    @Override
    public AssessmentResource getById(final Long id) {
        return assessmentRestService.getById(id).getSuccessObjectOrThrowException();
    }
    @Override
    public List<QuestionResource> getAllQuestionsById(Long assessmentId) throws ExecutionException, InterruptedException {
        ProcessRoleResource processRoleResource = processRoleService.getById(this.getById(assessmentId).getProcessRole()).get();
        ApplicationResource applicationResource = applicationService.getById(processRoleResource.getApplication());
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        return questionService.findByCompetition(competitionResource.getId());
    }
}
