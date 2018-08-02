package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.AbstractFinanceModelPopulator;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentFinancesSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
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
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;

import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;

@Component
public class AssessmentFinancesSummaryModelPopulator extends AbstractFinanceModelPopulator {

    private CompetitionRestService competitionRestService;
    private AssessmentService assessmentService;
    private ProcessRoleService processRoleService;
    private FileEntryRestService fileEntryRestService;
    private ApplicationFinanceRestService applicationFinanceRestService;
    private SectionService sectionService;
    private OrganisationService organisationService;
    private FinanceService financeService;

    public AssessmentFinancesSummaryModelPopulator(CompetitionRestService competitionRestService,
                                                   AssessmentService assessmentService,
                                                   ProcessRoleService processRoleService,
                                                   FileEntryRestService fileEntryRestService,
                                                   ApplicationFinanceRestService applicationFinanceRestService,
                                                   FinanceService financeService,
                                                   SectionService sectionService,
                                                   OrganisationService organisationService,
                                                   FormInputRestService formInputRestService,
                                                   QuestionService questionService) {
        super(sectionService, formInputRestService, questionService);
        this.organisationService = organisationService;
        this.sectionService = sectionService;
        this.competitionRestService = competitionRestService;
        this.assessmentService = assessmentService;
        this.processRoleService = processRoleService;
        this.fileEntryRestService = fileEntryRestService;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.financeService = financeService;
    }

    public AssessmentFinancesSummaryViewModel populateModel(Long assessmentId, Model model) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionRestService.getCompetitionById(assessment.getCompetition()).getSuccess();

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

        Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap =
                getFinanceSectionChildrenQuestionsMap(financeSubSectionChildren, competitionId);

        Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs =
                getFinanceSectionChildrenQuestionFormInputs(competitionId, financeSectionChildrenQuestionsMap);

        model.addAttribute("financeSectionChildrenQuestionsMap", financeSectionChildrenQuestionsMap);
        model.addAttribute("financeSectionChildrenQuestionFormInputs", financeSectionChildrenQuestionFormInputs);
    }
}

