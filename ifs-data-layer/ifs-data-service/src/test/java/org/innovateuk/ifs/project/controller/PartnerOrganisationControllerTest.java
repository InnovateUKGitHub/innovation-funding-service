package org.innovateuk.ifs.project.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.junit.Test;

import java.util.Collections;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PartnerOrganisationControllerTest extends BaseControllerMockMVCTest<PartnerOrganisationController> {

    @Override
    protected PartnerOrganisationController supplyControllerUnderTest() {
        return new PartnerOrganisationController();
    }

    @Test
    public void testGetProjectPartner() throws Exception {
        Long projectId = 123L;
        Long organisationId = 234L;
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();
        when(partnerOrganisationServiceMock.getPartnerOrganisation(projectId, organisationId)).thenReturn(serviceSuccess(partnerOrg));
        mockMvc.perform(get("/project/{projectId}/partner/{organisationId}", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(partnerOrg)));
    }

    @Test
    public void testGetProjectPartners() throws Exception {
        Long projectId = 123L;
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrg)));
        mockMvc.perform(get("/project/{projectId}/partner-organisation", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Collections.singletonList(partnerOrg))));
    }
}
