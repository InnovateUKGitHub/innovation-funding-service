package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.AbstractFinanceModelPopulator;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentFinancesSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.viewmodel.UserApplicationRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class AssessmentFinancesSummaryModelPopulator extends AbstractFinanceModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    private SectionService sectionService;

    @Autowired
    private FinanceService financeService;

    public AssessmentFinancesSummaryModelPopulator(SectionService sectionService) {
        super(sectionService);
        this.sectionService = sectionService;
    }

    public AssessmentFinancesSummaryViewModel populateModel(Long assessmentId, Model model) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionService.getById(assessment.getCompetition());

        addApplicationAndOrganisationDetails(model, assessment.getApplication(), competition.getAssessorFinanceView());
        addFinanceDetails(model, competition.getId(), assessment.getApplication());

        return new AssessmentFinancesSummaryViewModel(assessmentId, assessment.getApplication(),
                assessment.getApplicationName(), competition.getAssessmentDaysLeft(), competition.getAssessmentDaysLeftPercentage());
    }

    private void addApplicationAndOrganisationDetails(Model model, long applicationId, AssessorFinanceView financeVew) {
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(applicationId);
        addOrganisationDetails(model, userApplicationRoles, financeVew);
    }

    private void addOrganisationDetails(Model model, List<ProcessRoleResource> userApplicationRoles, AssessorFinanceView financeVew) {
        model.addAttribute("academicOrganisations", getAcademicOrganisations(getApplicationOrganisations(userApplicationRoles)));
        model.addAttribute("applicationOrganisations", getApplicationOrganisations(userApplicationRoles));

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );

        model.addAttribute("showAssessorDetailedFinanceLink", financeVew.equals(DETAILED) ? true : false);
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }

    private SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                        || uar.getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .collect(Collectors.toCollection(supplier));
    }

    private SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
                .filter(o -> OrganisationTypeEnum.RESEARCH.getId().equals(o.getOrganisationType()))
                .collect(Collectors.toCollection(supplier));
    }


    public void addFinanceDetails(Model model, Long competitionId, Long applicationId) {
        addFinanceSections(competitionId, model);
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, applicationId);
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
        model.addAttribute("academicFileEntries", organisationFinanceOverview.getAcademicOrganisationFileEntries());
        model.addAttribute("totalFundingSought", organisationFinanceOverview.getTotalFundingSought());
        model.addAttribute("totalContribution", organisationFinanceOverview.getTotalContribution());
        model.addAttribute("totalOtherFunding", organisationFinanceOverview.getTotalOtherFunding());
        model.addAttribute("researchParticipationPercentage", applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccess());
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

        List<QuestionResource> allQuestions = questionService.findByCompetition(competitionId);

        Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap = financeSubSectionChildren.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> filterQuestions(s.getQuestions(), allQuestions)
                ));

        List<FormInputResource> formInputs = formInputRestService.getByCompetitionIdAndScope(competitionId, APPLICATION)
                .getSuccess();

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionsMap
                .values().stream().flatMap(a -> a.stream())
                .collect(Collectors.toMap(q -> q.getId(), k -> filterFormInputsByQuestion(k.getId(), formInputs)));

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

    private List<QuestionResource> filterQuestions(final List<Long> ids, final List<QuestionResource> list) {
        return simpleFilter(list, question -> ids.contains(question.getId()));
    }

    private List<FormInputResource> filterFormInputsByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> id.equals(input.getQuestion()) && !FormInputType.EMPTY.equals(input.getType()));
    }
}

