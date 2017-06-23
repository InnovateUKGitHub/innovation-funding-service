package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.BaseModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * View model for the single question pages
 */
@Component
public class QuestionModelPopulator extends BaseModelPopulator {

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;
    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;
    @Autowired
    private QuestionService questionService;

    public QuestionViewModel populateModel(ApplicantQuestionResource question, final Model model, final ApplicationForm form) {
        List<AbstractFormInputViewModel> formInputViewModels = formInputViewModelGenerator.fromQuestion(question, form);
        NavigationViewModel navigationViewModel = applicationNavigationPopulator.addNavigation(question.getQuestion(), question.getApplication().getId());
        removeNotifications(question);

        return new QuestionViewModel(question, formInputViewModels, navigationViewModel, formInputViewModels.stream().anyMatch(AbstractFormInputViewModel::isReadonly), Optional.empty(), false);
    }


    private void removeNotifications(ApplicantQuestionResource questionResource) {

        if (isApplicationInViewMode(questionResource.getApplication(), Optional.of(questionResource.getCurrentApplicant().getOrganisation()))) {
            return;
        }

        QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(questionResource.getQuestion().getId(), questionResource.getApplication().getId(), questionResource.getCurrentApplicant().getOrganisation().getId());
        if (questionStatusResource != null) {
            List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(asList(questionStatusResource), questionResource.getCurrentUser().getId());
            questionService.removeNotifications(notifications);
        }
    }

}

