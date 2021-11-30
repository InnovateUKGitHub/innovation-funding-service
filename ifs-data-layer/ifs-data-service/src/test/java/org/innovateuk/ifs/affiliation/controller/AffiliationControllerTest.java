package org.innovateuk.ifs.affiliation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder.newAffiliationListResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AffiliationControllerTest extends BaseControllerMockMVCTest<AffiliationController> {

    @Mock
    private AffiliationService affiliationServiceMock;

    @Override
    protected AffiliationController supplyControllerUnderTest() {
        return new AffiliationController();
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);
        AffiliationListResource affiliationListResource = newAffiliationListResource()
                .withAffiliationList(affiliations)
                .build();

        when(affiliationServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(affiliationListResource));

        mockMvc.perform(get("/affiliation/id/{id}/get-user-affiliations", userId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(affiliationListResource)));

        verify(affiliationServiceMock, only()).getUserAffiliations(userId);
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);
        AffiliationListResource affiliationListResource = newAffiliationListResource()
                .withAffiliationList(affiliations)
                .build();

        when(affiliationServiceMock.updateUserAffiliations(userId, affiliationListResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/affiliation/id/{id}/update-user-affiliations", userId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(affiliationListResource)))
                .andExpect(status().isOk());

        verify(affiliationServiceMock, only()).updateUserAffiliations(userId, affiliationListResource);
    }
}
