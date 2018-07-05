package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationResearchParticipationViewModel;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.team.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class SummaryViewModelFragmentPopulator {

    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private SectionService sectionService;
    private QuestionService questionService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private ProcessRoleService processRoleService;
    private OrganisationService organisationService;
    private FormInputRestService formInputRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private FormInputResponseService formInputResponseService;
    private AssessmentRestService assessmentRestService;
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    private ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;
    private UserService userService;
    private ApplicantRestService applicantRestService;
    private FormInputViewModelGenerator formInputViewModelGenerator;
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    public SummaryViewModelFragmentPopulator(ApplicationService applicationService,
                                             CompetitionService competitionService,
                                             SectionService sectionService,
                                             QuestionService questionService,
                                             AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                             ProcessRoleService processRoleService,
                                             OrganisationService organisationService,
                                             FormInputRestService formInputRestService,
                                             FormInputResponseRestService formInputResponseRestService,
                                             FormInputResponseService formInputResponseService,
                                             AssessmentRestService assessmentRestService,
                                             ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator,
                                             ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator,
                                             ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator,
                                             UserService userService,
                                             ApplicantRestService applicantRestService,
                                             FormInputViewModelGenerator formInputViewModelGenerator,
                                             ApplicationTeamModelPopulator applicationTeamModelPopulator) {
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.processRoleService = processRoleService;
        this.organisationService = organisationService;
        this.formInputRestService = formInputRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.formInputResponseService = formInputResponseService;
        this.assessmentRestService = assessmentRestService;
        this.applicationFinanceSummaryViewModelPopulator = applicationFinanceSummaryViewModelPopulator;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
        this.applicationResearchParticipationViewModelPopulator = applicationResearchParticipationViewModelPopulator;
        this.userService = userService;
        this.applicantRestService = applicantRestService;
        this.formInputViewModelGenerator = formInputViewModelGenerator;
        this.applicationTeamModelPopulator = applicationTeamModelPopulator;
    }

    public SummaryViewModel populate (long applicationId, UserResource user, ApplicationForm form) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        List<SectionResource> allSections = sectionService.getAllByCompetitionId(competition.getId());
        List<SectionResource> parentSections = sectionService.filterParentSections(allSections);

        Map<Long, SectionResource> sections =
                parentSections.stream().collect(CollectionFunctions.toLinkedMap(SectionResource::getId,
                        Function.identity()));

        List<QuestionResource> questions = questionService.findByCompetition(competition.getId());

        Map<Long, List<QuestionResource>> sectionQuestions = parentSections.stream()
                .collect(Collectors.toMap(
                        SectionResource::getId,
                        s -> getQuestionsBySection(s.getQuestions(), questions)
                ));

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess();

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(user.getId(), userApplicationRoles);

        List<FormInputResource> formInputResources = formInputRestService.getByCompetitionIdAndScope(
                competition.getId(), APPLICATION).getSuccess();

        Map<Long, List<FormInputResource>> questionFormInputs = sectionQuestions.values().stream()
                .flatMap(a -> a.stream())
                .collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), formInputResources)));

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess();
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);

        final Map<Long, QuestionStatusResource> questionAssignees;
        if (userOrganisation.isPresent()) {
            questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.get().getId());
        } else {
            questionAssignees = Collections.emptyMap();
        }
        List<AssessmentResource> feedbackSummary = assessmentRestService
                .getByUserAndApplication(user.getId(), applicationId)
                .getSuccess();

        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModelPopulator.populate(applicationId, user);
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populate(applicationId);
        ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel = applicationResearchParticipationViewModelPopulator.populate(applicationId);

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection;
        final Long financeSectionId;
        if (financeSection == null) {
            hasFinanceSection = false;
            financeSectionId = null;
        } else {
            hasFinanceSection = true;
            financeSectionId = financeSection.getId();
        }

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());

        ApplicationOverviewCompletedViewModel completedViewModel = getCompletedDetails(application, userOrganisation);

        Map<Long, AbstractFormInputViewModel> formInputViewModels = sectionQuestions.values().stream().flatMap(List::stream)
                .map(question -> applicantRestService.getQuestion(user.getId(), application.getId(), question.getId()))
                .map(applicationQuestion -> formInputViewModelGenerator.fromQuestion(applicationQuestion, new ApplicationForm()))
                .flatMap(List::stream)
                .collect(Collectors.toMap(viewModel -> viewModel.getFormInput().getId(), Function.identity()));

        formInputViewModels.values().forEach(viewModel -> {
            viewModel.setClosed(!(competition.isOpen() && application.isOpen()));
            viewModel.setReadonly(true);
            viewModel.setSummary(true);
        });

        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);

        return new SummaryViewModel(
                application,
                sections,
                sectionQuestions,
                scores,
                questionFormInputs,
                mappedResponses,
                questionAssignees,
                feedbackSummary,
                hasFinanceSection,
                financeSectionId,
                applicationFinanceSummaryViewModel,
                applicationFundingBreakdownViewModel,
                applicationResearchParticipationViewModel,
                completedViewModel,
                formInputViewModels,
                true,
                applicationTeamModelPopulator.populateSummaryModel(applicationId, user.getId(), competition.getId())
        );
    }

    private ApplicationOverviewCompletedViewModel getCompletedDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = getCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
        boolean allQuestionsCompleted = sectionService.allSectionsMarkedAsComplete(application.getId());
        boolean userFinanceSectionCompleted = userOrganisation.map(org -> isUserFinanceSectionCompleted(application, org, completedSectionsByOrganisation)).orElse(false);

        ApplicationOverviewCompletedViewModel viewModel = new ApplicationOverviewCompletedViewModel(sectionsMarkedAsComplete, allQuestionsCompleted, markedAsComplete, userFinanceSectionCompleted);
        userOrganisation.ifPresent(org -> viewModel.setCompletedSections(completedSectionsByOrganisation.get(org.getId())));

        return viewModel;
    }

    private Set<Long> getCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }

    private boolean isUserFinanceSectionCompleted(ApplicationResource application, OrganisationResource userOrganisation, Map<Long, Set<Long>> completedSectionsByOrganisation) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .map(SectionResource::getId)
                .anyMatch(id -> completedSectionsByOrganisation.get(userOrganisation.getId()).contains(id));
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private Future<Set<Long>> getMarkedAsCompleteDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {

        Long organisationId = userOrganisation
                .map(OrganisationResource::getId)
                .orElse(0L);

        return questionService.getMarkedAsComplete(application.getId(), organisationId);
    }

    private List<FormInputResource> findFormInputByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> input.getQuestion().equals(id));
    }

}
