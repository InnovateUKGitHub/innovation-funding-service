package org.innovateuk.ifs.application.feedback.populator;

import org.innovateuk.ifs.application.feedback.viewmodel.ApplicationFeedbackViewModel;
import org.innovateuk.ifs.application.feedback.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.populator.section.AbstractApplicationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationFeedbackViewModelPopulator extends AbstractApplicationModelPopulator {

    private OrganisationRestService organisationRestService;
    private ApplicationService applicationService;
    private CompetitionRestService competitionRestService;
    private OrganisationService organisationService;
    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;
    private AssessmentRestService assessmentRestService;
    private SectionService sectionService;
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;
    private InterviewAssignmentRestService interviewAssignmentRestService;
    private InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator;
    private ProjectService projectService;

    public ApplicationFeedbackViewModelPopulator(OrganisationRestService organisationRestService,
                                                 ApplicationService applicationService,
                                                 CompetitionRestService competitionRestService,
                                                 OrganisationService organisationService,
                                                 FileEntryRestService fileEntryRestService,
                                                 FinanceService financeService,
                                                 AssessmentRestService assessmentRestService,
                                                 SectionService sectionService,
                                                 QuestionRestService questionRestService,
                                                 AssessorFormInputResponseRestService assessorFormInputResponseRestService,
                                                 ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator,
                                                 ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator,
                                                 InterviewFeedbackViewModelPopulator interviewFeedbackViewModelPopulator,
                                                 InterviewAssignmentRestService interviewAssignmentRestService,
                                                 ProjectService projectService) {
        super(sectionService, questionRestService);
        this.organisationRestService = organisationRestService;
        this.applicationService = applicationService;
        this.competitionRestService = competitionRestService;
        this.organisationService = organisationService;
        this.fileEntryRestService = fileEntryRestService;
        this.financeService = financeService;
        this.assessmentRestService = assessmentRestService;
        this.sectionService = sectionService;
        this.assessorFormInputResponseRestService = assessorFormInputResponseRestService;
        this.applicationFinanceSummaryViewModelPopulator = applicationFinanceSummaryViewModelPopulator;
        this.applicationFundingBreakdownViewModelPopulator = applicationFundingBreakdownViewModelPopulator;
        this.interviewFeedbackViewModelPopulator = interviewFeedbackViewModelPopulator;
        this.interviewAssignmentRestService = interviewAssignmentRestService;
        this.projectService = projectService;
    }

    public ApplicationFeedbackViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        List<OrganisationResource> partners = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
        OrganisationResource leadOrganisation = organisationService.getLeadOrganisation(application.getId(), partners);

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );

        List<String> feedback = assessmentRestService.getApplicationFeedback(applicationId).getSuccess().getFeedback();

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection = financeSection != null;

        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess();

        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModelPopulator.populate(applicationId, user);
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModelPopulator.populate(applicationId, user);

        long applicationTermsQuestion = sectionService.getTermsAndConditionsSection(application.getCompetition()).getQuestions().get(0);

        final InterviewFeedbackViewModel interviewFeedbackViewModel;
        if (interviewAssignmentRestService.isAssignedToInterview(applicationId).getSuccess()) {
            interviewFeedbackViewModel = interviewFeedbackViewModelPopulator.populate(applicationId, application.getCompetitionName(), user, application.getCompetitionStatus().isFeedbackReleased());
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
                organisationFinanceOverview.getTotalFundingSought(),
                feedback,
                hasFinanceSection,
                getSections(application.getCompetition()),
                getSectionQuestions(application.getCompetition()),
                scores,
                applicationFinanceSummaryViewModel,
                applicationFundingBreakdownViewModel,
                interviewFeedbackViewModel,
                applicationTermsQuestion,
                projectWithdrawn,
                application.isCollaborativeProject()
        );
    }
}