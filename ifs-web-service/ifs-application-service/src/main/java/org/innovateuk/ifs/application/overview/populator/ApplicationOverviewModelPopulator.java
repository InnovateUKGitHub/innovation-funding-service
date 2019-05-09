package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.overview.ApplicationOverviewData;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewRowViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toCollection;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.ASSESSED_QUESTION;


/**
 * view model for the application overview page
 */
@Component
public class ApplicationOverviewModelPopulator extends AsyncAdaptor {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private QuestionService questionService;

    public ApplicationOverviewViewModel populateModel(ApplicationResource application, UserResource user) {
        Future<OrganisationResource> organisation = async(() -> organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess());
        Future<CompetitionResource> competition = async(() -> competitionRestService.getCompetitionById(application.getCompetition()).getSuccess());
        Future<List<SectionResource>> sections = async(() -> sectionRestService.getByCompetition(application.getCompetition()).getSuccess());
        Future<List<QuestionResource>> questions = async(() -> questionRestService.findByCompetition(application.getCompetition()).getSuccess());
        Future<List<ProcessRoleResource>> processRoles = async(() -> userRestService.findProcessRole(application.getId()).getSuccess());
        Future<List<QuestionStatusResource>> statuses = async(() -> questionStatusRestService.findByApplicationAndOrganisation(application.getId(), resolve(organisation).getId()).getSuccess());
        Future<List<ApplicationInviteResource>> invites = async(() -> inviteService.getPendingInvitationsByApplicationId(application.getId()));
        Future<List<Long>> completedSectionIds = async(() -> sectionStatusRestService.getCompletedSectionIds(application.getId(), resolve(organisation).getId()).getSuccess());

        async(() -> {
            List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(resolve(statuses), user.getId());
            questionService.removeNotifications(notifications);
        });

        ApplicationOverviewData data = new ApplicationOverviewData(resolve(competition), application, resolve(sections), resolve(questions), resolve(processRoles), resolve(organisation), resolve(statuses), resolve(invites), resolve(completedSectionIds), user);

        Set<ApplicationOverviewSectionViewModel> sectionViewModels = data.getSections()
                .values()
                .stream()
                .filter(section -> section.getParentSection() == null)
                .map(section -> sectionViewModel(section, data))
                .collect(toCollection(LinkedHashSet::new));

        return new ApplicationOverviewViewModel(data.getUserProcessRole(), data.getCompetition(), application, sectionViewModels);
    }

    private ApplicationOverviewSectionViewModel sectionViewModel(SectionResource section, ApplicationOverviewData data) {
        Set<ApplicationOverviewRowViewModel> rows;
        if (!section.getChildSections().isEmpty()) {
            rows = section.getChildSections()
                    .stream()
                    .map(data.getSections()::get)
                    .map(childSection -> new ApplicationOverviewRowViewModel(childSection.getName(),
                            String.format("/application/%d/form/section/%d", data.getApplication().getId(), childSection.getId()),
                            data.getCompletedSectionIds().contains(childSection.getId()),
                            Optional.empty())
                    )
                    .collect(toCollection(LinkedHashSet::new));
        } else {
            rows = section.getQuestions()
                    .stream()
                    .map(data.getQuestions()::get)
                    .map(question -> new ApplicationOverviewRowViewModel(getQuestionTitle(question),
                            String.format("/application/%d/form/question/%d", data.getApplication().getId(), question.getId()),
                            data.getStatuses().get(question.getId()).stream().anyMatch(status -> status.getMarkedAsComplete() != null && status.getMarkedAsComplete()),
                            getAssignableViewModel(question, data))
                    )
                    .collect(toCollection(LinkedHashSet::new));
        }
        return new ApplicationOverviewSectionViewModel(section.getId(), section.getName(),
                section.getName().equals("Finances") ? getFinanceSectionSubTitle(data.getCompetition()) : section.getDescription(),
                rows);
    }

    private Optional<AssignButtonsViewModel> getAssignableViewModel(QuestionResource question, ApplicationOverviewData data) {
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
            viewModel.setPendingAssignableUsers(data.getInvites());
            viewModel.setQuestion(question);

            viewModel.setAssignedBy(maybeStatus.map(status -> data.getProcessRoles().get(status.getAssignedBy())).orElse(null));
            viewModel.setAssignee(maybeStatus.map(status -> data.getProcessRoles().get(status.getAssignee())).orElse(null));
            return Optional.of(viewModel);
        }
    }

    private String getQuestionTitle(QuestionResource question) {
        return question.getQuestionSetupType() == ASSESSED_QUESTION ?
                String.format("%s. %s", question.getQuestionNumber(), question.getShortName()) :
                question.getShortName();
    }

    private String getFinanceSectionSubTitle(CompetitionResource competition) {
        if (competition.isFullyFunded()) {
            return "Submit your organisation's project finances.";
        } else if (competition.getCollaborationLevel() == SINGLE) {
            return messageSource.getMessage("ifs.section.finances.description", null, Locale.getDefault());
        } else {
            return messageSource.getMessage("ifs.section.finances.collaborative.description", null, Locale.getDefault());
        }
    }

}
