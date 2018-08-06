package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.populator.section.FinanceOverviewSectionPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

@Component
public class SummaryViewModelFragmentPopulator extends FinanceOverviewSectionPopulator.AbstractApplicationModelPopulator {

    private ApplicationService applicationService;
    private CompetitionRestService competitionRestService;
    private SectionService sectionService;
    private QuestionService questionService;
    private UserRestService userRestService;
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
    private UserService userService;
    private ApplicationRestService applicationRestService;

    public SummaryViewModelFragmentPopulator(ApplicationService applicationService,
                                             CompetitionRestService competitionRestService,
                                             SectionService sectionService,
                                             QuestionService questionService,
                                             UserRestService userRestService,
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
                                             UserService userService,
                                             ApplicationRestService applicationRestService,
                                             QuestionRestService questionRestService) {
        super(sectionService, questionService, questionRestService);
        this.applicationService = applicationService;
        this.competitionRestService = competitionRestService;
        this.sectionService = sectionService;
        this.questionService = questionService;
        this.userRestService = userRestService;
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
        this.userService = userService;
        this.applicationRestService = applicationRestService;
    }

    public SummaryViewModel populate (long applicationId, UserResource user, ApplicationForm form) {

        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        Map<Long, List<QuestionResource>> sectionQuestions = getSectionQuestions(competition.getId());

        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(user.getId(), userApplicationRoles);

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

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection = financeSection != null;
        Long financeSectionId = null;
        if (hasFinanceSection) {
            financeSectionId = financeSection.getId();
        }

        //Comp admin user doesn't have user organisation
        long applicantId;
        if (!userOrganisation.isPresent())  {
            ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application.getId());
            applicantId = leadApplicantProcessRole.getUser();
            userOrganisation = organisationService.getOrganisationForUser(applicantId, userApplicationRoles);
        } else {
            applicantId = user.getId();
        }

        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);

        boolean showApplicationTeamLink = applicationRestService.showApplicationTeam(application.getId(), user.getId()).getSuccess();

        return new SummaryViewModel(
                application,
                getSections(competition.getId()),
                sectionQuestions,
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
                getFormInputViewModel(sectionQuestions, applicantId, application, competition),
                showApplicationTeamLink
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
