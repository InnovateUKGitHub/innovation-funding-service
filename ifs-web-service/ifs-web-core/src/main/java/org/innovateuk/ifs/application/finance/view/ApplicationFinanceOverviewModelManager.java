package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.BaseFinanceOverviewViewModel;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

//TODO - INFUND-7482 - remove usages of Model model
@Component
public class ApplicationFinanceOverviewModelManager implements FinanceOverviewModelManager {
    private ApplicationFinanceRestService applicationFinanceRestService;
    private SectionService sectionService;
    private QuestionService questionService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private FormInputService formInputService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    public ApplicationFinanceOverviewModelManager(ApplicationFinanceRestService applicationFinanceRestService, SectionService sectionService,
                                                  FinanceService financeService, QuestionService questionService,
                                                  FileEntryRestService fileEntryRestService,
                                                  FormInputService formInputService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.sectionService = sectionService;
        this.financeService = financeService;
        this.questionService = questionService;
        this.fileEntryRestService = fileEntryRestService;
        this.formInputService = formInputService;
    }

    public void addFinanceDetails(Model model, Long competitionId, Long applicationId, Optional<Long> organisationId) {
        addFinanceSections(competitionId, model);
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, applicationId);
        if (organisationId.isPresent()) {
            String organisationType = organisationService.getOrganisationById(organisationId.get()).getOrganisationTypeName();
            model.addAttribute("maySeeAcademicBreakdown", "Research".equals(organisationType));
        } else {
            //If no organisation id is present then this is an internal user and they can view the breakdown.
            model.addAttribute("maySeeAcademicBreakdown", true);
        }
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
        model.addAttribute("academicFileEntries", organisationFinanceOverview.getAcademicOrganisationFileEntries());
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());
        model.addAttribute("researchParticipationPercentage", applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccessObjectOrThrowException());
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

        List<FormInputResource> formInputs = formInputService.findApplicationInputsByCompetition(competitionId);

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));


        //Remove all questions without non-empty form inputs.
        Set<Long> questionsWithoutNonEmptyFormInput = financeSectionChildrenQuestionFormInputs.keySet().stream()
                .filter(key -> financeSectionChildrenQuestionFormInputs.get(key).isEmpty()).collect(Collectors.toSet());
        questionsWithoutNonEmptyFormInput.forEach(questionId -> {
            financeSectionChildrenQuestionFormInputs.remove(questionId);
            financeSectionChildrenQuestionsMap.keySet().forEach(key -> financeSectionChildrenQuestionsMap.get(key)
                    .removeIf(questionResource -> questionResource.getId().equals(questionId)));
        });

        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);
        model.addAttribute("financeSectionChildrenQuestionFormInputs", financeSectionChildrenQuestionFormInputs);
    }

    public BaseFinanceOverviewViewModel getFinanceDetailsViewModel(Long competitionId, Long applicationId) {
        ApplicationFinanceOverviewViewModel viewModel = new ApplicationFinanceOverviewViewModel();

        addFinanceSections(competitionId, viewModel);
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, applicationId);
        viewModel.setFinanceTotal(organisationFinanceOverview.getTotal());
        viewModel.setFinanceTotalPerType(organisationFinanceOverview.getTotalPerType());
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        viewModel.setOrganisationFinances(organisationFinances);
        viewModel.setAcademicFileEntries(organisationFinanceOverview.getAcademicOrganisationFileEntries());
        viewModel.setTotalFundingSought(organisationFinanceOverview.getTotalFundingSought());
        viewModel.setTotalContribution(organisationFinanceOverview.getTotalContribution());
        viewModel.setTotalOtherFunding(organisationFinanceOverview.getTotalOtherFunding());
        viewModel.setResearchParticipationPercentage(applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccessObjectOrThrowException());

        return viewModel;
    }

    private void addFinanceSections(Long competitionId, BaseFinanceOverviewViewModel viewModel) {
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

        List<FormInputResource> formInputs = formInputService.findApplicationInputsByCompetition(competitionId);

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));


        //Remove all questions without non-empty form inputs.
        Set<Long> questionsWithoutNonEmptyFormInput = financeSectionChildrenQuestionFormInputs.keySet().stream()
                .filter(key -> financeSectionChildrenQuestionFormInputs.get(key).isEmpty()).collect(Collectors.toSet());
        questionsWithoutNonEmptyFormInput.forEach(questionId -> {
            financeSectionChildrenQuestionFormInputs.remove(questionId);
            financeSectionChildrenQuestionsMap.keySet().forEach(key -> financeSectionChildrenQuestionsMap.get(key)
                    .removeIf(questionResource -> questionResource.getId().equals(questionId)));
        });

        viewModel.setFinanceSectionChildrenQuestionsMap(financeSectionChildrenQuestionsMap);
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
