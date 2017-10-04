package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * ProjectController exposes Project Details data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectDetailsController {

    @Autowired
    private ProjectDetailsService projectDetailsService;

    @PostMapping(value="/{id}/project-manager/{projectManagerId}")
    public RestResult<Void> setProjectManager(@PathVariable("id") final Long id, @PathVariable("projectManagerId") final Long projectManagerId) {
        return projectDetailsService.setProjectManager(id, projectManagerId).toPostResponse();
    }

    @PostMapping("/{projectId}/startdate")
    public RestResult<Void> updateProjectStartDate(@PathVariable("projectId") final Long projectId,
                                                   @RequestParam("projectStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectStartDate) {
        return projectDetailsService.updateProjectStartDate(projectId, projectStartDate).toPostResponse();
    }

    @PostMapping("/{projectId}/address")
    public RestResult<Void> updateProjectAddress(@PathVariable("projectId") final Long projectId,
                                                 @RequestParam("leadOrganisationId") final Long leadOrganisationId,
                                                 @RequestParam("addressType") final String addressType,
                                                 @RequestBody AddressResource addressResource) {
        return projectDetailsService.updateProjectAddress(leadOrganisationId, projectId, OrganisationAddressType.valueOf(addressType), addressResource).toPostResponse();
    }

    @PostMapping("/{projectId}/organisation/{organisation}/finance-contact")
    public RestResult<Void> updateFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @PathVariable("organisation") final Long organisationId,
                                                 @RequestParam("financeContact") Long financeContactUserId) {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        return projectDetailsService.updateFinanceContact(composite, financeContactUserId).toPostResponse();
    }

    @PostMapping("/{projectId}/invite-finance-contact")
    public RestResult<Void> inviteFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectDetailsService.inviteFinanceContact(projectId, inviteResource).toPostResponse();
    }

    @PostMapping("/{projectId}/invite-project-manager")
    public RestResult<Void> inviteProjectManager(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectDetailsService.inviteProjectManager(projectId, inviteResource).toPostResponse();
    }
}
