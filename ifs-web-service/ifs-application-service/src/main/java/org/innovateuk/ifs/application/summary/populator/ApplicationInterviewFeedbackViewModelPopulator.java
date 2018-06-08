package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationInterviewFeedbackViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationInterviewFeedbackViewModelPopulator {

    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private UserService userService;
    private OrganisationService organisationService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private OrganisationRestService organisationRestService;
    private SectionService sectionService;
    private QuestionService questionService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private AssessmentRestService assessmentRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private InterviewResponseRestService interviewResponseRestService;
    private ProcessRoleService processRoleService;

    public ApplicationInterviewFeedbackViewModelPopulator(ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator,
                                                          ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator,
                                                          ApplicationService applicationService,
                                                          CompetitionService competitionService,
                                                          UserService userService,
                                                          OrganisationService organisationService,
                                                          OrganisationRestService organisationRestService,
                                                          FinanceService financeService,
                                                          FileEntryRestService fileEntryRestService,
                                                          SectionService sectionService,
                                                          QuestionService questionService,
                                                          AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                                          InterviewAssignmentRestService interviewAssignmentRestService,
                                                          InterviewResponseRestService interviewResponseRestService,
                                                          ProcessRoleService processRoleService,
                                                          AssessmentRestService assessmentRestService) {
        this.applicationFinanceSummaryViewModelPopulator = applicationFinanceSummaryViewModelPopulator;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.userService = userService;
        this.organisationRestService = organisationRestService;
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationService = organisationService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.assessmentRestService = assessmentRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.interviewResponseRestService = interviewResponseRestService;
        this.processRoleService = processRoleService;
    }

    public ApplicationInterviewFeedbackViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        boolean feedbackReleased = competition.getCompetitionStatus().isFeedbackReleased();

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModelPopulator.populate(applicationId, user);
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populate(applicationId);

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        BigDecimal totalFundingSought = organisationFinanceOverview.getTotalFundingSought();

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

        boolean isLeadApplicant = processRoleService.findProcessRole(user.getId(), applicationId).getRole().equals(Role.LEADAPPLICANT);

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess();
        List<String> feedback = assessmentRestService.getApplicationFeedback(applicationId).getSuccess().getFeedback();

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

        String feedbackFilename = ofNullable(interviewAssignmentRestService.findFeedback(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        String responseFilename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        return new ApplicationInterviewFeedbackViewModel(
                application,
                competition,
                leadOrganisation,
                applicationOrganisations,
                totalFundingSought,
                sections,
                sectionQuestions,
                scores,
                feedback,
                hasFinanceSection,
                feedbackFilename,
                responseFilename,
                isLeadApplicant,
                feedbackReleased,
                applicationFinanceSummaryViewModel,
                applicationFundingBreakdownViewModel
                );
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

}
