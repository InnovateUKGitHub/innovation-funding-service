package org.innovateuk.ifs.assessment.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationFinanceOverview;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class AssessmentFinancesSummaryModelPopulator {
    private static final Log LOG = LogFactory.getLog(AssessmentFinancesSummaryModelPopulator.class);

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    OrganisationRestService organisationRestService;
    @Autowired
    FileEntryRestService fileEntryRestService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FormInputService formInputService;
    @Autowired
    ApplicationFinanceRestService applicationFinanceRestService;
    @Autowired
    SectionService sectionService;
    @Autowired
    FinanceService financeService;


    public void populateModel(Long assessmentId, final Model model) {

        final AssessmentResource assessment = getAssessment(assessmentId);
        final ApplicationResource application = getApplication(assessment.getApplication());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        addApplicationAndOrganisationDetails(application, model);
        addFinanceDetails(model, competition.getId(), application.getId());

        model.addAttribute("assessmentId", assessmentId);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
     }

    private AssessmentResource getAssessment(final Long assessmentId) {
        return assessmentService.getById(assessmentId);
    }

    private ApplicationResource getApplication(final Long applicationId) {
        return applicationService.getById(applicationId);
    }

    private void addApplicationAndOrganisationDetails(ApplicationResource application, Model model) {

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        addOrganisationDetails(model, userApplicationRoles);
    }

    private void addOrganisationDetails(Model model,  List<ProcessRoleResource> userApplicationRoles) {
        model.addAttribute("academicOrganisations", getAcademicOrganisations(getApplicationOrganisations(userApplicationRoles)));
        model.addAttribute("applicationOrganisations", getApplicationOrganisations(userApplicationRoles));

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
                .findFirst();
    }


    private SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                        || uar.getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
                .collect(Collectors.toCollection(supplier));
    }

    private SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations) {
        Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
                .filter(o -> OrganisationTypeEnum.ACADEMIC.getOrganisationTypeId().equals(o.getOrganisationType()))
                .collect(Collectors.toCollection(supplier));
    }


    public void addFinanceDetails(Model model, Long competitionId, Long applicationId) {
        addFinanceSections(competitionId, model);
        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview(financeService, fileEntryRestService, applicationId);
        model.addAttribute("financeTotal", organisationFinanceOverview.getTotal());
        model.addAttribute("financeTotalPerType", organisationFinanceOverview.getTotalPerType());
        Map<Long, ApplicationFinanceResource> organisationFinances = organisationFinanceOverview.getApplicationFinancesByOrganisation();
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
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> filterQuestions(s.getQuestions(), allQuestions)
                ));

        List<FormInputResource> formInputs = formInputService.findApplicationInputsByCompetition(competitionId);

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

