package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.transactional.CompaniesHouseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CompaniesHouseController exposes CompaniesHouse data and operations through a REST API.
 */
@RestController
@RequestMapping("companies-house")
public class CompaniesHouseController {

    @Autowired
    private CompaniesHouseApiService companiesHouseService;

    @GetMapping("/search/{searchText}/{indexPos}")
    public RestResult<List<OrganisationSearchResult>> search(@PathVariable("searchText") final String searchText, @PathVariable("indexPos") final int indexPos) {
        return companiesHouseService.searchOrganisations(searchText, indexPos).toGetResponse();
    }

    @GetMapping("/company/{id}")
    public RestResult<OrganisationSearchResult> getCompany(@PathVariable("id") final String id) {
        return companiesHouseService.getOrganisationById(id).toGetResponse();
    }
}