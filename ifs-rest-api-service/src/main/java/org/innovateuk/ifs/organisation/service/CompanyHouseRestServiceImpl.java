package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationSearchResultListType;

/**
 * Class to expose methods to communicate with the company house api.
 */
@Service
public class CompanyHouseRestServiceImpl extends BaseRestService implements CompanyHouseRestService {
    private static final Log log = LogFactory.getLog(CompanyHouseRestServiceImpl.class);

    private String companyHouseRestUrl = "/companyhouse";

    @Override
    public RestResult<List<OrganisationSearchResult>> searchOrganisations(String searchText){
    	String searchTextEncoded;
        try {
        	searchTextEncoded = UriUtils.encode(searchText, "UTF-8");
        } catch (Exception e) {
            log.error(e);
            searchTextEncoded = searchText;
        }

        return getWithRestResultAnonymous(companyHouseRestUrl + "/searchCompanyHouse/" + searchTextEncoded, organisationSearchResultListType());
    }
    @Override
    public RestResult<OrganisationSearchResult> getOrganisationById(String id){
        return getWithRestResultAnonymous(companyHouseRestUrl + "/getCompanyHouse/" + id, OrganisationSearchResult.class);
    }
}
