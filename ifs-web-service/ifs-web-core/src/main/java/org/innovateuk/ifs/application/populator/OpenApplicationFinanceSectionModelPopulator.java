package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public class OpenApplicationFinanceSectionModelPopulator extends BaseOpenFinanceSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection) {

        OpenFinanceSectionViewModel openFinanceSectionViewModel = new OpenFinanceSectionViewModel(addNavigation(applicantSection.getSection(), applicantSection.getApplication().getId()),
                applicantSection.getSection(), true, applicantSection.getSection().getId(), applicantSection.getCurrentApplicant().getUser(), isSubFinanceSection(applicantSection.getSection()));
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        sectionApplicationViewModel.setCurrentApplication(applicantSection.getApplication());
        sectionApplicationViewModel.setCurrentCompetition(applicantSection.getCompetition());

        addQuestionsDetails(openFinanceSectionViewModel, applicantSection, form);
        addApplicationAndSections(openFinanceSectionViewModel, sectionApplicationViewModel, form, applicantSection);
        addOrganisationAndUserFinanceDetails(openFinanceSectionViewModel, form, applicantSection);
        addFundingSection(openFinanceSectionViewModel, applicantSection);

        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(openFinanceSectionViewModel, applicantSection)
                || SectionType.FINANCE.equals(applicantSection.getSection().getType()));

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openFinanceSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);
        if(openFinanceSectionViewModel.getFinance() instanceof FinanceViewModel) {
            FinanceViewModel financeViewModel = (FinanceViewModel) openFinanceSectionViewModel.getFinance();
            populateSubSectionMenuOptions(openFinanceSectionViewModel, applicantSection.allSections().map(ApplicantSectionResource::getSection).collect(Collectors.toList()), openFinanceSectionViewModel.getSectionApplicationViewModel().getUserOrganisation().getId(), financeViewModel.getOrganisationGrantClaimPercentage());
        }

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openFinanceSectionViewModel;
    }

    private void addOrganisationAndUserFinanceDetails(OpenFinanceSectionViewModel financeSectionViewModel, ApplicationForm form, ApplicantSectionResource applicantSection) {
        List<ApplicantQuestionResource> costsQuestions = applicantSection.questionsWithType(QuestionType.COST);

        financeSectionViewModel.setFinanceOverviewViewModel(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(applicantSection.getCompetition().getId(), applicantSection.getApplication().getId()));
        financeSectionViewModel.setFinanceViewModel(financeHandler.getFinanceModelManager(applicantSection.getCurrentApplicant().getOrganisation().getOrganisationType()).getFinanceViewModel(applicantSection.getApplication().getId(), costsQuestions.stream().map(ApplicantQuestionResource::getQuestion).collect(Collectors.toList()), applicantSection.getCurrentApplicant().getUser().getId(), form, applicantSection.getCurrentApplicant().getOrganisation().getId()));
    }
}