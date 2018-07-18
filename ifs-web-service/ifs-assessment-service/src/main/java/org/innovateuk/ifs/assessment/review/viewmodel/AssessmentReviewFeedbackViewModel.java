package org.innovateuk.ifs.assessment.review.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;

import java.util.Collections;
import java.util.List;

public class AssessmentReviewFeedbackViewModel {
    private final List<AssessorFormInputResponseResource> score;
    private final List<AssessorFormInputResponseResource> feedback;

    public AssessmentReviewFeedbackViewModel() {
        score = Collections.emptyList();
        feedback = Collections.emptyList();
    }

    public AssessmentReviewFeedbackViewModel(List<AssessorFormInputResponseResource> score, List<AssessorFormInputResponseResource> feedback) {
        this.score = score;
        this.feedback = feedback;
    }

    public List<AssessorFormInputResponseResource> getScore() {
        return score;
    }

    public List<AssessorFormInputResponseResource> getFeedback() {
        return feedback;
    }
}
