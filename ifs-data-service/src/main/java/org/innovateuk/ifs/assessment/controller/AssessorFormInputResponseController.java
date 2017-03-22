package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} related data.
 */
@RestController
@RequestMapping("/assessorFormInputResponse")
public class AssessorFormInputResponseController {

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @GetMapping(value = "/assessment/{assessmentId}")
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(@PathVariable("assessmentId") Long assessmentId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId).toGetResponse();
    }

    @GetMapping(value = "/assessment/{assessmentId}/question/{questionId}")
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(@PathVariable("assessmentId") Long assessmentId, @PathVariable("questionId") Long questionId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).toGetResponse();
    }

    @PutMapping
    public RestResult<ValidationMessages> updateFormInputResponse(@Valid @RequestBody AssessorFormInputResponseResource response) {
        ServiceResult<ValidationMessages> messages = assessorFormInputResponseService.updateFormInputResponse(response)
                .andOnSuccessReturn(updatedResponse -> {
                    BindingResult result = validationUtil.validateResponse(
                            assessorFormInputResponseService.mapToFormInputResponse(updatedResponse),
                            true);

                    if (!result.hasErrors()) {
                        assessorFormInputResponseService.saveUpdatedFormInputResponse(updatedResponse);
                    }

                    return new ValidationMessages(result);
                });

        return messages.toPostWithBodyResponse();
    }

    @GetMapping(value = "/application/{applicationId}/scores")
    public RestResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(@PathVariable("applicationId") long applicationId) {
        return assessorFormInputResponseService.getApplicationAggregateScores(applicationId).toGetResponse();
    }

    @GetMapping(value = "/application/{applicationId}/question/{questionId}/feedback")
    public RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(@PathVariable("applicationId") long applicationId,
                                                                                          @PathVariable("questionId") long questionId) {
        return assessorFormInputResponseService.getAssessmentAggregateFeedback(applicationId, questionId).toGetResponse();
    }

}
