package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

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
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, ApplicationResource application, SectionResource section, UserResource user, BindingResult bindingResult, List<SectionResource> allSections, Long organisationId){
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(section.getId(), QuestionType.COST);
        Optional<OrganisationResource> organisationResource = Optional.of(organisationService.getOrganisationById(organisationId));

        OpenFinanceSectionViewModel openFinanceSectionViewModel = new OpenFinanceSectionViewModel(addNavigation(section, application.getId()),
                section, true, section.getId(), user, isSubFinanceSection(section));
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        sectionApplicationViewModel.setCurrentApplication(application);
        sectionApplicationViewModel.setCurrentCompetition(competition);

        addQuestionsDetails(openFinanceSectionViewModel, application, form);
        addApplicationAndSections(openFinanceSectionViewModel, sectionApplicationViewModel, application, competition, user.getId(), section, form, allSections, organisationResource);
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