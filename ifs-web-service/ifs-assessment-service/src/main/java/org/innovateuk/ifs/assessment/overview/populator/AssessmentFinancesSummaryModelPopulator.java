package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.AbstractFinanceModelPopulator;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
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
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;

@Component
public class AssessmentFinancesSummaryModelPopulator extends AbstractFinanceModelPopulator {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    private SectionService sectionService;
    private OrganisationService organisationService;
    private FormInputRestService formInputRestService;

    @Autowired
    private FinanceService financeService;

    public AssessmentFinancesSummaryModelPopulator(SectionService sectionService,
                                                   OrganisationService organisationService,
                                                   FormInputRestService formInputRestService) {
        super(sectionService, formInputRestService);
        this.organisationService = organisationService;
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
        SortedSet<OrganisationResource> applicationOrganisations = organisationService.getApplicationOrganisations(userApplicationRoles);
        model.addAttribute("academicOrganisations", organisationService.getAcademicOrganisations(applicationOrganisations));
        model.addAttribute("applicationOrganisations", applicationOrganisations);

        Optional<OrganisationResource> leadOrganisation = organisationService.getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );

        model.addAttribute("showAssessorDetailedFinanceLink", financeVew.equals(DETAILED));
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

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs =
                getFinanceSectionChildrenQuestionFormInputs(competitionId, financeSectionChildrenQuestionsMap);

        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);
        model.addAttribute("financeSectionChildrenQuestionFormInputs", financeSectionChildrenQuestionFormInputs);
    }
}

