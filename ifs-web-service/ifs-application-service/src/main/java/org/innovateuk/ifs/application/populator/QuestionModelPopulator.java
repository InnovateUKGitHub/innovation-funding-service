package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.QuestionOrganisationDetailsViewModel;
import org.innovateuk.ifs.application.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private InviteRestService inviteRestService;

    public QuestionViewModel populateModel(ApplicantQuestionResource question, final Model model, final ApplicationForm form, final QuestionOrganisationDetailsViewModel organisationDetailsViewModel) {
        List<AbstractFormInputViewModel> formInputViewModels = formInputViewModelGenerator.fromQuestion(question, form);
        NavigationViewModel navigationViewModel = applicationNavigationPopulator.addNavigation(question.getQuestion(), question.getApplication().getId());
        removeNotifications(question);

        return new QuestionViewModel(question, formInputViewModels, navigationViewModel, formInputViewModels.stream().anyMatch(AbstractFormInputViewModel::isReadonly));
    }


    private void removeNotifications(ApplicantQuestionResource questionResource) {

        if (isApplicationInViewMode(questionResource.getApplication(), Optional.of(questionResource.getCurrentApplicant().getOrganisation()))) {
            return;
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(questionResource.getQuestion().getId(), questionResource.getApplication().getId(), questionResource.getCurrentApplicant().getOrganisation().getId());
        questionAssignees = new HashMap<>();
        if (questionStatusResource != null) {
            questionAssignees.put(questionResource.getQuestion().getId(), questionStatusResource);
        }

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), questionResource.getCurrentUser().getId());
        questionService.removeNotifications(notifications);
    }

}

