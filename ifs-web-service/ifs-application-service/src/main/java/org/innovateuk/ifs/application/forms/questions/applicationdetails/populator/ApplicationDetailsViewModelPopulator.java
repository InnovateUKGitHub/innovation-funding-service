package org.innovateuk.ifs.application.forms.questions.applicationdetails.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.forminput.ApplicationDetailsPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;

@Component
public class ApplicationDetailsViewModelPopulator {

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;
    @Autowired
    private ApplicationDetailsPopulator applicationDetailsPopulator;
    @Autowired
    private QuestionService questionService;

    public ApplicationDetailsViewModel populate(ApplicantQuestionResource question, CompetitionResource competition) {
        ApplicationDetailsInputViewModel viewModel = getViewModel(question);
        viewModel.setIsProcurementCompetition(PROCUREMENT.equals(competition.getFundingType()));

        NavigationViewModel navigationViewModel = applicationNavigationPopulator.addNavigation(question.getQuestion(), question.getApplication().getId());
        if (!isApplicationInViewMode(question.getApplication(), question.getCurrentApplicant().getOrganisation())) {
            removeNotifications(question);
        }

        return new ApplicationDetailsViewModel(question, viewModel, navigationViewModel);
    }

    private ApplicationDetailsInputViewModel getViewModel(ApplicantQuestionResource question) {
        return applicationDetailsPopulator.populate(question,
                    null,
                    question,
                    question.getApplicantFormInputs().get(0),
                    question.getApplicantFormInputs().get(0).responseForApplicant(question.getCurrentApplicant(), question));
    }

    private void removeNotifications(ApplicantQuestionResource questionResource) {
        QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(questionResource.getQuestion().getId(), questionResource.getApplication().getId(), questionResource.getCurrentApplicant().getOrganisation().getId());
        if (questionStatusResource != null) {
            List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(singletonList(questionStatusResource), questionResource.getCurrentUser().getId());
            questionService.removeNotifications(notifications);
        }
    }

    private Boolean isApplicationInViewMode(ApplicationResource application, OrganisationResource userOrganisation) {
        if(null == userOrganisation){
            return TRUE;
        }
        if(!application.isOpen()){
            return TRUE;
        }
        return FALSE;
    }
}
