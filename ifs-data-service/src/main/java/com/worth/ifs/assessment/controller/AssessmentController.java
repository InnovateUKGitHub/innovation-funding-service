package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.controller.ProcessRoleController;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

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
        return newRestHandler().perform(() -> assessorService.getAllByCompetitionAndAssessor(competitionId, assessorId));
    }

    @RequestMapping("/findAssessmentByProcessRole/{processRoleId}")
    public RestResult<Assessment> getAssessmentByProcessRole( @PathVariable("processRoleId") final Long processRoleId) {
        return newRestHandler().perform(() -> assessorService.getOneByProcessRole(processRoleId));
    }

    @RequestMapping("/totalAssignedAssessmentsByCompetition/{userId}/{competitionId}")
    public RestResult<Integer> getTotalAssignedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return newRestHandler().perform(() -> assessorService.getTotalAssignedAssessmentsByCompetition(competitionId, userId));
    }

    @RequestMapping("/totalSubmittedAssessmentsByCompetition/{userId}/{competitionId}")
    public RestResult<Integer> getTotalSubmittedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return newRestHandler().perform(() -> assessorService.getTotalSubmittedAssessmentsByCompetition(competitionId, userId));
    }

    @RequestMapping(value = "/acceptAssessmentInvitation/{processRoleId}")
    public RestResult<Void> acceptAssessmentInvitation(@PathVariable("processRoleId") final Long processRoleId,
                                           @RequestBody Assessment assessment) {
        return newRestHandler().perform(() -> assessorService.acceptAssessmentInvitation(processRoleId, assessment));
    }

    @RequestMapping(value = "/rejectAssessmentInvitation/{processRoleId}")
    public RestResult<Void> rejectAssessmentInvitation(@PathVariable("processRoleId") final Long processRoleId,
                                           @RequestBody ProcessOutcome processOutcome) {
        return newRestHandler().perform(() -> assessorService.rejectAssessmentInvitation(processRoleId, processOutcome));
    }

    @RequestMapping(value = "/submitAssessments", method = RequestMethod.POST)
    public RestResult<Void> submitAssessments(@RequestBody JsonNode formData) {

        //unpacks all the response form data fields
        ArrayNode assessmentsIds = (ArrayNode) formData.get("assessmentsToSubmit");
        Set<Long> assessments = fromArrayNodeToSet(assessmentsIds);
        return newRestHandler().perform(() -> assessorService.submitAssessments(assessments));
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

    @RequestMapping(value = "/saveAssessmentSummary", method = RequestMethod.POST)
    public RestResult<Boolean> submitAssessment(@RequestBody JsonNode formData) {
        Long assessorId = formData.get("assessorId").asLong();
        Long applicationId = formData.get("applicationId").asLong();

        String suitableValue = formData.get("suitableValue").asText();
        String suitableFeedback =  HtmlUtils.htmlUnescape(formData.get("suitableFeedback").asText());
        String comments =  HtmlUtils.htmlUnescape(formData.get("comments").textValue());

        return newRestHandler().perform(() -> assessorService.submitAssessment(assessorId, applicationId, suitableValue, suitableFeedback, comments));
    }

    @RequestMapping(value = "{assessmentId}/score")
    public RestResult<Void> scoreForAssessment(@PathVariable("assessmentId") Long id){
        return newRestHandler().perform(() -> assessorService.getScore(id));
    }
}
