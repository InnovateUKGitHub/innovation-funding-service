package org.innovateuk.ifs.organisation.service;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationSearchResultListType;

/**
 * Class to expose methods to communicate with the companies house api.
 */
@Service
@Slf4j
public class CompaniesHouseRestServiceImpl extends BaseRestService implements CompaniesHouseRestService {
    private static final String COMPANIES_HOUSE_REST_URL = "/companies-house";

    @Override
    public RestResult<List<OrganisationSearchResult>> searchOrganisations(String searchText){
    	String searchTextEncoded;
        try {
        	searchTextEncoded = UriUtils.encode(searchText, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            searchTextEncoded = searchText;
        }

        return getWithRestResultAnonymous(COMPANIES_HOUSE_REST_URL + "/search/" + searchTextEncoded, organisationSearchResultListType());
    }

    @Override
    public RestResult<OrganisationSearchResult> getOrganisationById(String id){
        return getWithRestResultAnonymous(COMPANIES_HOUSE_REST_URL + "/company/" + id, OrganisationSearchResult.class);
    }
}