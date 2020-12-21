package org.innovateuk.ifs.organisation.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.util.*;

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

    private static final int IMPROVED_SEARCH_ITEMS_MAX = 10;

    private static final String COMPANIES_HOUSE_SEARCH_PATH = "search/companies?items_per_page={items_per_page}&q={q}";
    private static final String COMPANIES_HOUSE_SEARCH_BY_INDEX_PATH = "search/companies?q={q}&items_per_page={items_per_page}&start_index={start_index}";
    private static final String COMPANIES_HOUSE_LIST_DIRECTORS_PATH = "/officers?items_per_page={items_per_page}&register_type={register_type}";

    private static final String SEARCH_WORD_KEY = "q";

    private static final String OFFICERS_TYPE = "register_type";

    private static final String DIRECTORS = "directors";

    private static final String DIRECTOR = "director";

    private static final String ITEMS_PER_PAGE_KEY = "items_per_page";

    private static final String START_INDEX_KEY = "start_index";

    @Autowired
    @Qualifier("companieshouse_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText) {
        return decodeString(encodedSearchText).andOnSuccess(decodedSearchText -> {
            // encoded in the web-services.
            JsonNode companiesResources = restGet(COMPANIES_HOUSE_SEARCH_PATH, JsonNode.class, companySearchUrlVariables(decodedSearchText));
            return getSearchOrganisationResults(companiesResources, "");
        });
    }

    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText, int indexPos) {
        if (isImprovedSearchEnabled) {
            return decodeString(encodedSearchText).andOnSuccess(decodedSearchText -> {
                // search organsiation with index
                JsonNode companiesResources = restGet(COMPANIES_HOUSE_SEARCH_BY_INDEX_PATH, JsonNode.class, companySearchUrlVariablesWithIndex(decodedSearchText, indexPos));
                String totalResults = companiesResources.path("total_results").asText();
                return getSearchOrganisationResults(companiesResources, totalResults);
            });
        }
        return searchOrganisations(encodedSearchText);
    }

    @Override
    public ServiceResult<OrganisationSearchResult> getOrganisationById(String id) {
        LOG.debug("getOrganisationById " + id);
        if (isImprovedSearchEnabled) {
            Optional<JsonNode> companyDetails = getCompanyDetails(id);
            Optional<JsonNode> directorsDetails = getDirectorsDetails(id);
            OrganisationSearchResult orgResult = companyProfileSicCodeDirectorsMapper(companyDetails, companyDetails, directorsDetails);
            return serviceSuccess(orgResult);
        }
        return ofNullable(restGet("company/" + id, JsonNode.class)).
                map(jsonNode -> serviceSuccess(companyProfileMapper(jsonNode))).
                orElse(serviceFailure(COMPANIES_HOUSE_NO_RESPONSE));
    }

    private ServiceResult<List<OrganisationSearchResult>> getSearchOrganisationResults(JsonNode companiesResources, String totalResults) {
        JsonNode companyItems = companiesResources.path("items");
        List<OrganisationSearchResult> results = new ArrayList<>();
        companyItems.forEach(i -> results.add(companySearchMapper(i, totalResults)));
        return serviceSuccess(results);
    }

    private Optional<JsonNode> getCompanyDetails(String companiesHouseNo) {
        return ofNullable(restGet("company/" + companiesHouseNo, JsonNode.class));
    }

    private Optional<JsonNode> getDirectorsDetails(String companiesHouseNo) {
        return ofNullable(restGet("company/" + companiesHouseNo + COMPANIES_HOUSE_LIST_DIRECTORS_PATH,
                JsonNode.class, listDirectorsUrlVariables()));
    }

    private OrganisationSearchResult companySearchMapper(JsonNode jsonNode, String totalResults) {
        AddressResource officeAddress = getAddress(jsonNode, "address");
        OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("title").asText());
        Map<String, Object> extras = new HashMap<>();
        extras.put("company_type", jsonNode.path("company_type").asText());
        extras.put("date_of_creation", jsonNode.path("date_of_creation").asText());
        extras.put("description", jsonNode.path("description").asText());
        extras.put("total_results", totalResults);
        org.setExtraAttributes(extras);
        org.setOrganisationAddress(officeAddress);
        return org;
    }

    private OrganisationSearchResult companyProfileMapper(JsonNode jsonNode) {
        AddressResource officeAddress = getAddress(jsonNode, "registered_office_address");
        ObjectMapper mapper = new ObjectMapper();

        OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("company_name").asText());
        org.setExtraAttributes(mapper.convertValue(jsonNode, Map.class));
        org.setOrganisationAddress(officeAddress);
        return org;
    }

    private OrganisationSearchResult companyProfileSicCodeDirectorsMapper(Optional<JsonNode> companyItems, Optional<JsonNode> companyDetails, Optional<JsonNode> directorDetails) {
        if (companyItems.isPresent()) {
            JsonNode companyItemsNode = companyItems.get();
            AddressResource registeredOfficeAddress = getAddress(companyItemsNode, "registered_office_address");
            OrganisationAddressResource orgAddressResource = new OrganisationAddressResource(registeredOfficeAddress, new AddressTypeResource());
            ObjectMapper mapper = new ObjectMapper();

            OrganisationSearchResult org = new OrganisationSearchResult(companyItemsNode.path("company_number").asText(), companyItemsNode.path("company_name").asText());
            org.setExtraAttributes(mapper.convertValue(companyItemsNode, Map.class));
            org.setOrganisationAddress(registeredOfficeAddress);

            if (companyDetails.isPresent()) {
                List<OrganisationSicCodeResource> sicCodeResources = getSicCode(companyDetails.get(), "sic_codes");
                org.setOrganisationSicCodes(sicCodeResources);
            }
            if (directorDetails.isPresent()) {
                List<OrganisationExecutiveOfficerResource> executiveOfficerResources = getCurrentDirectors(directorDetails.get(), "items");
                org.setOrganisationExecutiveOfficers(executiveOfficerResources);
            }
            return org;
        }
        return new OrganisationSearchResult();
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

    private Map<String, Object> companySearchUrlVariablesWithIndex(String searchWord, int indexPos) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(SEARCH_WORD_KEY, searchWord);
        variables.put(ITEMS_PER_PAGE_KEY, IMPROVED_SEARCH_ITEMS_MAX);
        variables.put(START_INDEX_KEY, indexPos);
        return variables;
    }

    private Map<String, Object> listDirectorsUrlVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ITEMS_PER_PAGE_KEY, IMPROVED_SEARCH_ITEMS_MAX);
        variables.put(OFFICERS_TYPE, DIRECTORS);
        return variables;
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
        JsonNode sicCodesArray = jsonNode.get(fieldName);
        sicCodesArray.forEach(sicCode -> {
            OrganisationSicCodeResource sicCodeResource = new OrganisationSicCodeResource();
            sicCodeResource.setSicCode(sicCode.asText());
            sicCodeResources.add(sicCodeResource);
        });
        return sicCodeResources;

    }

    private List<OrganisationExecutiveOfficerResource> getCurrentDirectors(JsonNode jsonNode, String pathName) {
        List<OrganisationExecutiveOfficerResource> executiveOfficers = new ArrayList<>();
        JsonNode directorsDetails = jsonNode.path("items");
        directorsDetails.forEach(directorItem -> {
            if (directorItem.get("resigned_on") == null) {
                String officerRole = directorItem.get("officer_role").asText();
                if (!officerRole.isEmpty() && officerRole.equalsIgnoreCase(DIRECTOR)) {
                    OrganisationExecutiveOfficerResource executiveOfficerResource = new OrganisationExecutiveOfficerResource();
                    executiveOfficerResource.setName(directorItem.get("name").asText());
                    executiveOfficers.add(executiveOfficerResource);
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

    protected void setCompaniesHouseUrl(String companiesHouseUrl) {
        this.companiesHouseUrl = companiesHouseUrl;
    }
}