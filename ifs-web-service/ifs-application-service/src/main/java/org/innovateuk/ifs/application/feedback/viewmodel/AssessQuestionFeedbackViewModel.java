package org.innovateuk.ifs.application.feedback.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.List;

/**
 * View model for the individual question assessor feedback page
 */
public class AssessQuestionFeedbackViewModel implements BaseAnalyticsViewModel {

    private ApplicationResource application;
    private QuestionResource question;
    private List<FormInputResponseResource> responses;
    private List<FormInputResource> inputs;
    private AssessmentFeedbackAggregateResource aggregateResource;
    private NavigationViewModel navigation;

    public AssessQuestionFeedbackViewModel(ApplicationResource application,
                                           QuestionResource question,
                                           List<FormInputResponseResource> responses,
                                           List<FormInputResource> inputs,
                                           AssessmentFeedbackAggregateResource aggregateResource,
                                           NavigationViewModel navigationViewModel
    ) {
        this.application = application;
        this.question = question;
        this.responses = responses;
        this.inputs = inputs;
        this.aggregateResource = aggregateResource;
        this.navigation = navigationViewModel;
    }

    @Override
    public Long getApplicationId() {
        return application.getId();
    }

    @Override
    public String getCompetitionName() {
        return application.getCompetitionName();
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

    public boolean isAppendix(FormInputResponseResource response) {
        return inputs.stream()
                .anyMatch(input -> input.getId().equals(response.getFormInput()) && input.getType().equals(FormInputType.FILEUPLOAD));
    }

    public boolean isTemplateDocument(FormInputResponseResource response) {
        return inputs.stream()
                .anyMatch(input -> input.getId().equals(response.getFormInput()) && input.getType().equals(FormInputType.TEMPLATE_DOCUMENT));
    }

    public String getTemplateDocumentTitle() {
        return inputs.stream()
                .filter(input -> input.getType().equals(FormInputType.TEMPLATE_DOCUMENT))
                .findAny()
                .map(FormInputResource::getDescription)
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessQuestionFeedbackViewModel that = (AssessQuestionFeedbackViewModel) o;

        return new EqualsBuilder()
                .append(application, that.application)
                .append(question, that.question)
                .append(responses, that.responses)
                .append(aggregateResource, that.aggregateResource)
                .append(navigation, that.navigation)
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
                .toHashCode();
    }
}
