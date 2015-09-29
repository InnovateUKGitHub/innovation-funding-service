package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.worth.ifs.assessment.domain.Assessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    AssessmentHandler assessmentHandler;


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

    @RequestMapping(value = "/respondToAssessmentInvitation", method = RequestMethod.POST)
    public Boolean respondToAssessmentInvitation(@RequestBody JsonNode formData) {

        //unpacks all the response form data fields
        Long assessorId = formData.get("assessorId").asLong();
        Long applicationId = formData.get("applicationId").asLong();
        Boolean decision = formData.get("decision").asBoolean();
        String reason = formData.get("reason").asText("");
        String observations = HtmlUtils.htmlUnescape(formData.get("observations").asText(""));

        // delegates to the handler and returns its operation success
        return assessmentHandler.respondToAssessmentInvitation(assessorId, applicationId, decision, reason, observations);
    }

    @RequestMapping(value = "/submitAssessments", method = RequestMethod.POST)
    public Boolean submitAssessments(@RequestBody JsonNode formData) {

        //unpacks all the response form data fields
        Long assessorId = formData.get("assessorId").asLong();
        ArrayNode assessmentsIds = (ArrayNode) formData.get("assessmentsToSubmit");

        // delegates to the handler and returns its operation success
        return assessmentHandler.submitAssessments(assessorId, fromArrayNodeToSet(assessmentsIds));
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

        System.out.println("AssessmentController > saveAssessmentSummary");

        //unpacks all the response form data fields
        Long assessorId = formData.get("assessorId").asLong();
        Long applicationId = formData.get("applicationId").asLong();

        String suitableValue = formData.get("suitableValue").asText();
        String suitableFeedback = formData.get("suitableFeedback").asText();
        String comments = formData.get("comments").textValue();

        // delegates to the handler and returns its operation success
        return assessmentHandler.saveAssessmentSummary(assessorId, applicationId, suitableValue, suitableFeedback, comments);
    }



}
