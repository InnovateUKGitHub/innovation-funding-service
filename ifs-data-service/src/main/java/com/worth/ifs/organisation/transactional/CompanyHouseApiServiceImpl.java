package com.worth.ifs.organisation.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.RestTemplateAdaptor;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static java.util.Optional.ofNullable;

/**
 * This class communicates with the Company House API.
 * This is used to get information abouts companies.
 *
 * @see <a href="https://developer.companieshouse.gov.uk/api/docs/">Company House API site</a>
 */
@Service
public class CompanyHouseApiServiceImpl extends BaseRestService implements CompanyHouseApiService {

    private static final Log LOG = LogFactory.getLog(CompanyHouseApiServiceImpl.class);

    @Value("${ifs.data.company-house.url}")
    private final String COMPANY_HOUSE_API = null;

    @Value("${ifs.data.company-house.key}")
    private final String COMPANY_HOUSE_KEY = null;

    private static final int SEARCH_ITEMS_MAX = 20;

    @Override
    protected String getDataRestServiceURL() {
        return COMPANY_HOUSE_API;
    }

    public ServiceResult<List<CompanyHouseBusiness>> searchOrganisations(String encodedSearchText) {

        return decodeString(encodedSearchText).andOnSuccess(decodedSearchText -> {

            // encoded in the web-services.
            JsonNode companiesResources = restGet("search/companies?items_per_page=" + SEARCH_ITEMS_MAX + "&q=" + decodedSearchText, JsonNode.class, getHeaders());
            JsonNode companyItems = companiesResources.path("items");
            List<CompanyHouseBusiness> results = new ArrayList<>();
            companyItems.forEach(i -> results.add(companySearchMapper(i)));
            return serviceSuccess(results);
        });
    }

    public ServiceResult<CompanyHouseBusiness> getOrganisationById(String id) {
        LOG.debug("getOrganisationById " + id);

        return ofNullable(restGet("company/" + id, JsonNode.class, getHeaders())).
            map(jsonNode -> serviceSuccess(companyProfileMapper(jsonNode))).
            orElse(serviceFailure(internalServerErrorError("No response from Companies House")));
    }

    private HttpHeaders getHeaders() {
        LOG.debug("Adding authorization headers");
        HttpHeaders headers = RestTemplateAdaptor.getHeaders();

        String auth = COMPANY_HOUSE_KEY + ":";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);

        headers.add("Authorization", authHeader);
        return headers;
    }

    private CompanyHouseBusiness companyProfileMapper(JsonNode jsonNode) {
        String description = null;
        AddressResource officeAddress = getAddress(jsonNode, "registered_office_address");
        return new CompanyHouseBusiness(
                jsonNode.path("company_number").asText(),
                jsonNode.path("company_name").asText(),
                jsonNode.path("type").asText(),
                jsonNode.path("date_of_creation").asText(),
                description,
                officeAddress);
    }

    private AddressResource getAddress(JsonNode jsonNode, String path) {
        AddressResource address = new AddressResource(
                jsonNode.path(path).path("address_line_1").asText(),
                jsonNode.path(path).path("address_line_2").asText(),
                jsonNode.path(path).path("address_line_3").asText(),
                jsonNode.path(path).path("locality").asText(),
                jsonNode.path(path).path("region").asText(),
                jsonNode.path(path).path("postal_code").asText()
                );
        return address;
    }

    private ServiceResult<String> decodeString(String encodedSearchText) {
        try {
            return serviceSuccess(UriUtils.decode(encodedSearchText, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unable to decode search string " + encodedSearchText, e);
            return serviceFailure(internalServerErrorError("Unable to decode search string"));
        }
    }

    private CompanyHouseBusiness companySearchMapper(JsonNode jsonNode) {
        AddressResource officeAddress = getAddress(jsonNode, "address");
        return new CompanyHouseBusiness(
                jsonNode.path("company_number").asText(),
                jsonNode.path("title").asText(),
                jsonNode.path("company_type").asText(),
                jsonNode.path("date_of_creation").asText(),
                jsonNode.path("description").asText(),
                officeAddress);
    }
}
