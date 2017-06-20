package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.transactional.CompanyHouseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/searchCompanyHouse/{searchText}")
    public RestResult<List<OrganisationSearchResult>> searchCompanyHouse(@PathVariable("searchText") final String searchText) {
        return companyHouseService.searchOrganisations(searchText).toGetResponse();
    }

    @GetMapping("/getCompanyHouse/{id}")
    public RestResult<OrganisationSearchResult> getCompanyHouse(@PathVariable("id") final String id) {
        return companyHouseService.getOrganisationById(id).toGetResponse();
    }
}
