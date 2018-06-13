package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFeedbackSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class ApplicationFeedbackSummaryViewModelPopulator {

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

    public ApplicationFeedbackSummaryViewModelPopulator(OrganisationRestService organisationRestService,
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
                                                        ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator) {
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
    }

    public ApplicationFeedbackSummaryViewModel populate(long applicationId, UserResource user, String backUrl, String origin) {

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
        final Long financeSectionId;
        if (financeSection == null) {
            hasFinanceSection = false;
            financeSectionId = null;
        } else {
            hasFinanceSection = true;
            financeSectionId = financeSection.getId();
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

        return new ApplicationFeedbackSummaryViewModel(
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
                backUrl,
                origin
        );
    }

    private List<QuestionResource> getQuestionsBySection(final List<Long> questionIds, final List<QuestionResource> questions) {
        return simpleFilter(questions, q -> questionIds.contains(q.getId()));
    }
}
