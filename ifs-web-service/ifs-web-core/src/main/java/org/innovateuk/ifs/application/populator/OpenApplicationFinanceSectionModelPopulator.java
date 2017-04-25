package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public class OpenApplicationFinanceSectionModelPopulator extends BaseOpenFinanceSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private OrganisationService organisationService;

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection){
        List<ApplicantQuestionResource> costsQuestions = applicantSection.questionsWithType(QuestionType.COST);

        OpenFinanceSectionViewModel openFinanceSectionViewModel = new OpenFinanceSectionViewModel(addNavigation(applicantSection.getSection(), applicantSection.getApplication().getId()),
                applicantSection.getSection(), true, applicantSection.getSection().getId(), applicantSection.getCurrentApplicant().getUser(), isSubFinanceSection(applicantSection.getSection()));
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        sectionApplicationViewModel.setCurrentApplication(applicantSection.getApplication());
        sectionApplicationViewModel.setCurrentCompetition(applicantSection.getCompetition());

        addQuestionsDetails(openFinanceSectionViewModel, applicantSection, form);
        addApplicationAndSections(openFinanceSectionViewModel, sectionApplicationViewModel, form, applicantSection;
        addOrganisationAndUserFinanceDetails(openFinanceSectionViewModel, application.getCompetition(), application.getId(), costsQuestions, user, form, organisationId);
        addFundingSection(openFinanceSectionViewModel, application.getCompetition());

        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(competition, section.getId(), openFinanceSectionViewModel.getSectionsMarkedAsComplete())
                || SectionType.FINANCE.equals(section.getType()));

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openFinanceSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);
        if(openFinanceSectionViewModel.getFinance() instanceof FinanceViewModel) {
            FinanceViewModel financeViewModel = (FinanceViewModel) openFinanceSectionViewModel.getFinance();
            populateSubSectionMenuOptions(openFinanceSectionViewModel, allSections, openFinanceSectionViewModel.getSectionApplicationViewModel().getUserOrganisation().getId(), financeViewModel.getOrganisationGrantClaimPercentage());
        }

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openFinanceSectionViewModel;
    }

    private void addOrganisationAndUserFinanceDetails(OpenFinanceSectionViewModel financeSectionViewModel, Long competitionId, Long applicationId, List<QuestionResource> costsQuestions, UserResource user,
                                                      ApplicationForm form, Long organisationId) {

        financeSectionViewModel.setFinanceOverviewViewModel(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(competitionId, applicationId));
        Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        financeSectionViewModel.setFinanceViewModel(financeHandler.getFinanceModelManager(organisationType).getFinanceViewModel(applicationId, costsQuestions, user.getId(), form, organisationId));
    }
}