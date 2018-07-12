package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
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
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class SummaryViewModelPopulator extends AbstractApplicationModelPopulator {

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
    private ApplicantRestService applicantRestService;
    private FormInputViewModelGenerator formInputViewModelGenerator;
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    public SummaryViewModelPopulator(ApplicationService applicationService,
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
                                     ApplicantRestService applicantRestService,
                                     FormInputViewModelGenerator formInputViewModelGenerator,
                                     ApplicationTeamModelPopulator applicationTeamModelPopulator) {
        super(sectionService, questionService);
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
        this.applicantRestService = applicantRestService;
        this.formInputViewModelGenerator = formInputViewModelGenerator;
        this.applicationTeamModelPopulator = applicationTeamModelPopulator;
    }

    public SummaryViewModel populate (long applicationId, UserResource user, ApplicationForm form) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        Map<Long, List<QuestionResource>> sectionQuestions = getSectionQuestions(competition.getId());
        ApplicationAssessmentAggregateResource scores = assessorFormInputResponseRestService.getApplicationAssessmentAggregate(applicationId).getSuccess();

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(user.getId(), userApplicationRoles);

        List<FormInputResponseResource> responses = formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess();
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);

        Map<Long, QuestionStatusResource> questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.get().getId());

        List<AssessmentResource> feedbackSummary = assessmentRestService
                .getByUserAndApplication(user.getId(), applicationId)
                .getSuccess();

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection = financeSection != null;
        Long financeSectionId = null;
        if (hasFinanceSection) {
            financeSectionId = financeSection.getId();
        }

        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);

        return new SummaryViewModel(
                application,
                getSections(competition.getId()),
                sectionQuestions,
                scores,
                getQuestionFormInputs(sectionQuestions, competition.getId()),
                mappedResponses,
                questionAssignees,
                feedbackSummary,
                hasFinanceSection,
                financeSectionId,
                applicationFinanceSummaryViewModelPopulator.populate(applicationId, user),
                applicationFundingBreakdownViewModelPopulator.populate(applicationId),
                applicationResearchParticipationViewModelPopulator.populate(applicationId),
                getCompletedDetails(application, userOrganisation),
                getFormInputViewModel(sectionQuestions, user.getId(), application, competition),
                true,
                applicationTeamModelPopulator.populateSummaryModel(applicationId, user.getId(), competition.getId())
        );
    }

    private List<FormInputResource> findFormInputByQuestion(final Long id, final List<FormInputResource> list) {
        return simpleFilter(list, input -> input.getQuestion().equals(id));
    }

    private Map<Long, AbstractFormInputViewModel> getFormInputViewModel(Map<Long, List<QuestionResource>> sectionQuestions,
                                                                        Long userId,
                                                                        ApplicationResource application,
                                                                        CompetitionResource competition) {
        Map<Long, AbstractFormInputViewModel> formInputViewModels = sectionQuestions.values().stream().flatMap(List::stream)
                .map(question -> applicantRestService.getQuestion(userId, application.getId(), question.getId()))
                .map(applicationQuestion -> formInputViewModelGenerator.fromQuestion(applicationQuestion, new ApplicationForm()))
                .flatMap(List::stream)
                .collect(Collectors.toMap(viewModel -> viewModel.getFormInput().getId(), Function.identity()));

        formInputViewModels.values().forEach(viewModel -> {
            viewModel.setClosed(!(competition.isOpen() && application.isOpen()));
            viewModel.setReadonly(true);
            viewModel.setSummary(true);
        });

        return formInputViewModels;
    }

    private Map<Long, List<FormInputResource>> getQuestionFormInputs(Map<Long, List<QuestionResource>> sectionQuestions,
                                                                     Long competitionId) {
        List<FormInputResource> formInputResources = formInputRestService.getByCompetitionIdAndScope(
                competitionId, APPLICATION).getSuccess();

        return sectionQuestions.values().stream()
                .flatMap(a -> a.stream())
                .collect(Collectors.toMap(q -> q.getId(), k -> findFormInputByQuestion(k.getId(), formInputResources)));
    }
}
