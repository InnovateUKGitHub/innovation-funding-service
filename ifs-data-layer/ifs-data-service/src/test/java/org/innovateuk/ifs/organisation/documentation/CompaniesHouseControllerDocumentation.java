package org.innovateuk.ifs.organisation.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.controller.CompaniesHouseController;
import org.innovateuk.ifs.organisation.domain.SicCode;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.transactional.CompaniesHouseApiService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.documentation.CompaniesHouseDocs.organisationSearchResultFields;
import static org.innovateuk.ifs.organisation.builder.OrganisationSearchResultBuilder.newOrganisationSearchResult;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CompaniesHouseControllerDocumentation extends BaseControllerMockMVCTest<CompaniesHouseController> {

    @Mock
    private CompaniesHouseApiService companyHouseService;

    private OrganisationSearchResult organisationSearchResults;

    @Override
    protected CompaniesHouseController supplyControllerUnderTest() {
        return new CompaniesHouseController();
    }

    @Before
    public void setUp() throws Exception {
        AddressResource addressResource = newAddressResource()
                .withAddressLine1("one")
                .withAddressLine2("two")
                .withAddressLine3("three")
                .withPostcode("rg8 7te")
                .withTown("reading")
                .withCounty("england")
                .build();
        Map<String, Object> map = Collections.singletonMap("Key","Value");

        organisationSearchResults = newOrganisationSearchResult()
                .withName("testingThisName")
                .withOrganisationSearchId("1")
                .withAddressResource(addressResource)
                .withextraAttributes(map)
                .withSicCodes(getSicCodes(asList("70100")))
                .withExecutiveOfficers(getDirectors( asList("EISENSCHIMMEL, Eva Kristina","HOPES, Julie")))
                .build();

    }
    private List<OrganisationSicCodeResource> getSicCodes(List<String> sicCodes) {
        List<OrganisationSicCodeResource> sicCodeResources = new ArrayList<>();
        sicCodes.forEach(sicCode -> {
            sicCodeResources.add(new OrganisationSicCodeResource(1L,sicCode));
        });
        return sicCodeResources;
    }
    private List<OrganisationExecutiveOfficerResource> getDirectors(List<String> directors) {
        List<OrganisationExecutiveOfficerResource> directorResources = new ArrayList<>();
        directors.forEach(director -> {
            directorResources.add(new OrganisationExecutiveOfficerResource(1L,director));
        });
        return directorResources;
    }

    @Test
    public void searchByCompaniesHouseName() throws Exception {
        String searchText = "Batman Robin";
        int indexPos = 0;

        when(companyHouseService.searchOrganisations(any(), anyInt()))
                .thenReturn(ServiceResult.serviceSuccess(Arrays.asList(organisationSearchResults)));

        mockMvc.perform(get("/companies-house/search/{searchText}/{indexPos}",searchText, indexPos)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(organisationSearchResults))))
                .andExpect(status().isOk())
                .andDo(document("companies-house/{method-name}",
                        pathParameters(
                                parameterWithName("searchText").description("Name of the Organisation to search."),
                                parameterWithName("indexPos").description("Position to fetch the search results")
                        ),
                        responseFields(fieldWithPath("[]").description("List of Organisation search results"))
                                .andWithPrefix("[].", organisationSearchResultFields())
                                .and(fieldWithPath("[].extraAttributes.Key").description("extra attribute"))
                ));

        verify(companyHouseService,only()).searchOrganisations(searchText, 0);
    }

    @Test
    public void getCompaniesHouseById() throws Exception {

        String id = "08241216";

        when(companyHouseService.getOrganisationById(any()))
                .thenReturn(ServiceResult.serviceSuccess(organisationSearchResults));

        mockMvc.perform(get("/companies-house/company/{id}",id)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(organisationSearchResults)))
                .andDo(document("companies-house/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the Organisation to search.")
                        ),
                        responseFields(organisationSearchResultFields())
                        .and(fieldWithPath("organisationSicCodes[].sicCode").description("Sic Codes of the organisation"))
                        .and(fieldWithPath("organisationExecutiveOfficers[].name").description("Current Director's name of the organisation"))
                        .and(fieldWithPath("extraAttributes.Key").description("extra attribute"))
                ));
        verify(companyHouseService,only()).getOrganisationById(id);
    }
}