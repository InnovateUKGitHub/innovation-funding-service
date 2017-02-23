package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * This controller will handle all requests that are related to the read only view of the application team.
 */
@Controller
@RequestMapping("/application/{applicationId}/team")
@PreAuthorize("hasAuthority('applicant')")
public class ApplicationTeamController {

    @Autowired
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @GetMapping
    public String getApplicationTeam(Model model, @PathVariable("applicationId") long applicationId) {
        model.addAttribute("model", applicationTeamModelPopulator.populateModel(applicationId));
        return "application-team/team";
    }

    private Long getAuthenticatedUserOrganisationId(UserResource user, List<InviteOrganisationResource> savedInvites) {
        Optional<InviteOrganisationResource> matchingOrganisationResource = savedInvites.stream()
                .filter(inviteOrg -> inviteOrg.getInviteResources().stream()
                        .anyMatch(inv -> user.getEmail().equals(inv.getEmail())))
                .findFirst();
        return matchingOrganisationResource.map((invOrgRes -> invOrgRes.getOrganisation())).orElse(null);
    }
}