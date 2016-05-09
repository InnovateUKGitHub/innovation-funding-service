package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * AssessmentController is REST controller that handles requests and delegates their intent
 * to the AssessmentHandler and only that.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    private AssessorService assessorService;

    @RequestMapping("/findAssessmentsByCompetition/{assessorId}/{competitionId}")
    public RestResult<List<Assessment>> findAssessmentsByProcessRole(@PathVariable("assessorId") final Long assessorId, @PathVariable("competitionId") final Long competitionId) {
        return assessorService.getAllByCompetitionAndAssessor(competitionId, assessorId).toGetResponse();
    }

    @RequestMapping("/findAssessmentByProcessRole/{processRoleId}")
    public RestResult<Assessment> getAssessmentByProcessRole( @PathVariable("processRoleId") final Long processRoleId) {
        return assessorService.getOneByProcessRole(processRoleId).toGetResponse();
    }

    @RequestMapping("/totalAssignedAssessmentsByCompetition/{userId}/{competitionId}")
    public RestResult<Integer> getTotalAssignedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessorService.getTotalAssignedAssessmentsByCompetition(competitionId, userId).toGetResponse();
    }

    @RequestMapping("/totalSubmittedAssessmentsByCompetition/{userId}/{competitionId}")
    public RestResult<Integer> getTotalSubmittedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessorService.getTotalSubmittedAssessmentsByCompetition(competitionId, userId).toGetResponse();
    }

    @RequestMapping(value = "/acceptAssessmentInvitation/{processRoleId}")
    public RestResult<Void> acceptAssessmentInvitation(@PathVariable("processRoleId") final Long processRoleId,
                                           @RequestBody Assessment assessment) {
        return assessorService.acceptAssessmentInvitation(processRoleId, assessment).toPutResponse();
    }

    @RequestMapping(value = "/rejectAssessmentInvitation/{processRoleId}")
    public RestResult<Void> rejectAssessmentInvitation(@PathVariable("processRoleId") final Long processRoleId,
                                           @RequestBody ProcessOutcome processOutcome) {
        return assessorService.rejectAssessmentInvitation(processRoleId, processOutcome).toPutResponse();
    }

    @RequestMapping(value = "/submitAssessments", method = RequestMethod.POST)
    public RestResult<Void> submitAssessments(@RequestBody JsonNode formData) {

        //unpacks all the response form data fields
        ArrayNode assessmentsIds = (ArrayNode) formData.get("assessmentsToSubmit");
        Set<Long> assessments = fromArrayNodeToSet(assessmentsIds);
        return assessorService.submitAssessments(assessments).toPostResponse();
    }

    @RequestMapping(value = "/saveAssessmentSummary", method = RequestMethod.POST)
    public RestResult<Void> submitAssessment(@RequestBody JsonNode formData) {
        Long assessorId = formData.get("assessorId").asLong();
        Long applicationId = formData.get("applicationId").asLong();

        String suitableValue = formData.get("suitableValue").asText();
        String suitableFeedback =  HtmlUtils.htmlUnescape(formData.get("suitableFeedback").asText());
        String comments =  HtmlUtils.htmlUnescape(formData.get("comments").textValue());

        return assessorService.submitAssessment(assessorId, applicationId, suitableValue, suitableFeedback, comments).toPostResponse();
    }

    @RequestMapping(value = "{assessmentId}/score")
    public RestResult<Score> scoreForAssessment(@PathVariable("assessmentId") Long id){
        return assessorService.getScore(id).toGetResponse();
    }

    private Set<Long> fromArrayNodeToSet(ArrayNode array) {
        Set<Long> longsSet = new HashSet<>();
        Iterator<JsonNode> iterator = array.elements();

        while ( iterator.hasNext() ) {
            JsonNode aValue = iterator.next();
            longsSet.add(aValue.asLong());
        }

        return longsSet;
    }
}
