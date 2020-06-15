package org.innovateuk.ifs.project.core.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.core.controller.PartnerOrganisationController;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PartnerOrganisationControllerDocumentation extends BaseControllerMockMVCTest<PartnerOrganisationController> {

    @Mock
    private PartnerOrganisationService partnerOrganisationServiceMock;

    @Override
    protected PartnerOrganisationController supplyControllerUnderTest() {
        return new PartnerOrganisationController();
    }

    @Test
    public void getPartnerOrganisation() throws Exception {
        PartnerOrganisationResource partnerOrgResource = new PartnerOrganisationResource();
        when(partnerOrganisationServiceMock.getPartnerOrganisation(123L, 234L)).thenReturn(serviceSuccess(partnerOrgResource));

        mockMvc.perform(get("/project/{projectId}/partner/{organisationId}", 123L, 234L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(partnerOrgResource)))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to get the partner organisation of"),
                                parameterWithName("organisationId").description("Id of the organisation to get the partner organisation of")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of partner organisation"),
                                fieldWithPath("organisation").description("Organisation Id"),
                                fieldWithPath("organisationName").description("Organisation name"),
                                fieldWithPath("leadOrganisation").description("If the partner organisation is the lead partner"),
                                fieldWithPath("postcode").description("The project location for this partner"),
                                fieldWithPath("internationalLocation").description("The international project location for this partner"),
                                fieldWithPath("project").description("Project id"),
                                fieldWithPath("internationalAddress").description("The addresses of the international organisation for this project.").optional())
                ));
    }

    @Test
    public void getPartnerOrganisations() throws Exception {
        PartnerOrganisationResource partnerOrgResource = new PartnerOrganisationResource();
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(123L)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrgResource)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation", 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Collections.singletonList(partnerOrgResource))))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to get the partner organisations of")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("Id of partner organisation"),
                                fieldWithPath("[].organisation").description("Organisation Id"),
                                fieldWithPath("[].organisationName").description("Organisation name"),
                                fieldWithPath("[].leadOrganisation").description("If the partner organisation is the lead partner"),
                                fieldWithPath("[].postcode").description("The project location for this partner"),
                                fieldWithPath("[].internationalLocation").description("The international project location for this partner"),
                                fieldWithPath("[].project").description("Project id"),
                                fieldWithPath("[].internationalAddress").description("The addresses of the international organisation for this project.").optional())

                ));
    }

    @Test
    public void removePartnerOrganisation() throws Exception {
        long projectId = 123;
        long organisationId = 456;

        when(partnerOrganisationServiceMock.removePartnerOrganisation(id(projectId, organisationId))).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/remove-organisation/{organisationId}", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the organisation is part of"),
                                parameterWithName("organisationId").description("Id of organisation to remove")
                        )
                ));
    }
}
