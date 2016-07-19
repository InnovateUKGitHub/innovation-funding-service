package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.model.AssessmentFeedbackApplicationDetailsModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackNavigationModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Arrays.asList;

@Controller
@RequestMapping("/{assessmentId}")
public class AssessmentFeedbackController extends AbstractApplicationController {

    private static String APPLICATION_DETAILS = "assessment-application-details";
    private static String QUESTION = "assessment-question";

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentFeedbackModelPopulator assessmentFeedbackModelPopulator;

    @Autowired
    private AssessmentFeedbackApplicationDetailsModelPopulator assessmentFeedbackApplicationDetailsModelPopulator;

    @Autowired
    private AssessmentFeedbackNavigationModelPopulator assessmentFeedbackNavigationModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.GET)
    public String getQuestion(Model model,
                              HttpServletResponse response,
                              @ModelAttribute(MODEL_ATTRIBUTE_FORM) Form form,
                              BindingResult bindingResult,
                              @PathVariable("assessmentId") Long assessmentId,
                              @PathVariable("questionId") Long questionId) throws InterruptedException, ExecutionException {
        if (isApplicationDetailsQuestion(questionId)) {
            return getApplicationDetails(model, assessmentId, questionId);
        }

        ApplicationResource application = getApplicationForAssessment(assessmentId);
        AssessmentFeedbackViewModel viewModel = assessmentFeedbackModelPopulator.populateModel(assessmentId, questionId, application);

        Map<Long, AssessorFormInputResponseResource> mappedResponses = viewModel.getAssessorResponses();
        mappedResponses.forEach((k, v) ->
                form.addFormInput(k.toString(), v.getValue())
        );

        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, questionId));

        return QUESTION;
    }

    @RequestMapping(value = "/question/{questionId}/feedback-value", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonNode updateFeedbackValue(
            Model model,
            HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) Form form,
            BindingResult bindingResult,
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("questionId") Long questionId) {
        // TODO validation
        //ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackValue(assessmentId, questionId, form.getValue());
        ServiceResult<Void> result = serviceSuccess();
        // TODO handle service errors
        return createJsonObjectNode(result.isSuccess(), asList());
    }

    @RequestMapping(value = "/question/{questionId}/feedback-score", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonNode updateScore(
            Model model,
            HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) Form form,
            BindingResult bindingResult,
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("questionId") Long questionId) {
        // TODO validation
        //ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackScore(assessmentId, questionId, form.getScore());
        ServiceResult<Void> result = serviceSuccess();
        // TODO handle service errors
        return createJsonObjectNode(result.isSuccess(), asList());
    }

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.POST)
    public String save(
            Model model,
            HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) Form form,
            BindingResult bindingResult,
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("questionId") Long questionId) {
        // TODO validation
        //ServiceResult<Void> result = assessmentFeedbackService.updateAssessmentFeedback(assessmentId, questionId, form.getValue(), form.getScore());
        // TODO handle service errors
        return "redirect:/" + assessmentId;
    }

    private boolean isApplicationDetailsQuestion(Long questionId) {
        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(questionId);
        return applicationFormInputs.stream().anyMatch(formInputResource -> "application_details".equals(formInputResource.getFormInputTypeTitle()));
    }

    private String getApplicationDetails(Model model, Long assessmentId, Long questionId) throws InterruptedException, ExecutionException {
        ApplicationResource application = getApplicationForAssessment(assessmentId);
        model.addAttribute("model", assessmentFeedbackApplicationDetailsModelPopulator.populateModel(application, questionId));
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, questionId));
        organisationDetailsModelPopulator.populateModel(model, application.getId());

        return APPLICATION_DETAILS;
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private ApplicationResource getApplicationForAssessment(Long assessmentId) throws InterruptedException, ExecutionException {
        return getApplication(getApplicationIdForProcessRole(getProcessRoleForAssessment(getAssessment(assessmentId))));
    }

    private Future<ProcessRoleResource> getProcessRoleForAssessment(AssessmentResource assessment) {
        return processRoleService.getById(assessment.getProcessRole());
    }

    private Long getApplicationIdForProcessRole(Future<ProcessRoleResource> processRoleResource) throws InterruptedException, ExecutionException {
        return processRoleResource.get().getApplication();
    }

    private List<FormInputResource> getApplicationFormInputs(Long questionId) {
        return formInputService.findApplicationInputsByQuestion(questionId);
    }

    private ObjectNode createJsonObjectNode(boolean success, List<String> errors) {
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
