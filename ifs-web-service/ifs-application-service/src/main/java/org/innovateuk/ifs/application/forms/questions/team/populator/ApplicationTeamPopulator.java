package org.innovateuk.ifs.application.forms.questions.team.populator;

import com.google.common.collect.Multimap;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamOrganisationViewModel;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamRowViewModel;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;

@Component
public class ApplicationTeamPopulator {

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ApplicationTeamViewModel populate(long applicationId, long questionId, UserResource user) {
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(applicationId).getSuccess();
        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
        List<QuestionStatusResource> questionStatuses = questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId).getSuccess();

        boolean leadApplicant = processRoles.stream()
                .filter(pr -> pr.getRole().equals(LEADAPPLICANT))
                .findAny()
                .map(pr -> pr.getUser().equals(user.getId()))
                .orElse(false);

        Multimap<Long, ProcessRoleResource> organisationToProcessRole = index(processRoles, ProcessRoleResource::getOrganisationId);
        Map<Long, InviteOrganisationResource> organisationToInvite = inviteOrganisationResources.stream()
                .filter(organisationInvite -> organisationInvite.getOrganisation() != null)
                .collect(toMap(InviteOrganisationResource::getOrganisation, Function.identity()));

        List<ApplicationTeamOrganisationViewModel> organisationViewModels = organisations.stream()
                .map(organisation -> toOrganisationTeamViewModel(organisation, organisationToProcessRole.get(organisation.getId()), organisationToInvite.get(organisation.getId()), leadApplicant, user))
                .collect(toList());

        organisationViewModels.addAll(inviteOrganisationResources.stream()
            .filter(invite -> invite.getOrganisation() == null)
            .map(invite -> toInviteOrganisationTeamViewModel(invite, leadApplicant))
            .collect(toList()));

        sort(organisationViewModels);

        return new ApplicationTeamViewModel(applicationId, application.getName(), questionId, organisationViewModels, user.getId(),
                leadApplicant,
                competition.getCollaborationLevel() == CollaborationLevel.SINGLE,
                application.isOpen() && competition.isOpen(),
                questionStatuses.stream().anyMatch(QuestionStatusResource::getMarkedAsComplete));
    }

    private ApplicationTeamOrganisationViewModel toInviteOrganisationTeamViewModel(InviteOrganisationResource organisationInvite, boolean leadApplicant) {
        List<ApplicationTeamRowViewModel> inviteRows = organisationInvite.getInviteResources().stream()
                .map(ApplicationTeamRowViewModel::fromInvite)
                .collect(toList());

        return new ApplicationTeamOrganisationViewModel(organisationInvite.getId(), organisationInvite.getOrganisationName(), inviteRows, leadApplicant, false);
    }

    private ApplicationTeamOrganisationViewModel toOrganisationTeamViewModel(OrganisationResource organisation, Collection<ProcessRoleResource> processRoles, InviteOrganisationResource organisationInvite, boolean leadApplicant, UserResource user) {
        List<ApplicationTeamRowViewModel> userRows = processRoles.stream()
                .map(pr -> ApplicationTeamRowViewModel.fromProcessRole(pr, findInviteIdFromProcessRole(pr, organisationInvite)))
                .collect(toList());

        if (organisationInvite != null) {
            userRows.addAll(organisationInvite.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getUser() == null)
                    .map(ApplicationTeamRowViewModel::fromInvite)
                    .collect(toList()));
        }

        return new ApplicationTeamOrganisationViewModel(organisation.getId(), organisation.getName(), userRows,
                leadApplicant || userRows.stream().anyMatch(row -> !row.isInvite() && row.getId().equals(user.getId())),
                true);
    }

    private Long findInviteIdFromProcessRole(ProcessRoleResource pr, InviteOrganisationResource organisationInvite) {
        if (pr.getRole().isLeadApplicant()) {
            return null;
        }
        return organisationInvite.getInviteResources()
                .stream()
                .filter(invite -> pr.getUser().equals(invite.getUser()))
                .findAny()
                .map(ApplicationInviteResource::getId)
                .orElse(null);
    }
}
