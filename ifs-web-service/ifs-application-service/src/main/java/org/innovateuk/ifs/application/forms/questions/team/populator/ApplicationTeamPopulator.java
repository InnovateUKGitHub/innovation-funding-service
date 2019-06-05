package org.innovateuk.ifs.application.forms.questions.team.populator;

import com.google.common.collect.Multimap;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamOrganisationViewModel;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamRowViewModel;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.application.service.ApplicationService;
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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;

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

    public ApplicationTeamViewModel populate(long applicationId, long questionId, UserResource user) {
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();
        List<InviteOrganisationResource> inviteOrganisationResources = inviteRestService.getInvitesByApplication(applicationId).getSuccess();
        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();

        Multimap<Long, ProcessRoleResource> organisationToProcessRole = index(processRoles, ProcessRoleResource::getOrganisationId);
        Map<Long, InviteOrganisationResource> organisationToInvite = inviteOrganisationResources.stream()
                .filter(organisationInvite -> organisationInvite.getOrganisation() != null)
                .collect(toMap(InviteOrganisationResource::getOrganisation, Function.identity()));

        List<ApplicationTeamOrganisationViewModel> organisationViewModels = organisations.stream()
                .map(organisation -> toOrganisationTeamViewModel(organisation, organisationToProcessRole.get(organisation.getId()), organisationToInvite.get(organisation.getId())))
                .collect(toList());

        organisationViewModels.addAll(inviteOrganisationResources.stream()
            .filter(invite -> invite.getOrganisation() == null)
            .map(this::toInviteOrganisationTeamViewModel)
            .collect(toList()));

        return new ApplicationTeamViewModel(applicationId, "blah", questionId, organisationViewModels, user.getId(), false, false);
    }

    private ApplicationTeamOrganisationViewModel toInviteOrganisationTeamViewModel(InviteOrganisationResource organisationInvite) {
        List<ApplicationTeamRowViewModel> inviteRows = organisationInvite.getInviteResources().stream()
                .map(invite -> new ApplicationTeamRowViewModel(invite.getId(), invite.getName(), invite.getEmail(), false, true, invite.getId()))
                .collect(toList());

        return new ApplicationTeamOrganisationViewModel(organisationInvite.getId(), organisationInvite.getOrganisationName(), inviteRows, true, false);
    }

    private ApplicationTeamOrganisationViewModel toOrganisationTeamViewModel(OrganisationResource organisation, Collection<ProcessRoleResource> processRoles, InviteOrganisationResource organisationInvite) {
        List<ApplicationTeamRowViewModel> userRows = processRoles.stream()
                .map(pr -> new ApplicationTeamRowViewModel(pr.getUser(), pr.getUserName(), pr.getUserEmail(), pr.getRole().isLeadApplicant(), false, findInviteIdFromProcessRole(pr, organisationInvite)))
                .collect(toList());

        if (organisationInvite != null) {
            userRows.addAll(organisationInvite.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus().equals(SENT))
                    .map(invite -> new ApplicationTeamRowViewModel(invite.getId(), invite.getName(), invite.getEmail(), false, true, invite.getId()))
                    .collect(toList()));
        }

        return new ApplicationTeamOrganisationViewModel(organisation.getId(), organisation.getName(), userRows, true, true);
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
