package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.AssessorFeedback;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.resource.QuestionResource;

import java.util.Optional;

/**
 * A view model object representing a Question in the Assessment Summary section of the Assessment Review page.
 *
 * Created by dwatson on 07/10/15.
 */
public class AssessmentSummarySectionQuestion {

    private Long id;
    private String name;
    private AssessmentSummarySectionQuestionFeedback feedback;

    public AssessmentSummarySectionQuestion(Long id, String name, AssessmentSummarySectionQuestionFeedback feedback) {
        this.id = id;
        this.name = name;
        this.feedback = feedback;
    }

    public AssessmentSummarySectionQuestion(QuestionResource question, Optional<AssessorFeedbackResource> feedback) {
        this(question.getId(), question.getName(), feedback.map(AssessmentSummarySectionQuestionFeedback::new).orElse(null));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AssessmentSummarySectionQuestionFeedback getFeedback() {
        return feedback;
    }
}
