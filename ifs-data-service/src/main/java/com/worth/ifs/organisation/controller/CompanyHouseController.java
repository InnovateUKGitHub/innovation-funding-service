package com.worth.ifs.organisation.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CompanyHouseController exposes CompanyHouse data and operations through a REST API.
 */
@RestController
@RequestMapping("/companyhouse")
public class CompanyHouseController {

    @Autowired
    private CompanyHouseApiService companyHouseService;

    @RequestMapping("/searchCompanyHouse/{searchText}")
    public RestResult<List<OrganisationSearchResult>> searchCompanyHouse(@PathVariable("searchText") final String searchText) {
        return companyHouseService.searchOrganisations(searchText).toGetResponse();
    }

    @RequestMapping("/getCompanyHouse/{id}")
    public RestResult<OrganisationSearchResult> getCompanyHouse(@PathVariable("id") final String id) {
        return companyHouseService.getOrganisationById(id).toGetResponse();
    }
}
