package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.InviteOrganisationDocs.inviteOrganisationFields;
import static org.innovateuk.ifs.documentation.InviteOrganisationDocs.inviteOrganisationResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteOrganisationControllerDocumentation extends BaseControllerMockMVCTest<InviteOrganisationController> {

    @Override
    protected InviteOrganisationController supplyControllerUnderTest() {
        return new InviteOrganisationController();
    }

    @Test
    public void getById() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = inviteOrganisationResourceBuilder.build();

        when(inviteOrganisationServiceMock.getById(inviteOrganisationResource.getId()))
                .thenReturn(serviceSuccess(inviteOrganisationResource));

        mockMvc.perform(get("/inviteorganisation/{id}", inviteOrganisationResource.getId()))
                .andExpect(status().isOk())
                .andDo(document("invite-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the invite organisation that is being requested")
                        ),
                        responseFields(inviteOrganisationFields)
                ));
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() throws Exception {
        InviteOrganisationResource inviteOrganisationResource = inviteOrganisationResourceBuilder.build();
        long organisationId = 1L;
        long applicationId = 1L;

        when(inviteOrganisationServiceMock.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId))
                .thenReturn(serviceSuccess(inviteOrganisationResource));

        mockMvc.perform(get("/inviteorganisation/organisation/{organisationId}/application/{applicationId}",
                organisationId, applicationId))
                .andExpect(status().isOk())
                .andDo(document("invite-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("organisationId").description("Id of the organisation that invite organisations are being requested for"),
                                parameterWithName("applicationId").description("Id of the application that invites are being requested for")
                        ),
                        responseFields(inviteOrganisationFields)
                ));
    }
}