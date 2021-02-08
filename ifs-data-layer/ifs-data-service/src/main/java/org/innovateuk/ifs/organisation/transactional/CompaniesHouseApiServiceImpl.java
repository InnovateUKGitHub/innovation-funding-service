package org.innovateuk.ifs.organisation.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

    private static final String EMPTY_NAME_STRING = " ";

    @Autowired
    @Qualifier("companieshouse_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText, int indexPos) {
        LOG.debug("searchOrganisations " + encodedSearchText);
        return decodeString(encodedSearchText).andOnSuccess(decodedSearchText ->
        {
            if (isImprovedSearchEnabled) {
                return improvedSearchOrganisationByIndex(indexPos, decodedSearchText);
            } else {
                return searchOrganisations(decodedSearchText);
            }
        });
    }

    private ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String decodedSearchText) {
        //seearch organisation
        JsonNode organisationsResources = restGet(COMPANIES_HOUSE_SEARCH_PATH, JsonNode.class, organisationSearchUrlVariables(decodedSearchText));
        return getSearchOrganisationResults(organisationsResources);
    }

    private ServiceResult<List<OrganisationSearchResult>> improvedSearchOrganisationByIndex(int indexPos, String decodedSearchText) {
        // search organsiation by index
        JsonNode organisationsResources = restGet(COMPANIES_HOUSE_SEARCH_BY_INDEX_PATH, JsonNode.class,
                organisationSearchUrlVariablesWithIndex(decodedSearchText, indexPos));
        String totalResults = organisationsResources.path("total_results").asText();
        return getImprovedSearchOrganisationResults(organisationsResources, totalResults);
    }

    @Override
    public ServiceResult<OrganisationSearchResult> getOrganisationById(String id) {
        LOG.debug("getOrganisationById " + id);
        if (isImprovedSearchEnabled) {
            return getOrganisationDetailsWithSicCodesAndDirectors(id);
        }
        return getOrganisationBasicProfile(id);
    }

    private ServiceResult<OrganisationSearchResult> getOrganisationBasicProfile(String id) {
        return getOrganisationDetails(id).
                map(jsonNode -> serviceSuccess(organisationProfileMapper(jsonNode))).
                orElse(serviceFailure(COMPANIES_HOUSE_NO_RESPONSE));
    }

    private ServiceResult<OrganisationSearchResult> getOrganisationDetailsWithSicCodesAndDirectors(String id) {
        Optional<JsonNode> organisationDetails = getOrganisationDetails(id);
        Optional<JsonNode> directorsDetails = getDirectorsDetails(id);
        OrganisationSearchResult orgResult = organisationProfileSicCodeDirectorsMapper(organisationDetails, directorsDetails);
        return serviceSuccess(orgResult);
    }

    private ServiceResult<List<OrganisationSearchResult>> getSearchOrganisationResults(JsonNode companiesResources) {
        JsonNode organisationItems = companiesResources.path("items");
        List<OrganisationSearchResult> results = new ArrayList<>();
        organisationItems.forEach(i -> results.add(organisationSearchMapper(i)));
        return serviceSuccess(results);
    }

    private ServiceResult<List<OrganisationSearchResult>> getImprovedSearchOrganisationResults(JsonNode organisationsResources, String totalResults) {
        JsonNode organisationItems = organisationsResources.path("items");
        List<OrganisationSearchResult> results = new ArrayList<>();
        organisationItems.forEach(i -> results.add(improvedOrganisationSearchMapper(i, totalResults)));
        return serviceSuccess(results);
    }

    private Optional<JsonNode> getOrganisationDetails(String companiesHouseNo) {
        return ofNullable(restGet("company/" + companiesHouseNo, JsonNode.class));
    }

    private Optional<JsonNode> getDirectorsDetails(String companiesHouseNo) {
        return ofNullable(restGet("company/" + companiesHouseNo + COMPANIES_HOUSE_LIST_DIRECTORS_PATH,
                JsonNode.class, listDirectorsUrlVariables()));
    }

    private OrganisationSearchResult organisationSearchMapper(JsonNode jsonNode) {
        AddressResource officeAddress = getAddress(jsonNode, "address");
        OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("title").asText());
        Map<String, Object> extras = getExtraAttributesOfCHSearch(jsonNode);
        org.setExtraAttributes(extras);
        org.setOrganisationAddress(officeAddress);
        return org;
    }


    private OrganisationSearchResult improvedOrganisationSearchMapper(JsonNode jsonNode, String totalResults) {
        AddressResource officeAddress = getImprovedSearchDisplayAddress(jsonNode);
        OrganisationSearchResult org = getOrganisationBasicDetailsFromSearchResults(jsonNode);

        Map<String, Object> extras = getExtraAttributesOfCHSearch(jsonNode);
        extras.put("total_results", totalResults);
        org.setExtraAttributes(extras);
        org.setOrganisationAddress(officeAddress);
        return org;
    }

    private AddressResource getImprovedSearchDisplayAddress(JsonNode jsonNode) {
        String addressSnippet = jsonNode.path("address_snippet").asText();
        addressSnippet = addressSnippet.equals("null") ? EMPTY_NAME_STRING : addressSnippet;
        AddressResource officeAddress = new AddressResource(addressSnippet);
        return officeAddress;
    }

    private OrganisationSearchResult getOrganisationBasicDetailsFromSearchResults(JsonNode organisationNode) {
        String organisationName = organisationNode.path("title").asText();
        String registrationNumber = organisationNode.path("company_number").asText();
        String organisationStatus = organisationNode.path("company_status").asText();
        OrganisationSearchResult orgSearchResult = new OrganisationSearchResult(registrationNumber, organisationName);
        orgSearchResult.setOrganisationStatus(organisationStatus);
        return orgSearchResult;
    }

    private Map<String, Object> getExtraAttributesOfCHSearch(JsonNode jsonNode) {
        Map<String, Object> extras = new HashMap<>();
        extras.put("company_type", jsonNode.path("company_type").asText());
        extras.put("date_of_creation", jsonNode.path("date_of_creation").asText());
        extras.put("description", jsonNode.path("description").asText());
        return extras;
    }
    private OrganisationSearchResult organisationProfileMapper(JsonNode jsonNode) {
        AddressResource officeAddress = getAddress(jsonNode, "registered_office_address");
        ObjectMapper mapper = new ObjectMapper();

        OrganisationSearchResult org = new OrganisationSearchResult(jsonNode.path("company_number").asText(), jsonNode.path("company_name").asText());
        org.setExtraAttributes(mapper.convertValue(jsonNode, Map.class));
        org.setOrganisationAddress(officeAddress);
        return org;
    }

    private OrganisationSearchResult organisationProfileSicCodeDirectorsMapper(Optional<JsonNode> organisationDetails, Optional<JsonNode> directorDetails) {
        if (organisationDetails.isPresent()) {
            JsonNode organisationItemsNode = organisationDetails.get();
            AddressResource registeredOfficeAddress = getAddress(organisationItemsNode, "registered_office_address");
            ObjectMapper mapper = new ObjectMapper();

            OrganisationSearchResult org = new OrganisationSearchResult(organisationItemsNode.path("company_number").asText(), organisationItemsNode.path("company_name").asText());
            org.setExtraAttributes(mapper.convertValue(organisationItemsNode, Map.class));
            org.setOrganisationAddress(registeredOfficeAddress);

            List<OrganisationSicCodeResource> sicCodeResources = getSicCode(organisationItemsNode, "sic_codes");
            org.setOrganisationSicCodes(sicCodeResources);

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
     * @param c    - Return class type
     * @param <T>  - Return data type from the method
     * @return
     */
    protected <T> T restGet(String path, Class<T> c, Map<String, Object> variables) {
        try {
            return adaptor.restGetEntity(companiesHouseUrl + path, c, variables).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOG.error(e);
            return null;
        }
    }

    /**
     * Method to build the query variable map. The keys in the map must match the
     * keys defined in the URL path 'COMPANIES_HOUSE_SEARCH_PATH'.
     *
     * @param searchWord
     * @return {@link Map}
     */
    private Map<String, Object> organisationSearchUrlVariables(String searchWord) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ITEMS_PER_PAGE_KEY, SEARCH_ITEMS_MAX);
        variables.put(SEARCH_WORD_KEY, searchWord);
        return variables;
    }

    private Map<String, Object> organisationSearchUrlVariablesWithIndex(String searchWord, int indexPos) {
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
        if (sicCodesArray != null) {
            sicCodesArray.forEach(sicCode -> {
                setSicCodeValue(sicCodeResources, sicCode.asText());
            });
        } else {
            setSicCodeValue(sicCodeResources, EMPTY_NAME_STRING);
        }
        return sicCodeResources;

    }

    private void setSicCodeValue(List<OrganisationSicCodeResource> sicCodeResources, String sicCode) {
        OrganisationSicCodeResource sicCodeResource = new OrganisationSicCodeResource();
        sicCodeResource.setSicCode(sicCode);
        sicCodeResources.add(sicCodeResource);
    }

    private List<OrganisationExecutiveOfficerResource> getCurrentDirectors(JsonNode jsonNode, String pathName) {
        List<OrganisationExecutiveOfficerResource> executiveOfficersResource = new ArrayList<>();
        JsonNode directorsDetails = jsonNode.path("items");
        if (directorsDetails != null) {
            directorsDetails.forEach(directorItem -> {
                if (directorItem.get("resigned_on") == null) {
                    String officerRole = directorItem.get("officer_role").asText();
                    if (!officerRole.isEmpty() && officerRole.equalsIgnoreCase(DIRECTOR)) {
                        setDirectorsValue(executiveOfficersResource, directorItem.get("name").asText());
                    } else {
                        setDirectorsValue(executiveOfficersResource, EMPTY_NAME_STRING);
                    }
                }
            });
        } else {
            setDirectorsValue(executiveOfficersResource, EMPTY_NAME_STRING);
        }
        return executiveOfficersResource;
    }

    private void setDirectorsValue(List<OrganisationExecutiveOfficerResource> executiveOfficers, String directorName) {
        OrganisationExecutiveOfficerResource executiveOfficerResource = new OrganisationExecutiveOfficerResource();
        executiveOfficerResource.setName(directorName);
        executiveOfficers.add(executiveOfficerResource);
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