package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.Optional;

public abstract class AbstractQuestionSummaryViewModel implements ApplicationRowSummaryViewModel {

    private final long applicationId;
    private final long questionId;
    private final String name;
    private final boolean complete;

    public AbstractQuestionSummaryViewModel(ApplicationSummaryData data, QuestionResource question) {
        this.name = question.getShortName();
        this.applicationId = data.getApplication().getId();
        this.questionId = question.getId();
        Optional<QuestionStatusResource> questionStatus = Optional.ofNullable(data.getQuestionToQuestionStatus().get(question.getId()));
        this.complete = questionStatus.map(QuestionStatusResource::getMarkedAsComplete).orElse(false);
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public String getEditUrl() {
        return String.format("/application/%d/form/question/%d", applicationId, questionId);
    }

}
