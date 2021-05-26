package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.api.RenameOrganisationV1Api;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingService;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_COMPANY_NAME_TOO_LONG;

/**
 * This RestController exposes CRUD operations to both the
 * {@link org.innovateuk.ifs.user.service.OrganisationRestServiceImpl} and other REST-API users
 * to manage {@link Organisation} related data.
 */
@RestController
@RequestMapping("/organisation")
public class OrganisationController implements RenameOrganisationV1Api {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationInitialCreationService organisationCreationService;

    @Autowired
    private OrganisationMatchingService organisationMatchingService;

    @Autowired
    private OrganisationMapper organisationMapper;

    @Override
    public RestResult<List<OrganisationResource>> findOrganisationsByCompaniesHouseNumber(
            @PathVariable final String companiesHouseNumber) {
        return organisationService.findOrganisationsByCompaniesHouseId(companiesHouseNumber).toGetResponse();
    }

    @Override
    public RestResult<List<OrganisationResource>> findOrganisationsByName(@PathVariable final String name) {
        return organisationService.findOrganisationsByName(name).toGetResponse();
    }

    @GetMapping("/find-by-application-id/{applicationId}")
    public RestResult<Set<OrganisationResource>> findByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        return organisationService.findByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping("/find-by-id/{organisationId}")
    public RestResult<OrganisationResource> findById(@PathVariable("organisationId") final Long organisationId) {
        return organisationService.findById(organisationId).toGetResponse();
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

    @GetMapping()
    public RestResult<List<OrganisationResource>> getOrganisations(@RequestParam final long userId, @RequestParam final boolean international) {
        return organisationService.getOrganisations(userId, international).toGetResponse();
    }

    @PostMapping("/create-or-match")
    public RestResult<OrganisationResource> createOrMatch(@RequestBody OrganisationResource organisation) {
        return organisationCreationService.createOrMatch(organisation).toPostCreateResponse();
    }

    @PutMapping("/update")
    public RestResult<OrganisationResource> saveResource(@RequestBody OrganisationResource organisationResource) {
        return organisationService.update(organisationResource).toPutWithBodyResponse();
    }

    @PutMapping("/sync-companies-house-details")
    public RestResult<OrganisationResource> updateCompaniesHouseDetails(@RequestBody OrganisationResource organisationResource) {
        return organisationService.syncCompaniesHouseDetails(organisationResource).toPutWithBodyResponse();
    }

    @PostMapping("/update-name-and-registration/{organisationId}")
    public RestResult<OrganisationResource> updateNameAndRegistration(
                @PathVariable Long organisationId, @RequestParam String name, @RequestParam String registration) {
        return organisationService.updateOrganisationNameAndRegistration(organisationId, name, registration).toPostWithBodyResponse();
    }

    @Override
    public RestResult<OrganisationResource> updateOrganisationName(Long organisationId, String name) {
        return organisationService.updateOrganisationName(organisationId, name).toPostCreateResponse();
    }

}
