package com.worth.ifs.organisation.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.RestResultBuilder;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestResultHandler;

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

        RestResultBuilder<List<CompanyHouseBusiness>, List<CompanyHouseBusiness>> restResult = newRestHandler();
        return restResult.perform(() -> companyHouseService.searchOrganisations(searchText));
    }

    @RequestMapping("/getCompanyHouse/{id}")
    public RestResult<CompanyHouseBusiness> getCompanyHouse(@PathVariable("id") final String id) {

        RestResultBuilder<CompanyHouseBusiness, CompanyHouseBusiness> handler = newRestHandler();
        return handler.perform(() -> companyHouseService.getOrganisationById(id));
    }
}
