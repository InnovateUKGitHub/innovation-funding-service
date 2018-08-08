package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
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
@ZeroDowntime(description = "Remove /companyhouse mapping", reference = "IFS-3195")
@RequestMapping({ "/companyhouse", "companies-house" })
public class CompaniesHouseController {

    @Autowired
    private CompaniesHouseApiService companiesHouseService;

    @ZeroDowntime(description = "Remove searchCompanyHouse", reference = "IFS-3195")
    @GetMapping({ "/searchCompanyHouse/{searchText}", "/search/{searchText}" })
    public RestResult<List<OrganisationSearchResult>> search(@PathVariable("searchText") final String searchText) {
        return companiesHouseService.searchOrganisations(searchText).toGetResponse();
    }

    @ZeroDowntime(description = "Remove getCompanyHouse", reference = "IFS-3195")
    @GetMapping({ "/getCompanyHouse/{id}",  "/company/{id}"})
    public RestResult<OrganisationSearchResult> getCompany(@PathVariable("id") final String id) {
        return companiesHouseService.getOrganisationById(id).toGetResponse();
    }
}