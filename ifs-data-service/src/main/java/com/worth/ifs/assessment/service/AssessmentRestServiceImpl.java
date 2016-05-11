package com.worth.ifs.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessmentResourceListType;

/**
 * AssessmentRestServiceImpl is a utility for CRUD operations on {@link Assessment}.
 * This class connects to the {@link com.worth.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class AssessmentRestServiceImpl extends BaseRestService implements AssessmentRestService {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;

    @Override
    public RestResult<List<AssessmentResource>> getAllByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return getWithRestResult(assessmentRestURL +"/findAssessmentsByCompetition/" + assessorId + "/" + competitionId , assessmentResourceListType());
    }

    @Override
    public RestResult<AssessmentResource> getOneByProcessRole(Long processRoleId) {
        return getWithRestResult(assessmentRestURL +"/findAssessmentByProcessRole/" + processRoleId, AssessmentResource.class);
    }

    @Override
    public RestResult<Integer> getTotalAssignedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return getWithRestResult(assessmentRestURL +"/totalAssignedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }

    @Override
    public RestResult<Integer> getTotalSubmittedByAssessorAndCompetition(Long assessorId, Long competitionId) {
        return getWithRestResult(assessmentRestURL +"/totalSubmittedAssessmentsByCompetition/" + assessorId + "/" + competitionId, Integer.class);
    }

    @Override
    public RestResult<Void> respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String reason, String observations) {

        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("decision", decision);
        node.put("reason", reason);
        node.put("observations", HtmlUtils.htmlEscape(observations));

        return postWithRestResult(assessmentRestURL +"/respondToAssessmentInvitation/", node, Void.class);
    }

    @Override
    public RestResult<Void> saveAssessmentSummary(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments) {
        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        node.put("applicationId", applicationId);
        node.put("suitableValue", suitableValue);
        node.put("suitableFeedback", HtmlUtils.htmlEscape(suitableFeedback));
        node.put("comments",  HtmlUtils.htmlEscape(comments));
        return postWithRestResult(assessmentRestURL +"/saveAssessmentSummary/", node, Void.class);
    }

    @Override
    public RestResult<Void> submitAssessments(Long assessorId, Set<Long> assessmentIds ) {
        //builds the node with the response form fields data
        ObjectNode node =  new ObjectMapper().createObjectNode();
        node.put("assessorId", assessorId);
        ArrayNode assessmentsToSubmit = new ObjectMapper().valueToTree(assessmentIds);
        node.putArray("assessmentsToSubmit").addAll(assessmentsToSubmit);
        return postWithRestResult(assessmentRestURL +"/submitAssessments/", node, Void.class);
    }

    @Override
    public RestResult<Void> acceptAssessmentInvitation(Long processId, AssessmentResource assessment) {
        return postWithRestResult(assessmentRestURL + "/acceptAssessmentInvitation/" + processId, assessment, Void.class);
    }

    @Override
    public RestResult<Void> rejectAssessmentInvitation(Long processId, ProcessOutcomeResource processOutcome) {
        return postWithRestResult(assessmentRestURL + "/rejectAssessmentInvitation/" + processId, processOutcome, Void.class);
    }

    @Override
    public RestResult<Score> getScore(Long id) {
        return getWithRestResult(assessmentRestURL + "/" + id + "/score", Score.class);
    }
}
