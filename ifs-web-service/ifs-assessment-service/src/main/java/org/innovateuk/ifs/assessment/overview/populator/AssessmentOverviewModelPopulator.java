package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentOverviewAppendixViewModel;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentOverviewQuestionViewModel;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentOverviewSectionViewModel;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentOverviewViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ROUND_UP;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Build the model for Assessment Overview view.
 */
@Component
public class AssessmentOverviewModelPopulator {

    public static final BigDecimal ONE_KB = BigDecimal.valueOf(1024L);

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    public AssessmentOverviewViewModel populateModel(long assessmentId) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionService.getById(assessment.getCompetition());

        List<QuestionResource> questions = questionService.findByCompetition(assessment.getCompetition());
        List<QuestionResource> assessorViewQuestions = questions
                .stream()
                .collect(toList());

        return new AssessmentOverviewViewModel(assessmentId,
                assessment.getApplication(),
                assessment.getApplicationName(),
                assessment.getCompetition(),
                competition.getName(),
                competition.getAssessmentDaysLeftPercentage(),
                competition.getAssessmentDaysLeft(),
                getSections(assessment, assessorViewQuestions),
                getAppendices(assessment.getApplication(), assessorViewQuestions)
        );
    }

    private List<AssessmentOverviewSectionViewModel> getSections(AssessmentResource assessment,
                                                                 List<QuestionResource> questions) {
        List<SectionResource> sections = sectionRestService.getByCompetitionIdVisibleForAssessment(assessment.getCompetition()).getSuccess();

        Map<Long, List<FormInputResource>> formInputs = getFormInputsByQuestion(assessment.getCompetition());
        Map<Long, AssessorFormInputResponseResource> responses = getResponsesByFormInput(assessment.getId());

        Map<Long, QuestionResource> questionsMap = simpleToMap(questions, QuestionResource::getId, identity());
        return simpleMap(sections, sectionResource -> {
            List<QuestionResource> sectionQuestions = sectionResource.getQuestions()
                    .stream()
                    .map(questionsMap::get)
                    .filter(this::isAssessmentQuestion)
                    .collect(toList());

            return new AssessmentOverviewSectionViewModel(sectionResource.getId(),
                    sectionResource.getName(),
                    sectionResource.getAssessorGuidanceDescription(),
                    getQuestions(sectionQuestions, formInputs, responses),
                    "Finances".equals(sectionResource.getName())
            );
        });
    }

    private List<AssessmentOverviewQuestionViewModel> getQuestions(List<QuestionResource> questions,
                                                                   Map<Long, List<FormInputResource>> formInputs,
                                                                   Map<Long, AssessorFormInputResponseResource> responses) {
        return simpleMap(questions, question -> {
            List<FormInputResource> questionFormInputs = formInputs.getOrDefault(question.getId(), emptyList());
            Optional<FormInputResource> scopeInput = findScopeInput(questionFormInputs);
            Boolean scopeResponse = getResponseValue(scopeInput, responses).map(Boolean::valueOf).orElse(null);
            Optional<FormInputResource> scoreInput = findScoreInput(questionFormInputs);
            String scoreResponse = getResponseValue(scoreInput, responses).orElse(null);
            return new AssessmentOverviewQuestionViewModel(question.getId(),
                    question.getShortName(),
                    question.getQuestionNumber(),
                    question.getAssessorMaximumScore(),
                    !questionFormInputs.isEmpty(),
                    isAssessed(questionFormInputs, responses),
                    scopeResponse,
                    scoreResponse
            );
        });
    }

    private List<AssessmentOverviewAppendixViewModel> getAppendices(long applicationId, List<QuestionResource> questions) {
        List<FormInputResponseResource> applicantResponses = formInputResponseRestService.getResponsesByApplicationId(applicationId).getSuccess();
        Map<Long, QuestionResource> questionsMap = simpleToMap(questions, QuestionResource::getId, identity());
        return applicantResponses.stream()
                .filter(formInputResponseResource -> formInputResponseResource.getFileEntry() != null)
                .map(formInputResponseResource -> getAppendix(formInputResponseResource, questionsMap))
                .collect(toList());
    }

    private Map<Long, List<FormInputResource>> getFormInputsByQuestion(long competitionId) {
        List<FormInputResource> formInputResources = formInputRestService.getByCompetitionIdAndScope(competitionId, ASSESSMENT).getSuccess();
        return formInputResources.stream().collect(groupingBy(FormInputResource::getQuestion));
    }

    private Map<Long, AssessorFormInputResponseResource> getResponsesByFormInput(long assessmentId) {
        List<AssessorFormInputResponseResource> responses = assessorFormInputResponseRestService
                .getAllAssessorFormInputResponses(assessmentId).getSuccess();
        return simpleToMap(responses, AssessorFormInputResponseResource::getFormInput);
    }

    private boolean isAssessed(List<FormInputResource> formInputs,
                               Map<Long, AssessorFormInputResponseResource> responses) {
        return !formInputs.isEmpty() &&
                formInputs.stream().allMatch(formInputResource ->
                        isNotBlank(getResponseValue(formInputResource, responses).orElse(null)));
    }

    private Optional<FormInputResource> findScopeInput(List<FormInputResource> formInputs) {
        return findInputByType(formInputs, ASSESSOR_APPLICATION_IN_SCOPE);
    }

    private Optional<FormInputResource> findScoreInput(List<FormInputResource> formInputs) {
        return findInputByType(formInputs, ASSESSOR_SCORE);
    }

    private Optional<FormInputResource> findInputByType(List<FormInputResource> formInputs, FormInputType formInputType) {
        return simpleFindFirst(formInputs, formInputResource -> formInputType == formInputResource.getType());
    }

    private Optional<String> getResponseValue(Optional<FormInputResource> formInput,
                                              Map<Long, AssessorFormInputResponseResource> responses) {
        return formInput.flatMap(formInputResource -> getResponseValue(formInputResource, responses));
    }

    private Optional<String> getResponseValue(FormInputResource formInput,
                                              Map<Long, AssessorFormInputResponseResource> responses) {
        return ofNullable(responses.get(formInput.getId())).map(AssessorFormInputResponseResource::getValue);
    }

    private boolean isAssessmentQuestion(QuestionResource question) {
        return question.getQuestionSetupType() != APPLICATION_TEAM && question.getQuestionSetupType() != RESEARCH_CATEGORY;
    }

    private AssessmentOverviewAppendixViewModel getAppendix(FormInputResponseResource formInputResponse,
                                                            Map<Long, QuestionResource> questions) {
        QuestionResource question = questions.get(formInputResponse.getQuestion());

        String size = String.valueOf(BigDecimal.valueOf(formInputResponse.getFilesizeBytes()).divide(ONE_KB, 0, ROUND_UP)) + " KB";

        return new AssessmentOverviewAppendixViewModel(
                formInputResponse.getFormInput(),
                ofNullable(question.getShortName()).orElse(question.getName()),
                formInputResponse.getFilename(),
                size
        );
    }
}
