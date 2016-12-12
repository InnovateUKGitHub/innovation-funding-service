package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.ApplicationRejectionResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentSubmissionsResource;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.assessment.domain.Assessment} related data.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @RequestMapping(value= "/{id}", method = GET)
    public RestResult<AssessmentResource> findById(@PathVariable("id") Long id) {
        return assessmentService.findById(id).toGetResponse();
    }

    @RequestMapping(value= "/user/{userId}/competition/{competitionId}", method = GET)
    public RestResult<List<AssessmentResource>> findByUserAndCompetition(@PathVariable("userId") Long userId, @PathVariable("competitionId") Long competitionId ) {
        return assessmentService.findByUserAndCompetition(userId, competitionId).toGetResponse();
    }

    @RequestMapping(value = "/{id}/score", method = GET)
    public RestResult<AssessmentTotalScoreResource> getTotalScore(@PathVariable("id") Long id) {
        return assessmentService.getTotalScore(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}/recommend", method = PUT)
    public RestResult<Void> recommend(@PathVariable("id") Long id, @RequestBody @Valid AssessmentFundingDecisionResource assessmentFundingDecision) {
        return assessmentService.recommend(id, assessmentFundingDecision).toPutResponse();
    }

    @RequestMapping(value= "/{id}/rejectInvitation", method = PUT)
    public RestResult<Void> rejectInvitation(@PathVariable("id") Long id,@RequestBody @Valid ApplicationRejectionResource applicationRejection) {
        return assessmentService.rejectInvitation(id, applicationRejection).toPutResponse();
    }

    @RequestMapping(value= "/{id}/acceptInvitation", method = PUT)
    public RestResult<Void> acceptInvitation(@PathVariable("id") Long id) {
        return assessmentService.acceptInvitation(id).toPutResponse();
    }

    @RequestMapping(value = "/submitAssessments", method = PUT)
    public RestResult<Void> submitAssessments(@RequestBody @Valid AssessmentSubmissionsResource assessmentSubmissionsResource) {
        return assessmentService.submitAssessments(assessmentSubmissionsResource).toPutResponse();

    }
}
