package com.worth.ifs.organisation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.organisation.resource.PostalAddress;
import com.worth.ifs.security.NotSecured;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class communicates with the Company House API.
 * This is used to get information abouts companies.
 * @see <a href="https://developer.companieshouse.gov.uk/api/docs/">Company House API site</a>
 */
@Service
public class CompanyHouseApi extends BaseRestService {
    @Value("${ifs.data.company-house.url}")
    private final String COMPANY_HOUSE_API = null;

    @Value("${ifs.data.company-house.key}")
    private final String COMPANY_HOUSE_KEY = null;

    private static final int SEARCH_ITEMS_MAX = 20;
    private final Log log = LogFactory.getLog(getClass());

    public CompanyHouseApi() {
        super();
        this.setDataRestServiceUrl(COMPANY_HOUSE_API);
    }

    @NotSecured("These services are not secured because the company house api are open to use for everyone.")
    public List<CompanyHouseBusiness> searchOrganisationsByName(String name){
        this.setDataRestServiceUrl(COMPANY_HOUSE_API);
        JsonNode companiesResources = restGet("search/companies?items_per_page="+ SEARCH_ITEMS_MAX +"&q=" + name, JsonNode.class);
        JsonNode companyItems = companiesResources.path("items");
        List<CompanyHouseBusiness> results = new ArrayList<>();
        companyItems.forEach(i -> results.add(companySearchMapper(i)));
        return results;
    }
    private CompanyHouseBusiness companySearchMapper(JsonNode jsonNode){
        PostalAddress officeAddress = getPostalAddress(jsonNode, "address");
        return new CompanyHouseBusiness(
                jsonNode.path("company_number").asText(),
                jsonNode.path("title").asText(),
                jsonNode.path("company_type").asText(),
                jsonNode.path("date_of_creation").asText(),
                jsonNode.path("description").asText(),
                officeAddress);
    }

    @NotSecured("These services are not secured because the company house api are open to use for everyone.")
    public CompanyHouseBusiness getOrganisationById(String id){
        log.debug("getOrganisationById "+ id);
        this.setDataRestServiceUrl(COMPANY_HOUSE_API);

        JsonNode jsonNode = null;
        try{
            jsonNode = restGet("company/" + id, JsonNode.class);
        }catch(Exception e){
            log.error("Exception: " + e.getMessage());
            jsonNode = null;
        }

        if (jsonNode == null){
            return null;
        }else{
            return companyProfileMapper(jsonNode);
        }
    }

    private CompanyHouseBusiness companyProfileMapper(JsonNode jsonNode){
        String description = null;
        PostalAddress officeAddress = getPostalAddress(jsonNode, "registered_office_address");
        return new CompanyHouseBusiness(
                jsonNode.path("company_number").asText(),
                jsonNode.path("company_name").asText(),
                jsonNode.path("type").asText(),
                jsonNode.path("date_of_creation").asText(),
                description,
                officeAddress);
    }

    private PostalAddress getPostalAddress(JsonNode jsonNode, String path) {
        PostalAddress address = new PostalAddress(
                jsonNode.path(path).path("address_line_1").asText(),
                jsonNode.path(path).path("address_line_2").asText(),
                jsonNode.path(path).path("care_of").asText(),
                jsonNode.path(path).path("country").asText(),
                jsonNode.path(path).path("locality").asText(),
                jsonNode.path(path).path("po_box").asText(),
                jsonNode.path(path).path("postal_code").asText(),
                jsonNode.path(path).path("region").asText()
        );
        return address;
    }

    @NotSecured("")
    public HttpHeaders getHeaders(){
        log.debug("Adding authorization headers");
        HttpHeaders headers = super.getHeaders();

        String auth = COMPANY_HOUSE_KEY + ":";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String( encodedAuth );

        headers.add("Authorization", authHeader);
        return headers;
    }
}
