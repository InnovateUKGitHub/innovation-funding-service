package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.overview.ApplicationOverviewData;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewRowViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.ASSESSED_QUESTION;


/**
 * view model for the application overview page
 */
@Component
public class ApplicationOverviewModelPopulator extends AsyncAdaptor {

    private final CompetitionRestService competitionRestService;
    private final SectionRestService sectionRestService;
    private final QuestionRestService questionRestService;
    private final ProcessRoleRestService processRoleRestService;
    private final MessageSource messageSource;
    private final OrganisationRestService organisationRestService;
    private final QuestionStatusRestService questionStatusRestService;
    private final SectionStatusRestService sectionStatusRestService;
    private final QuestionService questionService;
    private final ApplicationUrlHelper applicationUrlHelper;

    public ApplicationOverviewModelPopulator(AsyncFuturesGenerator asyncFuturesGenerator, CompetitionRestService competitionRestService,
                                             SectionRestService sectionRestService, QuestionRestService questionRestService,
                                             ProcessRoleRestService processRoleRestService, MessageSource messageSource,
                                             OrganisationRestService organisationRestService, QuestionStatusRestService questionStatusRestService,
                                             SectionStatusRestService sectionStatusRestService,
                                             QuestionService questionService,
                                             ApplicationUrlHelper applicationUrlHelper) {
        super(asyncFuturesGenerator);
        this.competitionRestService = competitionRestService;
        this.sectionRestService = sectionRestService;
        this.questionRestService = questionRestService;
        this.processRoleRestService = processRoleRestService;
        this.messageSource = messageSource;
        this.organisationRestService = organisationRestService;
        this.questionStatusRestService = questionStatusRestService;
        this.sectionStatusRestService = sectionStatusRestService;
        this.questionService = questionService;
        this.applicationUrlHelper = applicationUrlHelper;
    }

    public ApplicationOverviewViewModel populateModel(ApplicationResource application, UserResource user) {
        Future<OrganisationResource> organisation = async(() -> organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess());
        Future<CompetitionResource> competition = async(() -> competitionRestService.getCompetitionById(application.getCompetition()).getSuccess());
        Future<List<SectionResource>> sections = async(() -> sectionRestService.getByCompetition(application.getCompetition()).getSuccess());
        Future<List<QuestionResource>> questions = async(() -> questionRestService.findByCompetition(application.getCompetition()).getSuccess());
        Future<List<ProcessRoleResource>> processRoles = async(() -> processRoleRestService.findProcessRole(application.getId()).getSuccess());
        Future<List<QuestionStatusResource>> statuses = async(() -> questionStatusRestService.findByApplicationAndOrganisation(application.getId(), resolve(organisation).getId()).getSuccess());
        Future<List<Long>> completedSectionIds = async(() -> sectionStatusRestService.getCompletedSectionIds(application.getId(), resolve(organisation).getId()).getSuccess());
        Future<Map<Long, Set<Long>>> completedSectionsByOrganisation = async(() -> sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId()).getSuccess());

        async(() -> {
            List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(resolve(statuses), user.getId());
            questionService.removeNotifications(notifications);
        });

        ApplicationOverviewData data = new ApplicationOverviewData(resolve(competition), application, resolve(sections),
                resolve(questions), resolve(processRoles), resolve(organisation), resolve(statuses),
                resolve(completedSectionIds), resolve(completedSectionsByOrganisation), user);

        Set<ApplicationOverviewSectionViewModel> sectionViewModels = data.getSections()
                .values()
                .stream()
                .sorted(comparing(SectionResource::getPriority))
                .filter(section -> section.getParentSection() == null)
                .filter(section -> section.getType() != SectionType.KTP_ASSESSMENT)
                .map(section -> sectionViewModel(section, data))
                .collect(toCollection(LinkedHashSet::new));

