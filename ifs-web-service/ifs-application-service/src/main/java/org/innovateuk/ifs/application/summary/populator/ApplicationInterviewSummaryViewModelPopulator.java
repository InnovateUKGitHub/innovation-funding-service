package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationInterviewSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationInterviewSummaryViewModelPopulator {

    private InterviewResponseRestService interviewResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private ApplicationService applicationService;
    private CompetitionService competitionService;
    private UserService userService;
    private OrganisationService organisationService;
    private OrganisationRestService organisationRestService;
    private FormInputResponseRestService formInputResponseRestService;
    private FormInputResponseService formInputResponseService;
    private SectionService sectionService;
    private QuestionService questionService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private AssessmentRestService assessmentRestService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    public ApplicationInterviewSummaryViewModelPopulator(InterviewResponseRestService interviewResponseRestService,
                                                         InterviewAssignmentRestService interviewAssignmentRestService,
                                                         ApplicationService applicationService,
                                                         CompetitionService competitionService,
                                                         UserService userService,
                                                         OrganisationService organisationService,
                                                         OrganisationRestService organisationRestService,
                                                         FormInputResponseRestService formInputResponseRestService,
                                                         FormInputResponseService formInputResponseService,
                                                         SectionService sectionService,
                                                         QuestionService questionService,
                                                         AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                                         AssessmentRestService assessmentRestService,
                                                         FinanceService financeService,
                                                         FileEntryRestService fileEntryRestService,
                                                         ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator,
                                                         ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator
    ) {
        this.interviewResponseRestService = interviewResponseRestService;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.applicationService = applicationService;
        this.competitionService = competitionService;
        this.userService = userService;
        this.organisationService = organisationService;
        this.organisationRestService = organisationRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.formInputResponseService = formInputResponseService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.assessmentRestService = assessmentRestService;
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.applicationFinanceSummaryViewModelPopulator = applicationFinanceSummaryViewModelPopulator;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
    }

    public ApplicationInterviewSummaryViewModel populate(long applicationId) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        String responseFilename = ofNullable(interviewResponseRestService.findResponse(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        String feedbackFilename = ofNullable(interviewAssignmentRestService.findFeedback(applicationId).getSuccess())
                .map(FileEntryResource::getName)
                .orElse(null);

        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());
        List<OrganisationResource> partners = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess();
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);

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

        List<SectionResource> eachOrganisationFinanceSections = sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE);
        Long eachCollaboratorFinanceSectionId = getEachCollaboratorFinanceSectionId(eachOrganisationFinanceSections);

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        BigDecimal totalFundingSought = organisationFinanceOverview.getTotalFundingSought();


        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModelPopulator.populate(applicationId);
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populate(applicationId);

        return new ApplicationInterviewSummaryViewModel(
                application,
                competition,
                responseFilename,
                feedbackFilename,
                leadOrganisation,
                partners,
                mappedResponses,
                sections,
                sectionQuestions,
                scores,
                feedback,
                hasFinanceSection,
                financeSectionId,
                eachCollaboratorFinanceSectionId,
                totalFundingSought,
                applicationFinanceSummaryViewModel,
                applicationFundingBreakdownViewModel
        );
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }

    private Long getEachCollaboratorFinanceSectionId(List<SectionResource> eachOrganisationFinanceSections) {
        if (!eachOrganisationFinanceSections.isEmpty()) {
            return eachOrganisationFinanceSections.get(0).getId();
        }

        return null;
    }
}
