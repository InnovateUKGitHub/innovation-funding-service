package org.innovateuk.ifs.project.core.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PartnerOrganisationControllerTest extends BaseControllerMockMVCTest<PartnerOrganisationController> {

    @Mock
    private PartnerOrganisationService partnerOrganisationService;

    @Override
    protected PartnerOrganisationController supplyControllerUnderTest() {
        return new PartnerOrganisationController();
    }

    @Test
    public void getProjectPartner() throws Exception {
        Long projectId = 123L;
        Long organisationId = 234L;
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();
        when(partnerOrganisationService.getPartnerOrganisation(projectId, organisationId)).thenReturn(serviceSuccess(partnerOrg));
        mockMvc.perform(get("/project/{projectId}/partner/{organisationId}", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(partnerOrg)));
    }

    @Test
    public void getProjectPartners() throws Exception {
        Long projectId = 123L;
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().build();
        when(partnerOrganisationService.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrg)));
        mockMvc.perform(get("/project/{projectId}/partner-organisation", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(Collections.singletonList(partnerOrg))));
    }

    @Test
    public void removeOrganisation() throws Exception {
        long projectId = 123;
        long organisationId = 456;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = id(projectId, organisationId);

        when(partnerOrganisationService.removePartnerOrganisation(projectOrganisationCompositeId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/remove-organisation/{organisationId}", projectId, organisationId))
                .andExpect(status().isOk());

        verify(partnerOrganisationService).removePartnerOrganisation(projectOrganisationCompositeId);
    }
}
