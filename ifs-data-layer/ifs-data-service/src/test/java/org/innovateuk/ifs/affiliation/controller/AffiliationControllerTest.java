package org.innovateuk.ifs.affiliation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AffiliationControllerTest extends BaseControllerMockMVCTest<AffiliationController> {

    @Override
    protected AffiliationController supplyControllerUnderTest() {
        return new AffiliationController();
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        when(affiliationServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(affiliations));

        mockMvc.perform(get("/affiliation/id/{id}/getUserAffiliations", userId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(toJson(affiliations)));

        verify(affiliationServiceMock, only()).getUserAffiliations(userId);
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        when(affiliationServiceMock.updateUserAffiliations(userId, affiliations)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/affiliation/id/{id}/updateUserAffiliations", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(affiliations)))
                .andExpect(status().isOk());

        verify(affiliationServiceMock, only()).updateUserAffiliations(userId, affiliations);
    }
}
