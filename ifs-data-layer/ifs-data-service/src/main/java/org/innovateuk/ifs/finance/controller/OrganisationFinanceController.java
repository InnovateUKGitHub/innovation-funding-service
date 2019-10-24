package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.transactional.OrganisationFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * A Controller to support the "Your organisation" section of Application Form finances.
 */
@RestController
@RequestMapping("/application/{applicationId}/organisation/{organisationId}/finance")
public class OrganisationFinanceController {

    @Autowired
    private OrganisationFinanceService organisationFinanceService;

    @GetMapping("/with-growth-table")
    public RestResult<OrganisationFinancesWithGrowthTableResource> getOrganisationWithGrowthTable(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId) {
        return organisationFinanceService.getOrganisationWithGrowthTable(applicationId, organisationId).toGetResponse();
    }

    @GetMapping("/without-growth-table")
    public RestResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationWithoutGrowthTable(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId) {
        return organisationFinanceService.getOrganisationWithoutGrowthTable(applicationId, organisationId).toGetResponse();
    }

    @PostMapping("/with-growth-table")
    public RestResult<Void> updateOrganisationWithGrowthTable(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @RequestBody OrganisationFinancesWithGrowthTableResource finances) {
        return organisationFinanceService.updateOrganisationWithGrowthTable(applicationId, organisationId, finances).toPostResponse();
    }

    @PostMapping("/without-growth-table")
    public RestResult<Void> updateOrganisationWithoutGrowthTable(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId,
            @RequestBody OrganisationFinancesWithoutGrowthTableResource finances) {
        return organisationFinanceService.updateOrganisationWithoutGrowthTable(applicationId, organisationId, finances).toPostResponse();
    }

    @GetMapping("/show-state-aid")
    public RestResult<Boolean> isShowStateAidAgreement(
            @PathVariable("applicationId") long applicationId,
            @PathVariable("organisationId") long organisationId) {
        return organisationFinanceService.isShowStateAidAgreement(applicationId, organisationId).toGetResponse();
    }
}
