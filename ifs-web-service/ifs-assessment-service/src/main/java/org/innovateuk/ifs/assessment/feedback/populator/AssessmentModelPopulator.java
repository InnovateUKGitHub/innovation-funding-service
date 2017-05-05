package org.innovateuk.ifs.assessment.feedback.populator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.assessment.feedback.viewmodel.BaseAssessmentFeedbackViewModel;

import java.util.Objects;

public abstract class AssessmentModelPopulator<T extends BaseAssessmentFeedbackViewModel> {

    public final T populateModel(final long assessmentId, final QuestionResource question) {
        Objects.requireNonNull(question, "question cannot be null");
        return populate(assessmentId, question);
    }

    protected abstract T populate(final long assessmentId, final QuestionResource question);
}
