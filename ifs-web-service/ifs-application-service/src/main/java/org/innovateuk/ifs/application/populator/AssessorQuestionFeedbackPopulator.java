package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.AssessQuestionFeedbackViewModel;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Populator for the individual question assessor feedback page
 */
@Component
public class AssessorQuestionFeedbackPopulator {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FeedbackNavigationPopulator feedbackNavigationPopulator;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    public AssessQuestionFeedbackViewModel populate(ApplicationResource applicationResource, long questionId) {

        QuestionResource questionResource = questionService.getById(questionId);
        long applicationId = applicationResource.getId();

        List<FormInputResponseResource> responseResource = formInputResponseRestService.getByApplicationIdAndQuestionId(
                applicationId, questionResource.getId()).getSuccessObjectOrThrowException();

        AssessmentFeedbackAggregateResource aggregateResource = assessorFormInputResponseRestService
                .getAssessmentAggregateFeedback(applicationId, questionId)
                .getSuccessObjectOrThrowException();
        NavigationViewModel navigationViewModel = feedbackNavigationPopulator.addNavigation(questionResource, applicationId);

      return new AssessQuestionFeedbackViewModel(applicationResource,
              questionResource,
              responseResource,
              aggregateResource,
              navigationViewModel);
    }
}
