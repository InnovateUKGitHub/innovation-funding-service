package org.innovateuk.ifs.application.feedback.populator;

import org.innovateuk.ifs.application.feedback.viewmodel.AssessQuestionFeedbackViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Populator for the individual question assessor feedback page
 */
@Component
public class AssessorQuestionFeedbackPopulator {

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FeedbackNavigationPopulator feedbackNavigationPopulator;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    public AssessQuestionFeedbackViewModel populate(ApplicationResource applicationResource, QuestionResource questionResource, UserResource user, Model model) {

        long applicationId = applicationResource.getId();

        List<FormInputResponseResource> responseResource =
                questionResource.getQuestionSetupType().equals(QuestionSetupType.RESEARCH_CATEGORY) ? singletonList(
                        new FormInputResponseResource(applicationResource.getResearchCategory().getName())
                ) : formInputResponseRestService.getByApplicationIdAndQuestionId(applicationId, questionResource.getId()).getSuccess();

        List<FormInputResource> inputs = formInputRestService.getByQuestionId(questionResource.getId()).getSuccess();

        AssessmentFeedbackAggregateResource aggregateResource = assessorFormInputResponseRestService
                .getAssessmentAggregateFeedback(applicationId, questionResource.getId())
                .getSuccess();
        NavigationViewModel navigationViewModel = feedbackNavigationPopulator.addNavigation(questionResource, applicationId);

      return new AssessQuestionFeedbackViewModel(applicationResource,
              questionResource,
              responseResource,
              aggregateResource,
              navigationViewModel);
    }
}
