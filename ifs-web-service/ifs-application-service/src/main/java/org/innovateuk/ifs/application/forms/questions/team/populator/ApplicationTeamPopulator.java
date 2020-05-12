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
import org.innovateuk.ifs.invite.constant.InviteStatus;
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
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.sort;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;

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
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess()
                .stream()
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .collect(toList());
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(applicationId).getSuccess();
        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
        List<QuestionStatusResource> questionStatuses = questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId).getSuccess();

        boolean leadApplicant = processRoles.stream()
                .anyMatch(pr -> pr.getUser().equals(user.getId()) && pr.getRole().equals(LEADAPPLICANT));

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

        return new ApplicationTeamViewModel(applicationId, application.getName(), application.getCompetitionName(), questionId, organisationViewModels, user.getId(),
                leadApplicant,
                competition.getCollaborationLevel() == CollaborationLevel.SINGLE,
                application.isOpen() && competition.isOpen(),
                questionStatuses.stream().anyMatch(QuestionStatusResource::getMarkedAsComplete));
    }

    private ApplicationTeamOrganisationViewModel toInviteOrganisationTeamViewModel(InviteOrganisationResource organisationInvite, boolean leadApplicant) {
        List<ApplicationTeamRowViewModel> inviteRows = organisationInvite.getInviteResources().stream()
                .map(ApplicationTeamRowViewModel::fromInvite)
                .collect(toList());

        return new ApplicationTeamOrganisationViewModel(organisationInvite.getId(), organisationInvite.getId(), organisationInvite.getOrganisationName(), null, inviteRows, leadApplicant, false);
    }

    private ApplicationTeamOrganisationViewModel toOrganisationTeamViewModel(OrganisationResource organisation, Collection<ProcessRoleResource> processRoles, InviteOrganisationResource organisationInvite, boolean leadApplicant, UserResource user) {
        List<ApplicationTeamRowViewModel> userRows = processRoles.stream()
                .map(pr -> ApplicationTeamRowViewModel.fromProcessRole(pr, findInviteIdFromProcessRole(pr, organisationInvite)))
                .collect(toList());

        Optional<InviteOrganisationResource> maybeOrganisationInvite = ofNullable(organisationInvite);
        if (maybeOrganisationInvite.isPresent()) {
            userRows.addAll(organisationInvite.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus() == InviteStatus.SENT)
                    .map(ApplicationTeamRowViewModel::fromInvite)
                    .collect(toList()));
        }

        return new ApplicationTeamOrganisationViewModel(organisation.getId(),
                maybeOrganisationInvite.map(InviteOrganisationResource::getId).orElse(null),
                organisation.getName(),
                organisation.getOrganisationTypeName(),
                userRows,
                applicantCanEditRow(userRows, user, leadApplicant),
                true);
    }

    private boolean applicantCanEditRow(List<ApplicationTeamRowViewModel> userRows, UserResource user, boolean leadApplicant) {
        return leadApplicant || userRows.stream().anyMatch(row -> !row.isInvite() && row.getId().equals(user.getId()));
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
