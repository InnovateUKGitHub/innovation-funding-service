package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.project.transactional.ProjectStatusService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * ProjectController exposes Project data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectStatusService projectStatusService;

    @GetMapping("/{id}")
    public RestResult<ProjectResource> getProjectById(@PathVariable("id") final Long id) {
        return projectService.getProjectById(id).toGetResponse();
    }

    @GetMapping("/application/{application}")
    public RestResult<ProjectResource> getByApplicationId(@PathVariable("application") final Long application) {
        return projectService.getByApplicationId(application).toGetResponse();
    }

    @GetMapping("/")
    public RestResult<List<ProjectResource>> findAll() {
        return projectService.findAll().toGetResponse();
    }

    @PostMapping(value="/{id}/project-manager/{projectManagerId}")
    public RestResult<Void> setProjectManager(@PathVariable("id") final Long id, @PathVariable("projectManagerId") final Long projectManagerId) {
        return projectService.setProjectManager(id, projectManagerId).toPostResponse();
    }

    @PostMapping("/{projectId}/startdate")
    public RestResult<Void> updateProjectStartDate(@PathVariable("projectId") final Long projectId,
                                                   @RequestParam("projectStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate projectStartDate) {
        return projectService.updateProjectStartDate(projectId, projectStartDate).toPostResponse();
    }

    @PostMapping("/{projectId}/address")
    public RestResult<Void> updateProjectAddress(@PathVariable("projectId") final Long projectId,
                                                 @RequestParam("leadOrganisationId") final Long leadOrganisationId,
                                                 @RequestParam("addressType") final String addressType,
                                                 @RequestBody AddressResource addressResource) {
        return projectService.updateProjectAddress(leadOrganisationId, projectId, OrganisationAddressType.valueOf(addressType), addressResource).toPostResponse();
    }

    @GetMapping(value = "/user/{userId}")
    public RestResult<List<ProjectResource>> findByUserId(@PathVariable("userId") final Long userId) {
        return projectService.findByUserId(userId).toGetResponse();
    }

    @PostMapping("/{projectId}/organisation/{organisation}/finance-contact")
    public RestResult<Void> updateFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @PathVariable("organisation") final Long organisationId,
                                                 @RequestParam("financeContact") Long financeContactUserId) {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        return projectService.updateFinanceContact(composite, financeContactUserId).toPostResponse();
    }

    @PostMapping("/{projectId}/invite-finance-contact")
    public RestResult<Void> inviteFinanceContact(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectService.inviteFinanceContact(projectId, inviteResource).toPostResponse();
    }

    @PostMapping("/{projectId}/invite-project-manager")
    public RestResult<Void> inviteProjectManager(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody @Valid final InviteProjectResource inviteResource) {
        return projectService.inviteProjectManager(projectId, inviteResource).toPostResponse();
    }

    @GetMapping("/{projectId}/project-users")
    public RestResult<List<ProjectUserResource>> getProjectUsers(@PathVariable("projectId") final Long projectId) {
        return projectService.getProjectUsers(projectId).toGetResponse();
    }

    @PostMapping("/{projectId}/setApplicationDetailsSubmitted")
    public RestResult<Void> setApplicationDetailsSubmitted(@PathVariable("projectId") final Long projectId){
        return projectService.submitProjectDetails(projectId, ZonedDateTime.now()).toPostResponse();
    }

    @GetMapping("/{projectId}/isSubmitAllowed")
    public RestResult<Boolean> isSubmitAllowed(@PathVariable("projectId") final Long projectId){
        return projectService.isSubmitAllowed(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/getOrganisationByUser/{userId}")
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(@PathVariable("projectId") final Long projectId,
                                                                            @PathVariable("userId") final Long userId){
        return projectService.getOrganisationByProjectAndUser(projectId, userId).toGetResponse();
    }

    @PostMapping("/{projectId}/partners")
    public RestResult<Void> addPartner(@PathVariable(value = "projectId")Long projectId,
                                       @RequestParam(value = "userId", required = true) Long userId,
                                       @RequestParam(value = "organisationId", required = true) Long organisationId) {
        return projectService.addPartner(projectId, userId, organisationId).toPostResponse();
    }

    @GetMapping("/{projectId}/team-status")
    public RestResult<ProjectTeamStatusResource> getTeamStatus(@PathVariable(value = "projectId") Long projectId,
                                                               @RequestParam(value = "filterByUserId", required = false) Long filterByUserId) {
        return projectService.getProjectTeamStatus(projectId, ofNullable(filterByUserId)).toGetResponse();
    }

    @GetMapping("/{projectId}/project-manager")
    public RestResult<ProjectUserResource> getProjectManager(@PathVariable(value = "projectId") Long projectId) {
        return projectService.getProjectManager(projectId).toGetResponse();
    }

    @GetMapping("/{projectId}/status")
    public RestResult<ProjectStatusResource> getStatus(@PathVariable(value = "projectId") Long projectId) {
        return projectStatusService.getProjectStatusByProjectId(projectId).toGetResponse();
    }
}
