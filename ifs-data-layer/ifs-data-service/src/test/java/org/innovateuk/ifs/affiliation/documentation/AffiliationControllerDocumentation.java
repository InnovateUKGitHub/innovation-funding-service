package org.innovateuk.ifs.affiliation.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.affiliation.controller.AffiliationController;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AffiliationDocs.affiliationResourceBuilder;
import static org.innovateuk.ifs.documentation.AffiliationDocs.affiliationResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AffiliationControllerDocumentation extends BaseControllerMockMVCTest<AffiliationController> {

    @Override
    protected AffiliationController supplyControllerUnderTest() {
        return new AffiliationController();
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> responses = affiliationResourceBuilder.build(2);
        when(affiliationServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/affiliation/id/{id}/getUserAffiliations", userId))
                .andExpect(status().isOk())
                .andDo(document("affiliation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with affiliations being requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of affiliations belonging to the user")
                        ).andWithPrefix("[].", affiliationResourceFields)
                ));
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = affiliationResourceBuilder
                .build(2);
        when(affiliationServiceMock.updateUserAffiliations(userId, affiliations)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/affiliation/id/{id}/updateUserAffiliations", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(affiliations)))
                .andExpect(status().isOk())
                .andDo(document("affiliation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Identifier of the user associated with affiliations being updated")
                        ),
                        requestFields(fieldWithPath("[]").description("List of affiliations belonging to the user"))
                                .andWithPrefix("[].", affiliationResourceFields)
                ));
    }
}
