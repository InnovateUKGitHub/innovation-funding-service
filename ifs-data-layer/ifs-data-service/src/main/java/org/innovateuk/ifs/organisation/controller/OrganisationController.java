package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * This RestController exposes CRUD operations to both the
 * {@link org.innovateuk.ifs.user.service.OrganisationRestServiceImpl} and other REST-API users
 * to manage {@link Organisation} related data.
 */
@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationInitialCreationService organisationCreationService;

    @GetMapping("/find-by-application-id/{applicationId}")
    public RestResult<Set<OrganisationResource>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return organisationService.findByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping("/find-by-id/{organisationId}")
    public RestResult<OrganisationResource> findById(@PathVariable("organisationId") final Long organisationId) {
        return organisationService.findById(organisationId).toGetResponse();
    }

    @GetMapping("/primary-for-user/{userId}")
    public RestResult<OrganisationResource> getPrimaryForUser(@PathVariable("userId") final long userId) {
        return organisationService.getPrimaryForUser(userId).toGetResponse();
    }

    @GetMapping("/by-user-and-application-id/{userId}/{applicationId}")
    public RestResult<OrganisationResource> getByUserAndApplicationId(@PathVariable("userId") final long userId,
                                                              @PathVariable("applicationId") final long applicationId) {
        return organisationService.getByUserAndApplicationId(userId, applicationId).toGetResponse();
    }

    @GetMapping("/by-user-and-project-id/{userId}/{projectId}")
    public RestResult<OrganisationResource> getByUserAndProjectId(@PathVariable("userId") final long userId,
                                                                      @PathVariable("projectId") final long projectId) {
        return organisationService.getByUserAndProjectId(userId, projectId).toGetResponse();
    }

    @GetMapping("/all-by-user-id/{userId}")
    public RestResult<List<OrganisationResource>> getAllByUserId(@PathVariable("userId") final long userId) {
        return organisationService.getAllByUserId(userId).toGetResponse();
    }

    @PostMapping("/create-or-match")
    public RestResult<OrganisationResource> createOrMatch(@RequestBody OrganisationResource organisation) {
        return organisationCreationService.createOrMatch(organisation).toPostCreateResponse();
    }

    @PostMapping("/create-and-link-by-invite")
    public RestResult<OrganisationResource> createAndLinkByInvite(@RequestBody OrganisationResource organisation,
                                                          @RequestParam("inviteHash") String inviteHash) {
        return organisationCreationService.createAndLinkByInvite(organisation, inviteHash).toPostCreateResponse();
    }

    @PostMapping("/create")
    public RestResult<OrganisationResource> create(@RequestBody OrganisationResource organisation) {
        return organisationService.create(organisation).toPostCreateResponse();
    }

    @PutMapping("/update")
    public RestResult<OrganisationResource> saveResource(@RequestBody OrganisationResource organisationResource) {
        return organisationService.update(organisationResource).toPutWithBodyResponse();
    }

    @PostMapping("/update-name-and-registration/{organisationId}")
    public RestResult<OrganisationResource> updateNameAndRegistration(@PathVariable("organisationId") Long organisationId, @RequestParam(value = "name") String name, @RequestParam(value = "registration") String registration) {
        return organisationService.updateOrganisationNameAndRegistration(organisationId, name, registration).toPostCreateResponse();
    }
}
