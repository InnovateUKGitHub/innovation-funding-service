package com.worth.ifs.user.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.organisation.transactional.CompanyHouseApiService;
import com.worth.ifs.organisation.transactional.OrganisationService;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.transactional.OrganisationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * This rest controller is only use to search organisations, form external systems, like CompanyHouse, and the list of academics.
 * This is used when a user registers a new organisation.
 */
@RestController
@RequestMapping("/organisationsearch")
public class ExternalOrganisationSearchController {

    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private OrganisationTypeService organisationTypeService;
    @Autowired
    private CompanyHouseApiService companyHouseService;

    private static final int SEARCH_ITEMS_MAX = 10;


    @RequestMapping("/searchOrganisations/{organisationType}")
    public RestResult<List<OrganisationSearchResult>> searchOrganisations(@PathVariable("organisationType") final Long organisationTypeId,
                                                                          @RequestParam("organisationSearchText") final String organisationSearchText) {
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(organisationTypeId);

        switch (organisationType){
            case BUSINESS:
                return companyHouseService.searchOrganisations(organisationSearchText).toGetResponse();
            case ACADEMIC:
                return organisationService.searchAcademic(organisationSearchText, SEARCH_ITEMS_MAX).toGetResponse();
            default:

                break;
        }

        return RestResult.restFailure(new Error("Search for organisation failed", HttpStatus.NOT_FOUND));
    }

    @RequestMapping("/getOrganisation/{organisationType}/{organisationSearchId}")
    public RestResult<OrganisationSearchResult> searchOrganisation(@PathVariable("organisationType") final Long organisationTypeId, @PathVariable("organisationSearchId") final String organisationSearchId) {
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(organisationTypeId);
        switch (organisationType){
            case BUSINESS:
                return companyHouseService.getOrganisationById(organisationSearchId).toGetResponse();
            case ACADEMIC:
                return organisationService.getSearchOrganisation(Long.valueOf(organisationSearchId)).toGetResponse();
            default:

                break;
        }

        return RestResult.restFailure(new Error("Search for organisation failed", HttpStatus.NOT_FOUND));
    }
}
