package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceOverviewViewModel;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

//TODO - INFUND-7482 - remove usages of Model model
@Component
public class ProjectFinanceOverviewModelManager implements FinanceOverviewModelManager {
    private SectionService sectionService;
    private QuestionService questionService;
    private FormInputRestService formInputRestService;
    private ProjectFinanceService financeService;

    @Autowired
    public ProjectFinanceOverviewModelManager(SectionService sectionService,
                                              QuestionService questionService,
                                              FormInputRestService formInputRestService,
                                              ProjectFinanceService financeService) {
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.formInputRestService = formInputRestService;
        this.financeService = financeService;
    }

    // TODO DW - INFUND-1555 - handle rest results
    public void addFinanceDetails(Model model, Long competitionId, Long projectId, Optional<Long> organisationId) {
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

    	if(section == null) {
    		return;
    	}

        sectionService.removeSectionsQuestionsWithType(section, FormInputType.EMPTY);

        model.addAttribute("financeSection", section);
        List<SectionResource> financeSubSectionChildren = getFinanceSubSectionChildren(competitionId, section);
        model.addAttribute("financeSectionChildren", financeSubSectionChildren);

        List<QuestionResource> allQuestions = questionService.findByCompetition(competitionId);

        Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap = financeSubSectionChildren.stream()
                .collect(toMap(
                        SectionResource::getId,
                        s -> filterQuestions(s.getQuestions(), allQuestions)
                ));
        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);

        List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(competitionId, APPLICATION).getSuccessObjectOrThrowException();

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

        if(section == null) {
            return;
        }

        sectionService.removeSectionsQuestionsWithType(section, FormInputType.EMPTY);

        viewModel.setFinanceSection(section);
        List<SectionResource> financeSubSectionChildren = getFinanceSubSectionChildren(competitionId, section);
        viewModel.setFinanceSectionChildren(financeSubSectionChildren);

        List<QuestionResource> allQuestions = questionService.findByCompetition(competitionId);

        Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap = financeSubSectionChildren.stream()
                .collect(toMap(
                        SectionResource::getId,
                        s -> filterQuestions(s.getQuestions(), allQuestions)
                ));
        viewModel.setFinanceSectionChildrenQuestionsMap(financeSectionChildrenQuestionsMap);

        List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(competitionId, APPLICATION).getSuccessObjectOrThrowException();

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));
        viewModel.setFinanceSectionChildrenQuestionFormInputs(financeSectionChildrenQuestionFormInputs);
    }

    private List<SectionResource> getFinanceSubSectionChildren(Long competitionId, SectionResource section) {
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competitionId);
        List<SectionResource> financeSectionChildren = sectionService.findResourceByIdInList(section.getChildSections(), allSections);
        List<SectionResource> financeSubSectionChildren = new ArrayList<>();
        financeSectionChildren.stream().forEach(sectionResource -> {
                    if (!sectionResource.getChildSections().isEmpty()) {
                        financeSubSectionChildren.addAll(
                                sectionService.findResourceByIdInList(sectionResource.getChildSections(), allSections)
                        );
                    }
                }
        );
        return financeSubSectionChildren;
    }

    private List<QuestionResource> filterQuestions(final List<Long> ids, final List<QuestionResource> list){
        return simpleFilter(list, question -> ids.contains(question.getId()));
    }

    private List<FormInputResource> filterFormInputsByQuestion(final Long id, final List<FormInputResource> list){
        return simpleFilter(list, input -> id.equals(input.getQuestion()) && !FormInputType.EMPTY.equals(input.getType()));
    }
}
