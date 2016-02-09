package com.worth.ifs.organisation.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

/**
 * CompanyHouseController exposes CompanyHouse data and operations through a REST API.
 */
@RestController
@RequestMapping("/companyhouse")
public class CompanyHouseController {

    @Autowired
    private CompanyHouseApi companyHouseService;

    @RequestMapping("/searchCompanyHouse/{searchText}")
    public RestResult<List<CompanyHouseBusiness>> searchCompanyHouse(@PathVariable("searchText") final String searchText) {
        return newRestHandler().perform(() -> companyHouseService.searchOrganisations(searchText));
    }

    @RequestMapping("/getCompanyHouse/{id}")
    public RestResult<CompanyHouseBusiness> getCompanyHouse(@PathVariable("id") final String id) {
        return newRestHandler().perform(() -> companyHouseService.getOrganisationById(id));
    }
}