        return new ApplicationOverviewViewModel(data.getUserProcessRole(), data.getCompetition(), application, sectionViewModels, application.hasBeenReopened(), application.getLastStateChangeDate());
    }

    private ApplicationOverviewSectionViewModel sectionViewModel(SectionResource section, ApplicationOverviewData data) {
        Set<ApplicationOverviewRowViewModel> rows;
        if (!section.getChildSections().isEmpty()) {
            rows = section.getChildSections()
                    .stream()
                    .map(data.getSections()::get)
                    .filter(childSection -> !(data.getCompetition().isFullyFunded() && childSection.getType().equals(OVERVIEW_FINANCES)))
                    .map(childSection ->
                            new ApplicationOverviewRowViewModel(
                                    childSection.getName(),
                                    format("/application/%d/form/section/%d", data.getApplication().getId(), childSection.getId()),
                                    data.getCompletedSectionIds().contains(childSection.getId()),
                                    true
                            )
                    )
                    .collect(toCollection(LinkedHashSet::new));
        } else {
            rows = section.getQuestions()
                    .stream()
                    .map(data.getQuestions()::get)
                    .map(question -> getApplicationOverviewRowViewModel(data, question, section))
                    .collect(toCollection(LinkedHashSet::new));
        }

        String subtitle = subtitle(data.getCompetition(), section);

        return new ApplicationOverviewSectionViewModel(section.getId(), section.getName(), subtitle, rows);
    }

    private String subtitle(CompetitionResource competition, SectionResource section) {

        String messageCode;

        switch (section.getType()) {
            case FINANCES:
                messageCode = getFinanceSectionSubTitle(competition);
                break;
            case PROJECT_DETAILS:
                if (competition.isKtp()) {
                    messageCode = "ifs.section.projectDetails.ktp.description";
                } else {
                    messageCode = "ifs.section.projectDetails.description";
                }
                break;
            case TERMS_AND_CONDITIONS:
                if (competition.isExpressionOfInterest()) {
                    messageCode = "ifs.section.termsAndConditionsEoi.description";
                } else {
                    messageCode = "ifs.section.termsAndConditions.description";
                }
                break;
            case APPLICATION_QUESTIONS:
                if (!competition.isKtp()) {
                    messageCode = "ifs.section.applicationQuestions.description";
                    break;
                }
                return null;
            default:
                return null;
        }

        return messageSource.getMessage(messageCode, null, Locale.getDefault());
    }

    private ApplicationOverviewRowViewModel getApplicationOverviewRowViewModel(ApplicationOverviewData data, QuestionResource question, SectionResource section) {
        boolean complete = section.isTermsAndConditions() ?
                isTermsAndConditionsComplete(data, question, section) :
                data.getStatuses().get(question.getId())
                        .stream()
                        .anyMatch(status -> TRUE.equals(status.getMarkedAsComplete()));

        boolean showStatus = !(section.isTermsAndConditions() && data.getCompetition().isExpressionOfInterest());

        return getAssignableViewModel(question, data)
                .map(avm ->
                        new ApplicationOverviewRowViewModel(
                                getQuestionTitle(question),
                                getRowUrlFromQuestion(question, data),
                                complete,
                                avm,
                                showStatus)
                ).orElse(
                        new ApplicationOverviewRowViewModel(
                                getQuestionTitle(question),
                                getRowUrlFromQuestion(question, data),
                                complete,
                                showStatus)
                );
    }

    private String getRowUrlFromQuestion(QuestionResource question, ApplicationOverviewData data) {
        return applicationUrlHelper.getQuestionUrl(question.getQuestionSetupType(), question.getId(), data.getApplication().getId(), data.getOrganisation().getId())
                .orElse(format("/application/%d/form/question/%d", data.getApplication().getId(), question.getId()));
    }

    private static boolean isTermsAndConditionsComplete(ApplicationOverviewData data, QuestionResource question, SectionResource section) {
        boolean completeForOrganisation = data.getStatuses().get(question.getId())
                .stream()
                .anyMatch(status -> status.getMarkedAsComplete() != null && status.getMarkedAsComplete());

        boolean leadOrganisation = data.getLeadApplicant().getOrganisationId().equals(data.getOrganisation().getId());

        boolean completeForAll = data.getCompletedSectionsByOrganisation()
                .values()
                .stream()
                .allMatch(completedSections -> completedSections.contains(section.getId()));

        return !leadOrganisation && completeForOrganisation || completeForAll;
    }

    private static Optional<AssignButtonsViewModel> getAssignableViewModel(QuestionResource question, ApplicationOverviewData data) {
        if (!question.isAssignEnabled()) {
            return Optional.empty();
        } else {
            AssignButtonsViewModel viewModel = new AssignButtonsViewModel();
            Optional<QuestionStatusResource> maybeStatus = data.getStatuses().get(question.getId())
                    .stream()
                    .filter(status -> status.getAssignee() != null)
                    .findFirst();

            viewModel.setCurrentApplicant(data.getUserProcessRole());
            viewModel.setLeadApplicant(data.getLeadApplicant());
            viewModel.setAssignableApplicants(new ArrayList<>(data.getProcessRoles().values()));
            viewModel.setQuestion(question);

            viewModel.setAssignedBy(maybeStatus.map(status -> data.getProcessRoles().get(status.getAssignedBy())).orElse(null));
            viewModel.setAssignee(maybeStatus.map(status -> data.getProcessRoles().get(status.getAssignee())).orElse(null));
            return Optional.of(viewModel);
        }
    }

    private static String getQuestionTitle(QuestionResource question) {
        return question.getQuestionSetupType() == ASSESSED_QUESTION ?
                format("%s. %s", question.getQuestionNumber(), question.getShortName()) :
                question.getShortName();
    }

    private String getFinanceSectionSubTitle(CompetitionResource competition) {
        if (competition.isFullyFunded()) {
            return "ifs.section.finances.fullyFunded.description";
        } else if (competition.getCollaborationLevel() == SINGLE) {
            return "ifs.section.finances.description";
        } else {
            return "ifs.section.finances.collaborative.description";
        }
    }

}
