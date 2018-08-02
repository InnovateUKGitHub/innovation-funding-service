package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;

@Component
public class ProjectFinanceOverviewModelManager extends AbstractFinanceModelPopulator implements FinanceOverviewModelManager {
    private SectionService sectionService;
    private FormInputRestService formInputRestService;
    private ProjectFinanceService financeService;

    @Autowired
    public ProjectFinanceOverviewModelManager(SectionService sectionService,
                                              QuestionRestService questionRestService,
                                              FormInputRestService formInputRestService,
                                              ProjectFinanceService financeService) {
        super(sectionService, formInputRestService, questionRestService);
        this.financeService = financeService;
        this.sectionService = sectionService;
        this.formInputRestService = formInputRestService;
    }

    public void addFinanceDetails(Model model, Long competitionId, Long projectId) {
        addFinanceSections(competitionId, model);
        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationProjectFinanceOverviewImpl(financeService, projectId);
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());
    }

    private void addFinanceSections(Long competitionId, Model model) {
        SectionResource section = sectionService.getFinanceSection(competitionId);

        if (section == null) {
            return;
        }

        sectionService.removeSectionsQuestionsWithType(section, FormInputType.EMPTY);

        model.addAttribute("financeSection", section);
        List<SectionResource> financeSubSectionChildren = getFinanceSubSectionChildren(competitionId, section);
        model.addAttribute("financeSectionChildren", financeSubSectionChildren);

        Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap =
                getFinanceSectionChildrenQuestionsMap(financeSubSectionChildren, competitionId);

        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);

        List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(competitionId, APPLICATION).getSuccess();

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));
        model.addAttribute("financeSectionChildrenQuestionFormInputs", financeSectionChildrenQuestionFormInputs);
    }

    public BaseFinanceOverviewViewModel getFinanceDetailsViewModel(Long competitionId, Long projectId) {
        ProjectFinanceOverviewViewModel viewModel = new ProjectFinanceOverviewViewModel();

        addFinanceSections(competitionId, viewModel);
        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationProjectFinanceOverviewImpl(financeService, projectId);
        viewModel.setFinanceTotal(organisationFinanceOverview.getTotal());
        viewModel.setFinanceTotalPerType(organisationFinanceOverview.getTotalPerType());
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        viewModel.setOrganisationFinances(organisationFinances);
        viewModel.setTotalFundingSought(organisationFinanceOverview.getTotalFundingSought());
        viewModel.setTotalContribution(organisationFinanceOverview.getTotalContribution());
        viewModel.setTotalOtherFunding(organisationFinanceOverview.getTotalOtherFunding());

        return viewModel;
    }

    private void addFinanceSections(Long competitionId, ProjectFinanceOverviewViewModel viewModel) {
        SectionResource section = sectionService.getFinanceSection(competitionId);

        if (section == null) {
            return;
        }

        sectionService.removeSectionsQuestionsWithType(section, FormInputType.EMPTY);

        viewModel.setFinanceSection(section);
        List<SectionResource> financeSubSectionChildren = getFinanceSubSectionChildren(competitionId, section);
        viewModel.setFinanceSectionChildren(financeSubSectionChildren);

        Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap =
                getFinanceSectionChildrenQuestionsMap(financeSubSectionChildren, competitionId);

        List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(competitionId, APPLICATION).getSuccess();

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));
        viewModel.setFinanceSectionChildrenQuestionFormInputs(financeSectionChildrenQuestionFormInputs);
    }
}
