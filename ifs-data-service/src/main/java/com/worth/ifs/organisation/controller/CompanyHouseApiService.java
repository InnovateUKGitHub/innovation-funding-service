package com.worth.ifs.organisation.controller;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;

import java.util.List;

/**
 *
 */
public interface CompanyHouseApiService {

    ServiceResult<List<CompanyHouseBusiness>> searchOrganisations(String encodedSearchText);

    ServiceResult<CompanyHouseBusiness> getOrganisationById(String id);
}
