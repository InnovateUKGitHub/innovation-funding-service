package org.innovateuk.ifs.project.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.controller.PartnerOrganisationController;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;

import java.util.Collections;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PartnerOrganisationControllerDocumentation extends BaseControllerMockMVCTest<PartnerOrganisationController> {

    @Override
    protected PartnerOrganisationController supplyControllerUnderTest() {
        return new PartnerOrganisationController();
    }

    @Test
    public void getPartnerOrganisation() throws Exception {
        PartnerOrganisationResource partnerOrgResource = new PartnerOrganisationResource();
        when(partnerOrganisationServiceMock.getPartnerOrganisation(123L, 234L)).thenReturn(serviceSuccess(partnerOrgResource));

        mockMvc.perform(get("/project/{projectId}/partner/{organisationId}", 123L, 234L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(partnerOrgResource))).
                andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to get the partner organisation of"),
                                parameterWithName("organisationId").description("Id of the organisation to get the partner organisation of")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of partner organisation"),
                                fieldWithPath("organisation").description("Organisation Id"),
                                fieldWithPath("organisationName").description("Organisation name"),
                                fieldWithPath("leadOrganisation").description("If the partner organisation is the lead partner"),
                                fieldWithPath("project").description("Project id"))
                ));
    }

    @Test
    public void getPartnerOrganisations() throws Exception {
        PartnerOrganisationResource partnerOrgResource = new PartnerOrganisationResource();
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(123L)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrgResource)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation", 123L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(Collections.singletonList(partnerOrgResource)))).
                andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to get the partner organisations of")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("Id of partner organisation"),
                                fieldWithPath("[].organisation").description("Organisation Id"),
                                fieldWithPath("[].organisationName").description("Organisation name"),
                                fieldWithPath("[].leadOrganisation").description("If the partner organisation is the lead partner"),
                                fieldWithPath("[].project").description("Project id"))
                ));
    }
}
