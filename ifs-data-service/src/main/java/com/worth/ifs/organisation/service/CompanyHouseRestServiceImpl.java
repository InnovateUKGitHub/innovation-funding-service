package com.worth.ifs.organisation.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Class to expose methods to communicate with the company house api.
 */
@Service
public class CompanyHouseRestServiceImpl  extends BaseRestService implements CompanyHouseRestService{
    @Value("${ifs.data.service.rest.companyhouse}")
    String companyHouseRestUrl;

    public List<CompanyHouseBusiness> searchOrganisations(String searchText){
        try {
            searchText = UriUtils.encode(searchText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return asList(restGet(companyHouseRestUrl + "/searchCompanyHouse/"+searchText, CompanyHouseBusiness[].class));
    }
    public CompanyHouseBusiness getOrganisationById(String id){

        return restGet(companyHouseRestUrl + "/getCompanyHouse/"+id, CompanyHouseBusiness.class);
    }
}
