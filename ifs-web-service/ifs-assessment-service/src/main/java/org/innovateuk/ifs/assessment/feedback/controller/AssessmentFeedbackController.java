package org.innovateuk.ifs.assessment.feedback.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.assessment.dashboard.controller.AssessorDashboardController;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackApplicationDetailsModelPopulator;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackModelPopulator;
import org.innovateuk.ifs.assessment.feedback.populator.AssessmentFeedbackNavigationModelPopulator;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackApplicationDetailsViewModel;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackNavigationViewModel;
import org.innovateuk.ifs.assessment.feedback.viewmodel.AssessmentFeedbackViewModel;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.mappingFieldErrorToField;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

@Controller
@RequestMapping("/{assessmentId}")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = AssessmentFeedbackController.class)
@PreAuthorize("hasAuthority('assessor')")
public class AssessmentFeedbackController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private AssessmentFeedbackModelPopulator assessmentFeedbackModelPopulator;

    @Autowired
    private AssessmentFeedbackApplicationDetailsModelPopulator assessmentFeedbackApplicationDetailsModelPopulator;

    @Autowired
    private AssessmentFeedbackNavigationModelPopulator assessmentFeedbackNavigationModelPopulator;

    @Autowired
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Autowired
    private ProcessRoleService processRoleService;

    @GetMapping("/question/{questionId}")
    public String getQuestion(Model model,
                              @ModelAttribute(name = FORM_ATTR_NAME, binding = false) Form form,
                              @PathVariable("assessmentId") long assessmentId,
                              @PathVariable("questionId") long questionId) {

        QuestionResource question = getQuestionForAssessment(questionId, assessmentId);

        if (isApplicationDetailsQuestion(questionId)) {
            return getApplicationDetails(model, assessmentId, question);
        }

        populateQuestionForm(form, assessmentId, questionId);
        return doViewQuestion(model, assessmentId, question);
    }

    @PostMapping("/formInput/{formInputId}")
    public
    @ResponseBody
    JsonNode updateFormInputResponse(
            @PathVariable("assessmentId") long assessmentId,
            @PathVariable("formInputId") long formInputId,
            @RequestParam("value") String value) {
        try {
            assessorFormInputResponseRestService.updateFormInputResponse(assessmentId, formInputId, value)
                    .getSuccessObjectOrThrowException();
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
            @PathVariable("assessmentId") long assessmentId,
            @PathVariable("questionId") long questionId) {

        Supplier<String> failureView = () -> doViewQuestion(model, assessmentId, getQuestionForAssessment(questionId, assessmentId));

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            List<FormInputResource> formInputs = getAssessmentFormInputsForQuestion(questionId);
            AssessorFormInputResponsesResource responses = getFormInputResponses(form, formInputs, assessmentId);
            RestResult<Void> updateResult = assessorFormInputResponseRestService.updateFormInputResponses(responses);

            return validationHandler.addAnyErrors(updateResult, mappingFieldErrorToField(e -> {
                Matcher matcher = Pattern.compile("responses\\[(\\d)\\]\\.value").matcher(e.getFieldName());

                if (matcher.find()) {
                    int errorIndex = Integer.parseInt(matcher.group(1));
                    Long formInputResponseWithError = responses.getResponses().get(errorIndex).getFormInput();

                    return format("formInput[%s]", formInputResponseWithError);
                } else if (e.isFieldError() && !e.getArguments().isEmpty()) {
                    return format("formInput[%s]", e.getArguments().get(0));
                }

                return e.getFieldName();
            }), asGlobalErrors()).failNowOrSucceedWith(failureView, () -> redirectToAssessmentOverview(assessmentId));
        });
    }

    private QuestionResource getQuestionForAssessment(long questionId, long assessmentId) {
        return questionService.getByIdAndAssessmentId(questionId, assessmentId);
    }

    private List<AssessorFormInputResponseResource> getAssessorResponses(long assessmentId, long questionId) {
        return assessorFormInputResponseRestService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(
                assessmentId, questionId).getSuccessObjectOrThrowException();
    }

    private Form populateQuestionForm(Form form, long assessmentId, long questionId) {
        List<AssessorFormInputResponseResource> assessorResponses = getAssessorResponses(assessmentId, questionId);
        Map<Long, AssessorFormInputResponseResource> mappedResponses = simpleToMap(assessorResponses, AssessorFormInputResponseResource::getFormInput);
        mappedResponses.forEach((k, v) -> form.addFormInput(k.toString(), v.getValue()));
        processScopeInput(form, mappedResponses, questionId);

        return form;
    }

    private void processScopeInput(Form form, Map<Long, AssessorFormInputResponseResource> mappedResponses, long questionId) {
        Optional<FormInputResource> scopeInput = getScopeFormInput(getAssessmentFormInputsForQuestion(questionId));

        scopeInput.ifPresent(scope -> {
            if (!mappedResponses.containsKey(scope.getId())) {
                form.addFormInput(scope.getId().toString(), "none");
            }
        });
    }

    private List<FormInputResource> getAssessmentFormInputsForQuestion(long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, ASSESSMENT)
                .getSuccessObjectOrThrowException();
    }

    private String doViewQuestion(Model model, long assessmentId, QuestionResource question) {
        AssessmentFeedbackViewModel viewModel = assessmentFeedbackModelPopulator.populateModel(assessmentId, question);
        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, question));
        return "assessment/application-question";
    }

    private String redirectToAssessmentOverview(long assessmentId) {
        return "redirect:/" + assessmentId;
    }

    private boolean isApplicationDetailsQuestion(long questionId) {
        List<FormInputResource> applicationFormInputs = getApplicationFormInputs(questionId);
        return applicationFormInputs.stream().anyMatch(formInputResource -> FormInputType.APPLICATION_DETAILS == formInputResource.getType());
    }

    private String getApplicationDetails(Model model, long assessmentId, QuestionResource question) {
        AssessmentFeedbackApplicationDetailsViewModel viewModel = assessmentFeedbackApplicationDetailsModelPopulator.populateModel(assessmentId, question);
        AssessmentFeedbackNavigationViewModel navigationViewModel = assessmentFeedbackNavigationModelPopulator.populateModel(assessmentId, question);
        model.addAttribute("model", viewModel);
        model.addAttribute("navigation", navigationViewModel);

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(viewModel.getApplicationId());
        organisationDetailsModelPopulator.populateModel(model, viewModel.getApplicationId(), userApplicationRoles);

        return "assessment/application-details";
    }

    private List<FormInputResource> getApplicationFormInputs(long questionId) {
        return formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION).getSuccessObjectOrThrowException();
    }

    private Optional<FormInputResource> getScopeFormInput(List<FormInputResource> formInputs) {
        return formInputs.stream()
                .filter(input -> input.getType().equals(ASSESSOR_APPLICATION_IN_SCOPE))
                .findAny();
    }

    private AssessorFormInputResponsesResource getFormInputResponses(Form form, List<FormInputResource> formInputs, long assessmentId) {
        Set<Long> formInputResourceIds = newHashSet(simpleMap(formInputs, FormInputResource::getId));

        Map<Long, String> responseStrings = simpleMapEntry(form.getFormInput(), formInput -> Long.valueOf(formInput.getKey()), Map.Entry::getValue);

        // Filter the responses to include only those for which a form input exist
        Map<Long, String> filtered = simpleFilter(responseStrings, (id, value) -> formInputResourceIds.contains(id));

        List<AssessorFormInputResponseResource> assessorFormInputResponses = simpleMap(
                filtered,
                (id, value) -> new AssessorFormInputResponseResource(assessmentId, id, value)
        );

        return new AssessorFormInputResponsesResource(assessorFormInputResponses);
    }

    private ObjectNode createJsonObjectNode(boolean success) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        return node;
    }
}
