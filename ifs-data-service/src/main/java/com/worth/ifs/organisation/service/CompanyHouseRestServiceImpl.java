package com.worth.ifs.organisation.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;
@Service
public class CompanyHouseRestServiceImpl  extends BaseRestService implements CompanyHouseRestService{
    @Value("${ifs.data.service.rest.companyhouse}")
    String companyHouseRestUrl;

    public List<CompanyHouseBusiness> searchOrganisationsByName(String name){
        return asList(restGet(companyHouseRestUrl + "/searchCompanyHouse/"+name, CompanyHouseBusiness[].class));
    }
    public CompanyHouseBusiness getOrganisationById(String id){
        return restGet(companyHouseRestUrl + "/getCompanyHouse/"+id, CompanyHouseBusiness.class);
    }

}
