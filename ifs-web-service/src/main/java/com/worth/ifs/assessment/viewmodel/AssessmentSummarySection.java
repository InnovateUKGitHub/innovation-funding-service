package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A view model object representing a Section within the Assessment Summary section of the Assessment Review page.
 *
 * Created by dwatson on 07/10/15.
 */
public class AssessmentSummarySection {

    private final List<AssessmentSummarySectionQuestion> questionsRequiringFeedback;
    private final boolean assessmentComplete;
    private final Long id;
    private final String name;

    public AssessmentSummarySection(List<AssessmentSummarySectionQuestion> questionsRequiringFeedback, Long id, String name) {
        this.questionsRequiringFeedback = questionsRequiringFeedback;
        this.assessmentComplete = questionsRequiringFeedback.stream().allMatch(question -> question.getFeedback() != null && !isBlank(question.getFeedback().getFeedbackValue()));
        this.id = id;
        this.name = name;
    }

    public AssessmentSummarySection(Section section, Map<Question, Optional<AssessorFeedback>> questionsAndFeedback) {
        this(section.getQuestions().stream().
                        filter(question -> question.getNeedingAssessorScore() || !isBlank(question.getAssessorConfirmationQuestion())).
                        map(question -> new AssessmentSummarySectionQuestion(question, questionsAndFeedback.get(question))).
                                        collect(toList()),
                                section.getId(),
                                section.getName());
    }

    public List<AssessmentSummarySectionQuestion> getQuestionsRequiringFeedback() {
        return questionsRequiringFeedback;
    }

    public boolean isAssessmentComplete() {
        return assessmentComplete;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
