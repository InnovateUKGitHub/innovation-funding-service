package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.assessment.model.AssessmentFeedbackApplicationDetailsModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackModelPopulator;
import com.worth.ifs.assessment.model.AssessmentFeedbackNavigationModelPopulator;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.service.AssessorFormInputResponseService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentNavigationViewModel;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_FORM_INPUT_RESPONSE_WORD_LIMIT_EXCEEDED;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.mappingErrorKeyToField;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static com.worth.ifs.util.MapFunctions.toListOfPairs;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/{assessmentId}")
public class AssessmentFeedbackController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

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
                              @PathVariable("assessmentId") Long assessmentId,
                              @PathVariable("questionId") Long questionId) {

        if (isApplicationDetailsQuestion(questionId)) {
            return getApplicationDetails(model, assessmentId, questionId);
        }

        Form form = populateQuestionForm(assessmentId, questionId);
        return doViewQuestion(model, form, assessmentId, questionId);
    }

    @RequestMapping(value = "/formInput/{formInputId}", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonNode updateFormInputResponse(
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("formInputId") Long formInputId,
            @RequestParam("value") String value) {

        ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessmentId, formInputId, value);
        return createJsonObjectNode(result.isSuccess(), result.getErrors());
    }

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.POST)
    public String save(
            Model model,
            @ModelAttribute(FORM_ATTR_NAME) Form form,
            BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("questionId") Long questionId) {

        Supplier<String> failureView = () -> doViewQuestion(model, form, assessmentId, questionId);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            List<FormInputResource> formInputs = formInputService.findAssessmentInputsByQuestion(questionId);
            List<Pair<Long, String>> formInputResponses = getFormInputResponses(form, formInputs);
            formInputResponses.stream().forEach(responsePair -> {
                // TODO INFUND-4105 optimise this to save multiple responses at a time
                String formInputField = format("formInput[%s]", responsePair.getLeft());
                ServiceResult<Void> updateResult = assessorFormInputResponseService.updateFormInputResponse(assessmentId, responsePair.getLeft(), responsePair.getRight());
                validationHandler.addAnyErrors(updateResult, toField(formInputField), mappingErrorKeyToField(ASSESSMENT_FORM_INPUT_RESPONSE_WORD_LIMIT_EXCEEDED, formInputField));
            });

            return validationHandler.
                    failNowOrSucceedWith(failureView, () -> redirectToAssessmentOverview(assessmentId));
        });
    }

    private List<AssessorFormInputResponseResource> getAssessorResponses(Long assessmentId, Long questionId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
    }

    private Form populateQuestionForm(Long assessmentId, Long questionId) {
        Form form = new Form();
        List<AssessorFormInputResponseResource> assessorResponses = getAssessorResponses(assessmentId, questionId);
        Map<Long, AssessorFormInputResponseResource> mappedResponses = simpleToMap(assessorResponses, AssessorFormInputResponseResource::getFormInput);
        mappedResponses.forEach((k, v) -> form.addFormInput(k.toString(), v.getValue()));
        return form;
    }

    private String doViewQuestion(Model model, Form form, Long assessmentId, Long questionId) {
        AssessmentFeedbackViewModel viewModel = assessmentFeedbackModelPopulator.populateModel(assessmentId, questionId);
        model.addAttribute("form", form);
        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, questionId));
        return "assessment/application-question";
    }

    private String redirectToAssessmentOverview(Long assessmentId) {
        return "redirect:/" + assessmentId;
    }

    private boolean isApplicationDetailsQuestion(Long questionId) {
        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(questionId);
        return applicationFormInputs.stream().anyMatch(formInputResource -> "application_details".equals(formInputResource.getFormInputTypeTitle()));
    }

    private String getApplicationDetails(Model model, Long assessmentId, Long questionId) {
        AssessmentFeedbackApplicationDetailsViewModel viewModel = assessmentFeedbackApplicationDetailsModelPopulator.populateModel(assessmentId, questionId);
        AssessmentNavigationViewModel navigationViewModel = assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, questionId);
        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", navigationViewModel);
        organisationDetailsModelPopulator.populateModel(model, viewModel.getApplication().getId());

        return "assessment/application-details";
    }

    private List<FormInputResource> getApplicationFormInputs(Long questionId) {
        return formInputService.findApplicationInputsByQuestion(questionId);
    }

    private List<Pair<Long, String>> getFormInputResponses(Form form, List<FormInputResource> formInputs) {
        // Convert the Form map to be keyed by Long rather than String
        List<Pair<Long, String>> responses = toListOfPairs(form.getFormInput().entrySet()
                .stream()
                .collect(toMap(keyEntry -> Long.valueOf(keyEntry.getKey()), Map.Entry::getValue)));
        // Filter the responses to include only those for which a form input exist
        Map<Long, FormInputResource> formInputResourceMap = simpleToMap(formInputs, FormInputResource::getId);
        return simpleFilter(responses, responsePair -> formInputResourceMap.containsKey(responsePair.getLeft()));
    }

    private ObjectNode createJsonObjectNode(boolean success, List<Error> errors) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");
        if (!success) {
            ArrayNode errorsNode = mapper.createArrayNode();
            errors.stream().map(u -> u.getErrorMessage()).collect(Collectors.toList()).forEach(errorsNode::add);
            node.set("validation_errors", errorsNode);
        }
        return node;
    }
}
