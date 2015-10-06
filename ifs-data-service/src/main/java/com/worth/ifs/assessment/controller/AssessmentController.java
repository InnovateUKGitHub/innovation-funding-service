package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * AssessmentController is http requests controller that maps those requests and delegates their intent
 * to the AssessmentHandler and only that.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    AssessmentHandler assessmentHandler;

    @Autowired
    AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;


    @RequestMapping("/findAssessmentsByCompetition/{userId}/{competitionId}")
    public List<Assessment> findAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessmentHandler.getAllByCompetitionAndAssessor(competitionId, userId);
    }

    @RequestMapping("/findAssessmentByApplication/{userId}/{applicationId}")
    public Assessment getAssessmentByUserAndApplication( @PathVariable("userId") final Long userId, @PathVariable("applicationId") final Long applicationId ) {
        return assessmentHandler.getOneByAssessorAndApplication(userId, applicationId);
    }

    @RequestMapping("/totalAssignedAssessmentsByCompetition/{userId}/{competitionId}")
    public Integer getTotalAssignedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
       return assessmentHandler.getTotalAssignedAssessmentsByCompetition(competitionId, userId);
    }

    @RequestMapping("/totalSubmittedAssessmentsByCompetition/{userId}/{competitionId}")
    public Integer getTotalSubmittedAssessmentsByCompetition( @PathVariable("userId") final Long userId, @PathVariable("competitionId") final Long competitionId ) {
        return assessmentHandler.getTotalSubmittedAssessmentsByCompetition(competitionId, userId);
    }

    @RequestMapping(value = "/acceptAssessmentInvitation/{applicationId}/{assessorId}")
    public void acceptAssessmentInvitation(@PathVariable("applicationId") final Long applicationId,
                                           @PathVariable("assessorId") final Long assessorId,
                                           @RequestBody Assessment assessment) {
        Assessment assessmentOriginal = assessmentHandler.getOneByAssessorAndApplication(assessorId, applicationId);
        assessment.setProcessStatus(assessmentOriginal.getProcessStatus());
        assessmentWorkflowEventHandler.acceptInvitation(applicationId, assessorId, assessment);
    }

    @RequestMapping(value = "/rejectAssessmentInvitation/{applicationId}/{assessorId}")
    public void rejectAssessmentInvitation(@PathVariable("applicationId") final Long applicationId,
                                           @PathVariable("assessorId") final Long assessorId,
                                           @RequestBody Assessment assessment) {
        Assessment assessmentOriginal = assessmentHandler.getOneByAssessorAndApplication(assessorId, applicationId);
        String currentProcessStatus = assessmentOriginal.getProcessStatus();
        assessment.setProcessStatus(currentProcessStatus);
        assessmentWorkflowEventHandler.rejectInvitation(applicationId, assessorId, assessment);
    }

    @RequestMapping(value = "/submitAssessments", method = RequestMethod.POST)
    public Boolean submitAssessments(@RequestBody JsonNode formData) {

        //unpacks all the response form data fields
        Long assessorId = formData.get("assessorId").asLong();
        ArrayNode assessmentsIds = (ArrayNode) formData.get("assessmentsToSubmit");
        Set<Long> assessments = fromArrayNodeToSet(assessmentsIds);
        for(Long assessmentId : assessments) {
            Assessment assessment = assessmentHandler.getOne(assessmentId);
            assessmentWorkflowEventHandler.submit(assessment);
        }
        return new Boolean(true);
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
    public Boolean submitAssessment(@RequestBody JsonNode formData) {
        Long assessorId = formData.get("assessorId").asLong();
        Long applicationId = formData.get("applicationId").asLong();

        String suitableValue = formData.get("suitableValue").asText();
        String suitableFeedback =  HtmlUtils.htmlUnescape(formData.get("suitableFeedback").asText());
        String comments =  HtmlUtils.htmlUnescape(formData.get("comments").textValue());

        // delegates to the handler and returns its operation success
        Assessment assessment = assessmentHandler.getOneByAssessorAndApplication(assessorId, applicationId);
        Assessment newAssessment = new Assessment();
        newAssessment.setSummary(assessmentHandler.getRecommendedValueFromString(suitableValue), suitableFeedback, comments);
        newAssessment.setProcessStatus(assessment.getProcessStatus());

        assessmentWorkflowEventHandler.recommend(applicationId, assessorId, newAssessment);
        return true;
    }



}
