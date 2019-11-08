package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.transactional.OrganisationFinanceService;
import org.springframework.web.bind.annotation.*;

public abstract class AbstractOrganisationFinanceController {

    protected abstract OrganisationFinanceService getOrganisationFinanceService();

    @GetMapping("/with-growth-table")
    public RestResult<OrganisationFinancesWithGrowthTableResource> getOrganisationWithGrowthTable(
            @PathVariable long targetId,
            @PathVariable long organisationId) {
        return getOrganisationFinanceService().getOrganisationWithGrowthTable(targetId, organisationId).toGetResponse();
    }

    @GetMapping("/without-growth-table")
    public RestResult<OrganisationFinancesWithoutGrowthTableResource> getOrganisationWithoutGrowthTable(
            @PathVariable long targetId,
            @PathVariable long organisationId) {
        return getOrganisationFinanceService().getOrganisationWithoutGrowthTable(targetId, organisationId).toGetResponse();
    }

    @PostMapping("/with-growth-table")
    public RestResult<Void> updateOrganisationWithGrowthTable(
            @PathVariable long targetId,
            @PathVariable long organisationId,
            @RequestBody OrganisationFinancesWithGrowthTableResource finances) {
        return getOrganisationFinanceService().updateOrganisationWithGrowthTable(targetId, organisationId, finances).toPostResponse();
    }

    @PostMapping("/without-growth-table")
    public RestResult<Void> updateOrganisationWithoutGrowthTable(
            @PathVariable long targetId,
            @PathVariable long organisationId,
            @RequestBody OrganisationFinancesWithoutGrowthTableResource finances) {
        return getOrganisationFinanceService().updateOrganisationWithoutGrowthTable(targetId, organisationId, finances).toPostResponse();
    }

    @GetMapping("/show-state-aid")
    public RestResult<Boolean> isShowStateAidAgreement(
            @PathVariable long targetId,
            @PathVariable long organisationId) {
        return getOrganisationFinanceService().isShowStateAidAgreement(targetId, organisationId).toGetResponse();
    }
}
