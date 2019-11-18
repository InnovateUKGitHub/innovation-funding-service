package org.innovateuk.ifs.organisation.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPANIES_HOUSE_NO_RESPONSE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPANIES_HOUSE_UNABLE_TO_DECODE_SEARCH_STRING;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * This class communicates with the Companies House API.
 * This is used to get information abouts companies.
 *
 * @see <a href="https://developer.companieshouse.gov.uk/api/docs/">Companies House API site</a>
 */
@Service
@ConditionalOnProperty(name = "ifs.data.companies.house.lookup.enabled", havingValue = "", matchIfMissing = true)
public class CompaniesHouseApiServiceImpl implements CompaniesHouseApiService {

    private static final Log LOG = LogFactory.getLog(CompaniesHouseApiServiceImpl.class);

    @Value("${ifs.data.companies.house.url}")
    private String companiesHouseUrl = null;

    private static final int SEARCH_ITEMS_MAX = 10;

    private static final String COMPANIES_HOUSE_SEARCH_PATH = "search/companies?items_per_page={items_per_page}&q={q}";

    private static final String SEARCH_WORD_KEY = "q";

    private static final String ITEMS_PER_PAGE_KEY = "items_per_page";

    @Autowired
    @Qualifier("companieshouse_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText) {
        return decodeString(encodedSearchText).andOnSuccess(decodedSearchText -> {
             // encoded in the web-services.
            JsonNode companiesResources = restGet(COMPANIES_HOUSE_SEARCH_PATH, JsonNode.class, companySearchUrlVariables(decodedSearchText));
            JsonNode companyItems = companiesResources.path("items");
            List<OrganisationSearchResult> results = new ArrayList<>();
            companyItems.forEach(i -> results.add(companySearchMapper(i)));
            return serviceSuccess(results);
        });
    }

    @Override
    public ServiceResult<OrganisationSearchResult> getOrganisationById(String id) {
        LOG.debug("getOrganisationById " + id);

        return ofNullable(restGet("company/" + id, JsonNode.class)).
            map(jsonNode -> serviceSuccess(companyProfileMapper(jsonNode))).
            orElse(serviceFailure(COMPANIES_HOUSE_NO_RESPONSE));
    }

    protected <T> T restGet(String path, Class<T> c) {
        return adaptor.restGetEntity(companiesHouseUrl + path, c).getBody();
    }

    /**
     * Method to the pass query variable separately rather than passing as part of the path.
     * This allows the proper encoding of the query values by the default URLComponentBuild used by the
     * Rest Template.
     *
     * @param path - URL path
     * @param c - Return class type
     * @param <T> - Return data type from the method
     * @return
     */
    protected <T> T restGet(String path, Class<T> c, Map<String, Object> variables) {
        return adaptor.restGetEntity(companiesHouseUrl + path, c, variables).getBody();
    }

    /**
     * Method to build the query variable map. The keys in the map must match the
     * keys defined in the URL path 'COMPANIES_HOUSE_SEARCH_PATH'.
     * @param searchWord
     * @return {@link Map}
     */
    private Map<String, Object> companySearchUrlVariables(String searchWord) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ITEMS_PER_PAGE_KEY, SEARCH_ITEMS_MAX);
        variables.put(SEARCH_WORD_KEY, searchWord);
        return variables;
    }

    private OrganisationSearchResult companyProfileMapper(JsonNode jsonNode) {
        AddressResource officeAddress = getAddress(jsonNode, "registered_office_address");
        ObjectMapper mapper = new ObjectMapper();

        OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("company_name").asText());
        org.setExtraAttributes(mapper.convertValue(jsonNode, Map.class));
        org.setOrganisationAddress(officeAddress);
        return org;
    }

    private AddressResource getAddress(JsonNode jsonNode, String path) {
    	String line1 = stringOrNull(jsonNode, path, "address_line_1");
    	String line2 = stringOrNull(jsonNode, path, "address_line_2");
    	String line3 = stringOrNull(jsonNode, path, "address_line_3");
    	String locality = stringOrNull(jsonNode, path, "locality");
    	String region = stringOrNull(jsonNode, path, "region");
    	String postcode = stringOrNull(jsonNode, path, "postal_code");
         
        return new AddressResource(line1, line2, line3, locality, region, postcode);
    }

    private String stringOrNull(JsonNode jsonNode, String path, String path2) {
    	JsonNode node = jsonNode.path(path);
    	if(node.hasNonNull(path2)) {
    		return node.path(path2).asText();
    	}
    	return null;
	}

	private ServiceResult<String> decodeString(String encodedSearchText) {
        try {
            return serviceSuccess(UriUtils.decode(encodedSearchText, "UTF-8"));
        } catch (Exception e) {
            LOG.error("Unable to decode search string " + encodedSearchText, e);
            return serviceFailure(COMPANIES_HOUSE_UNABLE_TO_DECODE_SEARCH_STRING);
        }
    }

    private OrganisationSearchResult companySearchMapper(JsonNode jsonNode) {
        AddressResource officeAddress = getAddress(jsonNode, "address");

        OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("title").asText());
        Map<String, Object> extras = new HashMap<>();
        extras.put("company_type", jsonNode.path("company_type").asText());
        extras.put("date_of_creation", jsonNode.path("date_of_creation").asText());
        extras.put("description", jsonNode.path("description").asText());
        org.setExtraAttributes(extras);
        org.setOrganisationAddress(officeAddress);
        return org;
    }
    
    protected void setCompaniesHouseUrl(String companiesHouseUrl) {
		this.companiesHouseUrl = companiesHouseUrl;
	}
}