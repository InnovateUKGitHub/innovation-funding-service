package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.assessment.form.AssessmentFeedbackForm;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentFeedbackViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentNavigationViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.rest.RestResult.aggregate;
import static com.worth.ifs.util.CollectionFunctions.flattenLists;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;

@Controller
public class AssessmentFeedbackController extends AbstractApplicationController {

    private static String QUESTION_FORM = "assessment-question";

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

    @RequestMapping(value = "/{assessmentId}/question/{questionId}", method = RequestMethod.GET)
    public String getQuestion(final Model model,
                              final HttpServletResponse response,
                              @ModelAttribute(MODEL_ATTRIBUTE_FORM) final AssessmentFeedbackForm form,
                              final BindingResult bindingResult,
                              @PathVariable("assessmentId") final Long assessmentId,
                              @PathVariable("questionId") final Long questionId) throws InterruptedException, ExecutionException {

        final ApplicationResource application = getApplicationForAssessment(assessmentId);

        model.addAttribute("model", populateModel(application, questionId));
        model.addAttribute("navigation", populateNavigation(assessmentId, questionId));

        return QUESTION_FORM;
    }

    private AssessmentFeedbackViewModel populateModel(final ApplicationResource application, final Long questionId) {
        final CompetitionResource competition = getCompetition(application.getCompetition());
        final QuestionResource question = getQuestion(questionId);
        final List<FormInputResource> questionFormInputs = getQuestionFormInputs(questionId);
        final Map<String, String> questionFormInputResponses = getQuestionFormInputResponsesAsMap(getQuestionFormInputResponses(application.getId(), questionFormInputs));
        return new AssessmentFeedbackViewModel(competition, question, questionFormInputs, questionFormInputResponses);
    }

    private AssessmentNavigationViewModel populateNavigation(final Long assessmentId, final Long questionId) {
        return new AssessmentNavigationViewModel(assessmentId, getPreviousQuestion(questionId), getNextQuestion(questionId));
    }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private CompetitionResource getCompetition(final Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private QuestionResource getQuestion(final Long questionId) {
        return questionService.getById(questionId);
    }

    private List<FormInputResource> getQuestionFormInputs(final Long questionId) {
        return formInputService.findByQuestion(questionId);
    }

    private List<FormInputResponseResource> getQuestionFormInputResponses(final Long applicationId, List<FormInputResource> formInputs) {
        final RestResult<List<List<FormInputResponseResource>>> questionFormInputResponses = aggregate(formInputs
                .stream()
                .map(formInput -> formInputResponseService.getByFormInputIdAndApplication(applicationId, formInput.getId()))
                .collect(Collectors.toList()));
        return flattenLists(questionFormInputResponses.getSuccessObjectOrThrowException());
    }

    private Map<String, String> getQuestionFormInputResponsesAsMap(final List<FormInputResponseResource> formInputResponses) {
        return simpleToMap(
                formInputResponses,
                response -> String.valueOf(response.getFormInput()),
                response -> response.getValue()
        );
    }

    private ApplicationResource getApplicationForAssessment(final Long assessmentId) throws InterruptedException, ExecutionException {
        return getApplication(getApplicationIdForProcessRole(getProcessRoleForAssessment(getAssessment(assessmentId))));
    }

    private Future<ProcessRoleResource> getProcessRoleForAssessment(final AssessmentResource assessment) {
        return processRoleService.getById(assessment.getId());
    }

    private Long getApplicationIdForProcessRole(final Future<ProcessRoleResource> processRoleResource) throws InterruptedException, ExecutionException {
        return processRoleResource.get().getApplication();
    }

    private Optional<QuestionResource> getPreviousQuestion(final Long questionId) {
        return questionService.getPreviousQuestion(questionId);
    }

    private Optional<QuestionResource> getNextQuestion(final Long questionId) {
        return questionService.getPreviousQuestion(questionId);
    }
}
