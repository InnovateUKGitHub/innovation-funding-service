package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.AddressDocs;
import org.innovateuk.ifs.documentation.AddressTypeResourceDocs;
import org.innovateuk.ifs.documentation.OrganisationAddressDocs;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationInitialCreationService;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.OrganisationDocs.organisationResourceBuilder;
import static org.innovateuk.ifs.documentation.OrganisationDocs.organisationResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationControllerDocumentation extends BaseControllerMockMVCTest<OrganisationController> {

    @Mock
    private OrganisationService organisationServiceMock;

    @Mock
    private OrganisationInitialCreationService organisationInitialCreationServiceMock;

    @Override
    protected OrganisationController supplyControllerUnderTest() {
        return new OrganisationController();
    }

    @Test
    public void findByApplicationId() throws Exception {
        long applicationId = 1L;
        Set<OrganisationResource> organisationResourceSet = organisationResourceBuilder.buildSet(1);
        when(organisationServiceMock.findByApplicationId(applicationId)).thenReturn(serviceSuccess(organisationResourceSet));

        mockMvc.perform(get("/organisation/find-by-application-id/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("organisation/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Identifier of the application to find the organisations for")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of organisations for the selected application")
                        ).andWithPrefix("[].", organisationResourceFields)
                        .andWithPrefix("[].addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                        .andWithPrefix("[].addresses.[].address.", AddressDocs.addressResourceFields)
                        .andWithPrefix("[].addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }

    @Test
    public void findById() throws Exception {
        long organisationId = 1L;
        OrganisationResource organisationResource = organisationResourceBuilder.build();
        when(organisationServiceMock.findById(organisationId)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(get("/organisation/find-by-id/{organisationId}", organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("organisation/{method-name}",
                        pathParameters(
                                parameterWithName("organisationId").description("Identifier of the organisation to find")
                        ),
                        responseFields(organisationResourceFields)
                        .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                        .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                        .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }

    @Test
    public void getByUserAndApplicationId() throws Exception {
        long userId = 1L;
        long applicationId = 2L;
        OrganisationResource organisationResource = organisationResourceBuilder.build();

        when(organisationServiceMock.getByUserAndApplicationId(userId, applicationId)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(get("/organisation/by-user-and-application-id/{userId}/{applicationId}", userId, applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("organisation/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user to find the application organisation for"),
                                parameterWithName("applicationId").description("Identifier of the application to find the application organisation for")
                        ),
                        responseFields(organisationResourceFields)
                        .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                        .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                        .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }

    @Test
    public void getByUserAndProjectId() throws Exception {
        long userId = 1L;
        long projectId = 2L;
        OrganisationResource organisationResource = organisationResourceBuilder.build();

        when(organisationServiceMock.getByUserAndProjectId(userId, projectId)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(get("/organisation/by-user-and-project-id/{userId}/{projectId}", userId, projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("organisation/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Identifier of the user to find the project organisation for"),
                                parameterWithName("projectId").description("Identifier of the project to find the project organisation for")
                        ),
                        responseFields(organisationResourceFields)
                        .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                        .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                        .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }

    @Test
    public void createOrMatch() throws Exception {
        OrganisationResource organisationResource = organisationResourceBuilder.build();

        when(organisationInitialCreationServiceMock.createOrMatch(organisationResource)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(post("/organisation/create-or-match")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(organisationResource)))
                .andExpect(status().isCreated())
                .andDo(document("organisation/{method-name}",
                        requestFields(organisationResourceFields)
                                .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                                .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                                .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields),
                        responseFields(organisationResourceFields)
                                .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                                .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                                .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }

    @Test
    public void saveResource() throws Exception {
        OrganisationResource organisationResource = organisationResourceBuilder.build();

        when(organisationServiceMock.update(organisationResource)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(put("/organisation/update")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(organisationResource)))
                .andExpect(status().isOk())
                .andDo(document("organisation/{method-name}",
                        requestFields(organisationResourceFields)
                                .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                                .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                                .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields),
                        responseFields(organisationResourceFields)
                                .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                                .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                                .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }

    @Test
    public void updateNameAndRegistration() throws Exception {
        long organisationId = 1L;
        String name = "name";
        String registration = "registration";
        OrganisationResource organisationResource = organisationResourceBuilder.build();

        when(organisationServiceMock.updateOrganisationNameAndRegistration(organisationId, name, registration)).thenReturn(serviceSuccess(organisationResource));

        mockMvc.perform(post("/organisation/update-name-and-registration/{organisationId}", organisationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("name", name)
                .param("registration", registration))
                .andExpect(status().isCreated())
                .andDo(document("organisation/{method-name}",
                        pathParameters(
                                parameterWithName("organisationId").description("The identifier of the organisation being updated")
                        ),
                        requestParameters(
                                parameterWithName("name").description("The name of the organisation"),
                                parameterWithName("registration").description("The companies house number")
                        ),
                        responseFields(organisationResourceFields)
                                .andWithPrefix("addresses.[].", OrganisationAddressDocs.organisationAddressResourceFields)
                                .andWithPrefix("addresses.[].address.", AddressDocs.addressResourceFields)
                                .andWithPrefix("addresses.[].addressType.", AddressTypeResourceDocs.addressTypeResourceFields)
                ));
    }
}
