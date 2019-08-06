package org.innovateuk.ifs.application.feedback.populator;

import org.innovateuk.ifs.application.feedback.viewmodel.AssessQuestionFeedbackViewModel;
import org.innovateuk.ifs.application.forms.questions.researchcategory.form.ResearchCategoryForm;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryFormPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.populator.ApplicationResearchCategoryModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Populator for the individual question assessor feedback page
 */
@Component
public class AssessorQuestionFeedbackPopulator {

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private FeedbackNavigationPopulator feedbackNavigationPopulator;

    @Autowired
    private ApplicationResearchCategoryModelPopulator applicationResearchCategoryModelPopulator;

    @Autowired
    private ApplicationResearchCategoryFormPopulator applicationResearchCategoryFormPopulator;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    public AssessQuestionFeedbackViewModel populate(ApplicationResource applicationResource, long questionId, UserResource user, Model model) {

        QuestionResource questionResource = questionRestService.findById(questionId).getSuccess();
        long applicationId = applicationResource.getId();

        List<FormInputResponseResource> responseResource = formInputResponseRestService.getByApplicationIdAndQuestionId(
                applicationId, questionResource.getId()).getSuccess();

        if (questionResource.getQuestionSetupType() == RESEARCH_CATEGORY) {
            model.addAttribute("researchCategoryModel", applicationResearchCategoryModelPopulator.populate(
                    applicationResource, user.getId(), questionId));
            model.addAttribute("form", applicationResearchCategoryFormPopulator.populate(applicationResource,
                    new ResearchCategoryForm()));
        }

        AssessmentFeedbackAggregateResource aggregateResource = assessorFormInputResponseRestService
                .getAssessmentAggregateFeedback(applicationId, questionId)
                .getSuccess();
        NavigationViewModel navigationViewModel = feedbackNavigationPopulator.addNavigation(questionResource, applicationId);

        return new AssessQuestionFeedbackViewModel(applicationResource,
                questionResource,
                responseResource,
                aggregateResource,
                navigationViewModel);
    }
}
