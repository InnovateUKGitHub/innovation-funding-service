package org.innovateuk.ifs.application.feedback.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.feedback.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryOrigin;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.common.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.feedback.viewmodel.ApplicationFeedbackViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.util.MapFunctions.toMap;

@Component
public class ApplicationFeedbackViewModelPopulator {

    private OrganisationRestService organisationRestService;
    private OrganisationService organisationService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private UserService userService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    private AssessmentRestService assessmentRestService;
    private SectionService sectionService;
    private QuestionService questionService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;
    private ProjectService projectService;

    public ApplicationFeedbackViewModelPopulator(OrganisationRestService organisationRestService,
                                                 ApplicationService applicationService,
                                                 CompetitionService competitionService,
                                                 OrganisationService organisationService,
                                                 UserService userService,
                                                 FileEntryRestService fileEntryRestService,
                                                 FinanceService financeService,
                                                 AssessmentRestService assessmentRestService,
                                                 SectionService sectionService,
                                                 QuestionService questionService,
                                                 AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                                 ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator,
                                                 ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator,
                                                 InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator,
                                                 InterviewAssignmentRestService interviewAssignmentRestService,
                                                 ProjectService projectService) {
        this.organisationRestService = organisationRestService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.organisationService = organisationService;
        this.userService = userService;
        this.fileEntryRestService = fileEntryRestService;
        this.financeService = financeService;
        this.assessmentRestService = assessmentRestService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.applicationFinanceSummaryViewModelPopulator = applicationFinanceSummaryViewModelPopulator;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
        this.interviewFeedbackViewModelPopulator = interviewFeedbackViewModelPopulator;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.projectService = projectService;
    }

    public ApplicationFeedbackViewModel populate(long applicationId, UserResource user, MultiValueMap<String, String> queryParams, String origin) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());
        List<OrganisationResource> partners = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();


        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        BigDecimal totalFundingSought = organisationFinanceOverview.getTotalFundingSought();

        List<String> feedback = assessmentRestService.getApplicationFeedback(applicationId).getSuccess().getFeedback();

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection;
        if (financeSection == null) {
            hasFinanceSection = false;
        } else {
            hasFinanceSection = true;
        }

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

        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModelPopulator.populate(applicationId, user);
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populate(applicationId);

        InterviewFeedbackViewModel interviewFeedbackViewModel;
        if (interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess()) {
            interviewFeedbackViewModel = interviewFeedbackViewModelPopulator.populate(applicationId, user, competition.getCompetitionStatus().isFeedbackReleased());
        } else {
            interviewFeedbackViewModel = null;
        }

        ProjectResource project = projectService.getByApplicationId(applicationId);
        boolean projectWithdrawn = (project != null && project.isWithdrawn());

        return new ApplicationFeedbackViewModel(
                application,
                competition,
                leadOrganisation,
                partners,
                totalFundingSought,
                feedback,
                hasFinanceSection,
                sections,
                sectionQuestions,
                scores,
                applicationFinanceSummaryViewModel,
                applicationFundingBreakdownViewModel,
                interviewFeedbackViewModel,
                projectWithdrawn,
                ApplicationSummaryOrigin.valueOf(origin),
                buildBackUrl(origin, queryParams)
        );
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private String buildBackUrl(String origin, MultiValueMap<String, String> queryParams) {
        String baseUrl = ApplicationSummaryOrigin.valueOf(origin).getOriginUrl();
        queryParams.remove("origin");

        if (queryParams.containsKey("applicationId")) {
            queryParams.remove("applicationId");
        }

        String competitionId = getSingleValue(queryParams, "competitionId");
        String projectId = getSingleValue(queryParams, "projectId");

        return UriComponentsBuilder.fromPath(baseUrl)
                .queryParams(queryParams)
                .buildAndExpand(asMap(
                        "competitionId", competitionId,
                        "projectId", projectId
                ))
                .encode()
                .toUriString();
    }

    private String getSingleValue(MultiValueMap<String, String> queryParams, String key) {
        List<String> value = queryParams.get(key);
        if (value != null && value.size() == 1) {
            return value.get(0);
        }
        return null;
    }

    private Map<String, String> handleParameters(MultiValueMap<String, String> queryParams, String... keys) {
        return Arrays.stream(keys)
                .filter(queryParams::containsKey)
                .peek(queryParams::remove)
                .collect(Collectors.toMap(Function::identity, key -> getSingleValue(queryParams, key)));

    }
}
