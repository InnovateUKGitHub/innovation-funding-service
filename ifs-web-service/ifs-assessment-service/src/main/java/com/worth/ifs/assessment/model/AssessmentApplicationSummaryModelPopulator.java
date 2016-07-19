package com.worth.ifs.assessment.model;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentFeedbackService;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessmentApplicationSummaryQuestionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessmentApplicationSummaryViewModel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Build the model for the Assessment Application Summary view.
 */
@Component
public class AssessmentApplicationSummaryModelPopulator {

    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    public AssessmentApplicationSummaryViewModel populateModel(Long assessmentId) throws ExecutionException, InterruptedException {
        ApplicationResource application = getApplicationForAssessment(assessmentId);
        CompetitionResource competition = getCompetition(application.getCompetition());
        List<AssessmentApplicationSummaryQuestionViewModel> questions = getSummaryQuestions(assessmentId);

        // TODO calculate the scores
        Integer totalScoreGiven = 21;
        Integer totalScorePossible = 100;
        Integer totalScorePercentage = 21;

        return new AssessmentApplicationSummaryViewModel(assessmentId, competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage(), competition, application, questions, totalScoreGiven, totalScorePossible, totalScorePercentage);
    }

    private CompetitionResource getCompetition(Long competitionId) {
        return competitionService.getById(competitionId);
    }

    private ApplicationResource getApplicationForAssessment(Long assessmentId) throws InterruptedException, ExecutionException {
        return getApplication(getApplicationIdForProcessRole(getProcessRoleForAssessment(getAssessment(assessmentId))));
    }

    private AssessmentResource getAssessment(Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private Future<ProcessRoleResource> getProcessRoleForAssessment(AssessmentResource assessment) {
        return processRoleService.getById(assessment.getProcessRole());
    }

    private Long getApplicationIdForProcessRole(Future<ProcessRoleResource> processRoleResource) throws InterruptedException, ExecutionException {
        return processRoleResource.get().getApplication();
    }

    private List<AssessmentApplicationSummaryQuestionViewModel> getSummaryQuestions(Long assessmentId) throws ExecutionException, InterruptedException {
        Map<Long, AssessmentFeedbackResource> allAssessmentFeedback = getAssessmentFeedbackForAssessment(assessmentId);
        return simpleMap(getQuestionsForAssessment(assessmentId), question -> {
            String displayLabel = question.getShortName();
            String displayLabelShort = getSummaryQuestionDisplayLabelShort(question);
            boolean requireScore = true;
            Integer scorePossible = requireScore ? getScorePossible(question) : null;
            AssessmentFeedbackResource assessmentFeedback = allAssessmentFeedback.get(question.getId());
            Map<String, String> values = getSummaryQuestionValues(assessmentFeedback);
            boolean complete = isComplete(assessmentFeedback);
            return new AssessmentApplicationSummaryQuestionViewModel(question.getId(), displayLabel, displayLabelShort, requireScore, scorePossible, values, complete);
        });
    }

    private Map<Long, AssessmentFeedbackResource> getAssessmentFeedbackForAssessment(Long assessmentId) {
        return simpleToMap(assessmentFeedbackService.getAllAssessmentFeedback(assessmentId), AssessmentFeedbackResource::getQuestion);
    }

    private List<QuestionResource> getQuestionsForAssessment(Long assessmentId) throws ExecutionException, InterruptedException {
        // TODO can 2L be relied on?
        return assessmentService.getAllQuestionsById(assessmentId).stream().filter(questionResource -> Long.valueOf(2L).equals(questionResource.getSection())).sorted(comparing(QuestionResource::getPriority)).collect(toList());
    }

    private String getSummaryQuestionDisplayLabelShort(QuestionResource question) {
        return format("Q%s", question.getQuestionNumber());
    }

    private boolean isComplete(AssessmentFeedbackResource assessmentFeedback) {
        // TODO determine if the assessment of this question is complete
        return false;
    }

    private Map<String, String> getSummaryQuestionValues(AssessmentFeedbackResource assessmentFeedback) {
        // TODO map each of the assessors answers
        return asMap(
                "SCORE", "3",
                "FEEDBACK", "Blah",
                "SCOPE", "Yes");
    }

    private Integer getScorePossible(QuestionResource question) {
        // TODO maximum possible score will be defined during Competition Setup
        return 10;
    }
}
