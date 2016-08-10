package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.transactional.AssessorFormInputResponseService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} related data.
 */
@RestController
@RequestMapping("/assessorFormInputResponse")
public class AssessorFormInputResponseController {

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @RequestMapping(value = "/assessment/{assessmentId}", method = RequestMethod.GET)
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(@PathVariable("assessmentId") Long assessmentId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId).toGetResponse();
    }

    @RequestMapping(value = "/assessment/{assessmentId}/question/{questionId}", method = RequestMethod.GET)
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(@PathVariable("assessmentId") Long assessmentId, @PathVariable("questionId") Long questionId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).toGetResponse();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public RestResult<Void> updateFormInputResponse(@Valid @RequestBody AssessorFormInputResponseResource response) {
        return assessorFormInputResponseService.updateFormInputResponse(response).toPutResponse();
    }
}
