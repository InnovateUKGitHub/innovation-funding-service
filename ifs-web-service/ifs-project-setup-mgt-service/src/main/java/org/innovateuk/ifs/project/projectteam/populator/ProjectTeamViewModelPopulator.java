package org.innovateuk.ifs.project.projectteam.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.projectteam.viewmodel.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

@Component
public class ProjectTeamViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectInviteRestService projectInviteRestService;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    public ProjectTeamViewModel populate(long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);

        List<ProjectUserResource> projectUsers = projectService.getDisplayProjectUsersForProject(project.getId());
        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);


        List<ProjectUserInviteResource> invitedUsers = projectInviteRestService.getInvitesByProject(projectId).getSuccess();

        boolean projectIsNotActive = !project.getProjectState().isActive();

        // support users and ifs admins can edit, other internal users have read only view only
        boolean isReadOnly = !loggedInUser.hasAnyRoles(IFS_ADMINISTRATOR, SUPPORT) || projectIsNotActive;

        List<ProjectTeamOrganisationViewModel> partnerOrgModels = projectOrganisations.stream()
                .map(org -> mapToProjectOrganisationViewModel(projectId,
                        projectUsers,
                        invitedUsers,
                        org,
                        org.equals(leadOrganisation),
                        !isReadOnly))
                .sorted()
                .collect(toList());

        boolean userCanAddAndRemoveOrganisations = userCanAddAndRemoveOrganisations(project, loggedInUser);

        partnerOrgModels.addAll(partnerOrganisationInvites(projectId, userCanAddAndRemoveOrganisations));

        return new ProjectTeamViewModel(
                project,
                partnerOrgModels,
                null,
                getProjectManager(project.getId()).orElse(null),
                false,
                loggedInUser.getId(),
                false,
                true,
                !project.getProjectState().isActive(),
                userCanAddAndRemoveOrganisations,
                canInvitePartnerOrganisation(project, loggedInUser));
    }

    private List<ProjectTeamOrganisationViewModel> partnerOrganisationInvites(long projectId, boolean userCanAddAndRemoveOrganisations) {
        return projectPartnerInviteRestService.getPartnerInvites(projectId).getSuccess()
                .stream()
                .map(invite -> new ProjectTeamOrganisationViewModel(
                        singletonList(new ProjectTeamInviteViewModel(invite.getId(), invite.getEmail(), invite.getUserName(), invite.getSentOn(), userCanAddAndRemoveOrganisations, userCanAddAndRemoveOrganisations)),
                        invite.getOrganisationName(),
                        invite.getId(),
                        false,
                        false,
                        invite.getId(),
                        null
                ))
                .collect(toList());
    }

    private boolean canInvitePartnerOrganisation(ProjectResource project, UserResource user) {
        return user.hasRole(PROJECT_FINANCE)
                && !project.isSpendProfileGenerated()
                && project.getProjectState().isActive();
    }

    private boolean userCanAddAndRemoveOrganisations(ProjectResource project, UserResource user) {
        return user.hasRole(PROJECT_FINANCE)
                && !project.isSpendProfileGenerated()
                && project.getProjectState().isActive();
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getId() == pu.getRole());
    }

    private ProjectTeamOrganisationViewModel mapToProjectOrganisationViewModel(long projectId, List<ProjectUserResource> totalUsers, List<ProjectUserInviteResource> totalInvites, OrganisationResource organisation, boolean isLead, boolean userCanAddAndResend) {
        List<ProjectUserResource> usersForOrganisation = simpleFilter(totalUsers,
                user -> user.getOrganisation().equals(organisation.getId()));
        List<ProjectUserInviteResource> invitesForOrganisation = simpleFilter(totalInvites,
                invite -> invite.getOrganisation().equals(organisation.getId()));
        AddressResource address = null;
        if (organisation.isInternational()) {
            address = partnerOrganisationRestService.getPartnerOrganisation(projectId, organisation.getId()).getSuccess().getInternationalAddress();
        }
        return new ProjectTeamOrganisationViewModel(mapUsersToViewModelRows(usersForOrganisation, invitesForOrganisation, userCanAddAndResend), organisation.getName(), organisation.getId(), isLead, userCanAddAndResend, null, address);
    }

    private List<AbstractProjectTeamRowViewModel> mapUsersToViewModelRows(List<ProjectUserResource> users, List<ProjectUserInviteResource> invites, boolean userCanAddAndResend) {

        List<ProjectTeamUserViewModel> partnerUsers = users.stream()
                .filter(pu -> !(pu.isProjectManager() || pu.isFinanceContact()))
                .map(pu -> new ProjectTeamUserViewModel(
                        pu.getUser(),
                        pu.getEmail(),
                        pu.getUserName(),
                        false))
                .distinct()
                .collect(toList());

        Optional<ProjectUserResource> financeContact = simpleFindFirst(users,
                ProjectUserResource::isFinanceContact);

        financeContact.ifPresent(fc -> partnerUsers.stream()
                .filter(user -> user.getId() == fc.getUser())
                .findFirst()
                .get()
                .setFinanceContact(true));

        Optional<ProjectUserResource> projectManager = simpleFindFirst(users,
                ProjectUserResource::isProjectManager);

        projectManager.ifPresent(pm -> partnerUsers.stream()
                .filter(user -> user.getId() == pm.getUser())
                .findFirst()
                .get()
                .setProjectManager(true));

        List<ProjectTeamInviteViewModel> inviteViews = invites.stream()
                .filter(invite -> invite.getStatus() != InviteStatus.OPENED)
                .map(invite -> new ProjectTeamInviteViewModel(
                        invite.getId(),
                        invite.getEmail(),
                        invite.getName(),
                        invite.getSentOn(),
                        false,
                        userCanAddAndResend))
                .collect(toList());

        List<AbstractProjectTeamRowViewModel> rows = new ArrayList<>();
        rows.addAll(partnerUsers);
        rows.addAll(inviteViews);
        return rows;
    }
}
