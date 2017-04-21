package org.innovateuk.ifs.assessment.feedback.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.assessment.common.service.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackApplicationDetailsModelPopulator;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackModelPopulator;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackNavigationModelPopulator;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentNavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.toField;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.innovateuk.ifs.util.MapFunctions.toListOfPairs;

@Controller
@RequestMapping("/{assessmentId}")
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentFeedbackController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private QuestionService questionService;

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

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/question/{questionId}")
    public String getQuestion(Model model,
                              @ModelAttribute(FORM_ATTR_NAME) Form form,
                              @PathVariable("assessmentId") Long assessmentId,
                              @PathVariable("questionId") Long questionId) {

        QuestionResource question = getQuestionForAssessment(questionId, assessmentId);

        if (isApplicationDetailsQuestion(questionId)) {
            return getApplicationDetails(model, assessmentId, question);
        }

        populateQuestionForm(form, assessmentId, questionId);
        return doViewQuestion(model, assessmentId, question);
    }

    @PostMapping("/formInput/{formInputId}")
    public @ResponseBody JsonNode updateFormInputResponse(
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("formInputId") Long formInputId,
            @RequestParam("value") String value) {
        try {
          ServiceResult<Void> result = assessorFormInputResponseService.updateFormInputResponse(assessmentId, formInputId, value);
          return createJsonObjectNode(true);
        } catch (Exception e) {
            return createJsonObjectNode(false);
        }
    }

    @PostMapping("/question/{questionId}")
    public String save(
            Model model,
            @ModelAttribute(FORM_ATTR_NAME) Form form,
            @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable("assessmentId") Long assessmentId,
            @PathVariable("questionId") Long questionId) {

        Supplier<String> failureView = () -> doViewQuestion(model, assessmentId, getQuestionForAssessment(questionId, assessmentId));

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            List<FormInputResource> formInputs = formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT).getSuccessObjectOrThrowException();
            List<Pair<Long, String>> formInputResponses = getFormInputResponses(form, formInputs);
            formInputResponses.stream().forEach(responsePair -> {
                // TODO INFUND-4105 optimise this to save multiple responses at a time
                String formInputField = format("formInput[%s]", responsePair.getLeft());
                ServiceResult<Void> updateResult = assessorFormInputResponseService.updateFormInputResponse(assessmentId, responsePair.getLeft(), responsePair.getRight());
                validationHandler.addAnyErrors(updateResult, toField(formInputField));
            });

            return validationHandler.
                    failNowOrSucceedWith(failureView, () -> redirectToAssessmentOverview(assessmentId));
        });
    }

    private QuestionResource getQuestionForAssessment(Long questionId, Long assessmentId) {
        return questionService.getByIdAndAssessmentId(questionId, assessmentId);
    }

    private List<AssessorFormInputResponseResource> getAssessorResponses(Long assessmentId, Long questionId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
    }

    private Form populateQuestionForm(Form form, Long assessmentId, Long questionId) {
        List<AssessorFormInputResponseResource> assessorResponses = getAssessorResponses(assessmentId, questionId);
        Map<Long, AssessorFormInputResponseResource> mappedResponses = simpleToMap(assessorResponses, AssessorFormInputResponseResource::getFormInput);
        mappedResponses.forEach((k, v) -> form.addFormInput(k.toString(), v.getValue()));
        return form;
    }

    private String doViewQuestion(Model model, Long assessmentId, QuestionResource question) {
        AssessmentFeedbackViewModel viewModel = assessmentFeedbackModelPopulator.populateModel(assessmentId, question);
        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, question.getId()));
        return "assessment/application-question";
    }

    private String redirectToAssessmentOverview(Long assessmentId) {
        return "redirect:/" + assessmentId;
    }

    private boolean isApplicationDetailsQuestion(Long questionId) {
        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(questionId);
        return applicationFormInputs.stream().anyMatch(formInputResource -> FormInputType.APPLICATION_DETAILS == formInputResource.getType());
    }

    private String getApplicationDetails(Model model, Long assessmentId, QuestionResource question) {
        AssessmentFeedbackApplicationDetailsViewModel viewModel = assessmentFeedbackApplicationDetailsModelPopulator.populateModel(assessmentId, question);
        AssessmentNavigationViewModel navigationViewModel = assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, question.getId());
        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", navigationViewModel);
        organisationDetailsModelPopulator.populateModel(model, viewModel.getApplicationId());

        return "assessment/application-details";
    }

    private List<FormInputResource> getApplicationFormInputs(Long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION).getSuccessObjectOrThrowException();
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

    private ObjectNode createJsonObjectNode(boolean success) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        return node;
    }
}
