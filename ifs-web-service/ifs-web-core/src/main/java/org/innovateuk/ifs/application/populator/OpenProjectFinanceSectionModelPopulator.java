package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.ProjectFinanceOverviewModelManager;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
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
public class OpenProjectFinanceSectionModelPopulator extends BaseOpenFinanceSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProjectFinanceOverviewModelManager projectFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProjectService projectService;

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form,
                                              Model model,
                                              ApplicationResource application,
                                              SectionResource section,
                                              UserResource user,
                                              BindingResult bindingResult,
                                              List<SectionResource> allSections,
                                              final Long organisationId){
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(section.getId(), QuestionType.COST);
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());

        OpenFinanceSectionViewModel openFinanceSectionViewModel = new OpenFinanceSectionViewModel(addNavigation(section, application.getId()),
                section, true, section.getId(), user, isSubFinanceSection(section));
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(competition) || SectionType.FINANCE.equals(section.getType()));
        sectionApplicationViewModel.setCurrentApplication(application);
        sectionApplicationViewModel.setCurrentCompetition(competition);

        addQuestionsDetails(openFinanceSectionViewModel, application, form);
        addApplicationAndSections(openFinanceSectionViewModel, sectionApplicationViewModel, application, competition, user.getId(), section, form, allSections, Optional.of(organisationResource));

        String organisationType = organisationResource.getOrganisationTypeName();
        addOrganisationAndUserProjectFinanceDetails(application.getCompetition(), projectResource.getId(), costsQuestions, user, model, form, organisationType, organisationId);

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openFinanceSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);

        //TODO INFUND-7482 use finance view model when its complete.
        Integer organisationGrantClaimPercentage = (Integer) model.asMap().get("organisationGrantClaimPercentage");
        populateSubSectionMenuOptions(openFinanceSectionViewModel, allSections, organisationId, organisationGrantClaimPercentage);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openFinanceSectionViewModel;
    }

    private void addOrganisationAndUserProjectFinanceDetails(Long competitionId, Long projectId, List<QuestionResource> costsQuestions, UserResource user,
                                                      Model model, ApplicationForm form, String organisationType, Long organisationId) {
        projectFinanceOverviewModelManager.addFinanceDetails(model, competitionId, projectId);
        financeHandler.getProjectFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, projectId, costsQuestions, user.getId(), form, organisationId);
    }
}