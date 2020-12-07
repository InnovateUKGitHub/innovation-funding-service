package org.innovateuk.ifs.organisation.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Value("${ifs.new.organisation.search.enabled}")
    private boolean isImprovedSearchEnabled = false;

    private static final int SEARCH_ITEMS_MAX = 20;

    private static final String COMPANIES_HOUSE_SEARCH_PATH = "search/companies?items_per_page={items_per_page}&q={q}";

    private static final String COMPANIES_HOUSE_LIST_DIRECTORS_PATH = "/officers?items_per_page={items_per_page}&register_type={register_type}";

    private static final String SEARCH_WORD_KEY = "q";

    private static final String OFFICERS_TYPE = "register_type";

    private static final String DIRECTORS = "directors";

    private static final String DIRECTOR = "director";

    private static final String ITEMS_PER_PAGE_KEY = "items_per_page";

    @Autowired
    @Qualifier("companieshouse_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText) {
        if (!isImprovedSearchEnabled) {
            return decodeString(encodedSearchText).andOnSuccess(decodedSearchText -> {
                // encoded in the web-services.
                JsonNode companiesResources = restGet(COMPANIES_HOUSE_SEARCH_PATH, JsonNode.class, companySearchUrlVariables(decodedSearchText));
                JsonNode companyItems = companiesResources.path("items");
                List<OrganisationSearchResult> results = new ArrayList<>();
                companyItems.forEach(i -> results.add(companySearchMapper(i)));
                return serviceSuccess(results);
            });
        }
        return improvedSearchOrganisations(encodedSearchText);
    }

    public ServiceResult<List<OrganisationSearchResult>> improvedSearchOrganisations(String encodedSearchText) {
        return decodeString(encodedSearchText).andOnSuccess(decodedSearchText -> {
            // search prgansiation
            JsonNode companiesResources = restGet(COMPANIES_HOUSE_SEARCH_PATH, JsonNode.class, companySearchUrlVariables(decodedSearchText));
            JsonNode searchResultItems = companiesResources.path("items");
            List<OrganisationSearchResult> results = new ArrayList<>();
            searchResultItems.forEach(companyItem ->
            {
                String comanyHouseNo = companyItem.path("company_number").asText();
                Optional<JsonNode> companyDetails = getCompanyDetails(comanyHouseNo);
                Optional<JsonNode> directorsDetails = getDirectorsDetails(comanyHouseNo);
                OrganisationSearchResult orgResult = companySearchDataMapper(companyItem, companyDetails, directorsDetails);
                results.add(orgResult);
            });
            return serviceSuccess(results);
        });
    }

    private Optional<JsonNode> getCompanyDetails(String companiesHouseNo) {
        return ofNullable(restGet("company/" + companiesHouseNo, JsonNode.class));
    }

    private Optional<JsonNode> getDirectorsDetails(String companiesHouseNo) {
        try {
           return ofNullable(restGet("company/" + companiesHouseNo + COMPANIES_HOUSE_LIST_DIRECTORS_PATH,
                    JsonNode.class, listDirectorsUrlVariables()));
        } catch (HttpStatusCodeException exe) {
            if (exe.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.warn(exe.getMessage());
            }
        }
        return Optional.empty();
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
     * @param c    - Return class type
     * @param <T>  - Return data type from the method
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

    private Map<String, Object> listDirectorsUrlVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ITEMS_PER_PAGE_KEY, SEARCH_ITEMS_MAX);
        variables.put(OFFICERS_TYPE, DIRECTORS);
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

    private OrganisationSearchResult companyProfileMapperWithSicCode(JsonNode jsonNode, OrganisationSearchResult org) {
        //    AddressResource officeAddress = getAddress(jsonNode, "registered_office_address");
        List<OrganisationSicCodeResource> sicCodeResources = getSicCode(jsonNode, "sic_codes");
        ObjectMapper mapper = new ObjectMapper();

        //   OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("company_name").asText());
        org.setExtraAttributes(mapper.convertValue(jsonNode, Map.class));
        //   org.setOrganisationAddress(officeAddress);
        org.setOrganisationSicCodes(sicCodeResources);
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

    private List<OrganisationSicCodeResource> getSicCode(JsonNode jsonNode, String fieldName) {
        List<OrganisationSicCodeResource> sicCodeResources = new ArrayList<>();
        JsonNode sicCodeArray = jsonNode.get(fieldName);
        if (sicCodeArray != null) {
            for (JsonNode action : sicCodeArray) {
                sicCodeResources.add(new OrganisationSicCodeResource(action.asText()));
            }
        }
        return sicCodeResources;
    }

    private List<OrganisationExecutiveOfficerResource> getExecutiveOfficers(JsonNode jsonNode, String pathName) {
        List<OrganisationExecutiveOfficerResource> executiveOfficers = new ArrayList<>();
        JsonNode directorsDetails = jsonNode.path("items");
        directorsDetails.forEach(directorItem -> {
            if(directorItem.get("resigned_on") == null) {
                String officerRole = directorItem.get("officer_role").asText();
                if(!officerRole.isEmpty() && officerRole.equalsIgnoreCase(DIRECTOR)) {
                    executiveOfficers.add(new OrganisationExecutiveOfficerResource(directorItem.get("name").asText()));
                }
            }
       });
        return executiveOfficers;
    }

    private String stringOrNull(JsonNode jsonNode, String path, String path2) {
        JsonNode node = jsonNode.path(path);
        if (node.hasNonNull(path2)) {
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

    //Now both companySearchMapper and companySearchDataMapper same, but need to check the address
    private OrganisationSearchResult companySearchDataMapper(JsonNode companyItems, Optional<JsonNode> companyDetails, Optional<JsonNode> directorDetails) {
        AddressResource officeAddress = getAddress(companyItems, "address");
        OrganisationSearchResult org = new OrganisationSearchResult(companyItems.path("company_number").asText(), companyItems.path("title").asText());
        Map<String, Object> extras = new HashMap<>();
        extras.put("company_type", companyItems.path("company_type").asText());
        extras.put("date_of_creation", companyItems.path("date_of_creation").asText());
        extras.put("description", companyItems.path("description").asText());
        org.setExtraAttributes(extras);
        org.setOrganisationAddress(officeAddress);
        if (companyDetails.isPresent()) {
            List<OrganisationSicCodeResource> sicCodeResources = getSicCode(companyDetails.get(), "sic_codes");
            org.setOrganisationSicCodes(sicCodeResources);
        }
        if(directorDetails.isPresent()) {
            List<OrganisationExecutiveOfficerResource> executiveOfficerResources = getExecutiveOfficers(directorDetails.get(), "items");
            org.setOrganisationExecutiveOfficers(executiveOfficerResources);
        }
        return org;
    }

    protected void setCompaniesHouseUrl(String companiesHouseUrl) {
        this.companiesHouseUrl = companiesHouseUrl;
    }
}