package org.innovateuk.ifs.application.feedback.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;

import java.util.List;

/**
 * View model for the individual question assessor feedback page
 */
public class AssessQuestionFeedbackViewModel {

    private ApplicationResource application;

    private QuestionResource question;

    private List<FormInputResponseResource> responses;

    private AssessmentFeedbackAggregateResource aggregateResource;

    private NavigationViewModel navigation;

    private boolean isInterviewAssessor;

    public AssessQuestionFeedbackViewModel(ApplicationResource application,
                                           QuestionResource question,
                                           List<FormInputResponseResource> responses,
                                           AssessmentFeedbackAggregateResource aggregateResource,
                                           NavigationViewModel navigationViewModel,
                                           boolean isInterviewAssessor
    ) {
        this.application = application;
        this.question = question;
        this.responses = responses;
        this.aggregateResource = aggregateResource;
        this.navigation = navigationViewModel;
        this.isInterviewAssessor = isInterviewAssessor;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public QuestionResource getQuestion() {
        return question;
    }

    public List<FormInputResponseResource> getResponses() {
        return responses;
    }

    public AssessmentFeedbackAggregateResource getAggregateResource() {
        return aggregateResource;
    }

    public NavigationViewModel getNavigation() {
        return navigation;
    }

    public boolean isInterviewAssessor() {
        return isInterviewAssessor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessQuestionFeedbackViewModel that = (AssessQuestionFeedbackViewModel) o;

        return new EqualsBuilder()
                .append(application, that.application)
                .append(question, that.question)
                .append(responses, that.responses)
                .append(aggregateResource, that.aggregateResource)
                .append(navigation, that.navigation)
                .append(isInterviewAssessor, isInterviewAssessor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(application)
                .append(question)
                .append(responses)
                .append(aggregateResource)
                .append(navigation)
                .append(isInterviewAssessor)
                .toHashCode();
    }
}
