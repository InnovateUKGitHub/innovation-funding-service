package org.innovateuk.ifs.organisation.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.controller.CompanyHouseController;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.transactional.CompanyHouseApiService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

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


public class CompaniesHouseControllerDocumentation extends BaseControllerMockMVCTest<CompanyHouseController> {

    @Mock
    private CompanyHouseApiService companyHouseService;

    private OrganisationSearchResult organisationSearchResults;

    @Override
    protected CompanyHouseController supplyControllerUnderTest() {
        return new CompanyHouseController();
    }

    @Before
    public void setUp() throws Exception {
        AddressResource addressResource = newAddressResource()
                .withId(1L)
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
                .build();
    }

    @Test
    public void searchByCompaniesHouseName() throws Exception {

        String searchText = "Batman Robin";

        when(companyHouseService.searchOrganisations(any()))
                .thenReturn(ServiceResult.serviceSuccess(Arrays.asList(organisationSearchResults)));

        mockMvc.perform(get("/companyhouse/searchCompanyHouse/{searchText}",searchText)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(organisationSearchResults))))
                .andExpect(status().isOk())
                .andDo(document("companies-house/{method-name}",
                        pathParameters(
                                parameterWithName("searchText").description("Name of the Organisation to search.")
                        ),
                        responseFields(fieldWithPath("[]").description("List of Organisation search results"))
                                .andWithPrefix("[].", organisationSearchResultFields())
                ));

        verify(companyHouseService,only()).searchOrganisations(searchText);
    }

    @Test
    public void searchByCompaniesHouseNumber() throws Exception {

        String id = "08241216";

        when(companyHouseService.getOrganisationById(any()))
                .thenReturn(ServiceResult.serviceSuccess(organisationSearchResults));

        mockMvc.perform(get("/companyhouse/getCompanyHouse/{id}",id)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(organisationSearchResults)))
                .andDo(document("companies-house/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the Organisation to search.")
                        ),
                        responseFields(organisationSearchResultFields())
                ));
        verify(companyHouseService,only()).getOrganisationById(id);
    }
}
