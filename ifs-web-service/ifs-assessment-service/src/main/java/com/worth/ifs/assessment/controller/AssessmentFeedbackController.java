package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.form.AssessmentFeedbackForm;
import com.worth.ifs.assessment.model.AssessmentFeedbackApplicationDetailsModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackNavigationModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;

@Controller
@RequestMapping("/{assessmentId}")
public class AssessmentFeedbackController extends AbstractApplicationController {

    private static String APPLICATION_DETAILS = "assessment-application-details";
    private static String QUESTION = "assessment-question";

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

    @Autowired
    private AssessmentFeedbackModelPopulator assessmentFeedbackModelPopulator;

    @Autowired
    private AssessmentFeedbackApplicationDetailsModelPopulator assessmentFeedbackApplicationDetailsModelPopulator;

    @Autowired
    private AssessmentFeedbackNavigationModelPopulator assessmentFeedbackNavigationModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.GET)
    public String getQuestion(final Model model,
                              final HttpServletResponse response,
                              @ModelAttribute(MODEL_ATTRIBUTE_FORM) final AssessmentFeedbackForm form,
                              final BindingResult bindingResult,
                              @PathVariable("assessmentId") final Long assessmentId,
                              @PathVariable("questionId") final Long questionId) throws InterruptedException, ExecutionException {
        if (isApplicationDetailsQuestion(questionId)) {
            return getApplicationDetails(model, assessmentId, questionId);
        }

        final AssessmentFeedbackResource assessmentFeedback = getAssessmentFeedbackForQuestion(assessmentId, questionId);
        form.setScore(assessmentFeedback.getScore());
        form.setValue(assessmentFeedback.getFeedback());

        final ApplicationResource application = getApplicationForAssessment(assessmentId);
        model.addAttribute("model", assessmentFeedbackModelPopulator.populateModel(application, questionId, assessmentFeedback));
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, questionId));

        return QUESTION;
    }

    @RequestMapping(value = "/question/{questionId}/feedback-value", method = RequestMethod.POST)
    public @ResponseBody JsonNode updateFeedbackValue(
            final Model model,
            final HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) final AssessmentFeedbackForm form,
            final BindingResult bindingResult,
            @PathVariable("assessmentId") final Long assessmentId,
            @PathVariable("questionId") final Long questionId) {
        // TODO validation
        final ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackValue(assessmentId, questionId, form.getValue());
        // TODO handle service errors
        return createJsonObjectNode(result.isSuccess(), asList());
    }

    @RequestMapping(value = "/question/{questionId}/feedback-score", method = RequestMethod.POST)
    public @ResponseBody JsonNode updateScore(
            final Model model,
            final HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) final AssessmentFeedbackForm form,
            final BindingResult bindingResult,
            @PathVariable("assessmentId") final Long assessmentId,
            @PathVariable("questionId") final Long questionId) {
        // TODO validation
        final ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackScore(assessmentId, questionId, form.getScore());
        // TODO handle service errors
        return createJsonObjectNode(result.isSuccess(), asList());
    }

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.POST)
    public String save(
            final Model model,
            final HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) final AssessmentFeedbackForm form,
            final BindingResult bindingResult,
            @PathVariable("assessmentId") final Long assessmentId,
            @PathVariable("questionId") final Long questionId) {
        // TODO validation
        final ServiceResult<Void> result = assessmentFeedbackService.updateAssessmentFeedback(assessmentId, questionId, form.getValue(), form.getScore());
        // TODO handle service errors
        return "redirect:/" + assessmentId;
    }

    private boolean isApplicationDetailsQuestion(final Long questionId) {
        final List<FormInputResource> questionFormInputs = getQuestionFormInputs(questionId);
        return questionFormInputs.stream().anyMatch(formInputResource -> "application_details".equals(formInputResource.getFormInputTypeTitle()));
    }

    private String getApplicationDetails(final Model model, final Long assessmentId, final Long questionId) throws InterruptedException, ExecutionException {
        final ApplicationResource application = getApplicationForAssessment(assessmentId);
        model.addAttribute("model", assessmentFeedbackApplicationDetailsModelPopulator.populateModel(application, questionId));
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, questionId));
        organisationDetailsModelPopulator.populateModel(model, application.getId());

        return APPLICATION_DETAILS;
    }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private ApplicationResource getApplicationForAssessment(final Long assessmentId) throws InterruptedException, ExecutionException {
        return getApplication(getApplicationIdForProcessRole(getProcessRoleForAssessment(getAssessment(assessmentId))));
    }

    private Future<ProcessRoleResource> getProcessRoleForAssessment(final AssessmentResource assessment) {
        return processRoleService.getById(assessment.getProcessRole());
    }

    private Long getApplicationIdForProcessRole(final Future<ProcessRoleResource> processRoleResource) throws InterruptedException, ExecutionException {
        return processRoleResource.get().getApplication();
    }

    private List<FormInputResource> getQuestionFormInputs(final Long questionId) {
        return formInputService.findByQuestion(questionId);
    }

    private AssessmentFeedbackResource getAssessmentFeedbackForQuestion(final Long assessmentId, final Long questionId) {
        return assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId);
    }

    private ObjectNode createJsonObjectNode(final boolean success, final List<String> errors) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");
        if (!success) {
            ArrayNode errorsNode = mapper.createArrayNode();
            errors.stream().forEach(errorsNode::add);
            node.set("validation_errors", errorsNode);
        }
        return node;
    }
}
