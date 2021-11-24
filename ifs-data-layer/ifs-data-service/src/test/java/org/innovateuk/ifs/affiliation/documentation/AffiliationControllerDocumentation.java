package org.innovateuk.ifs.affiliation.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.affiliation.controller.AffiliationController;
import org.innovateuk.ifs.affiliation.transactional.AffiliationService;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AffiliationDocs.affiliationListResourceBuilder;
import static org.innovateuk.ifs.documentation.AffiliationDocs.affiliationResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AffiliationControllerDocumentation extends BaseControllerMockMVCTest<AffiliationController> {

    @Mock
    private AffiliationService affiliationServiceMock;

    @Override
    protected AffiliationController supplyControllerUnderTest() {
        return new AffiliationController();
    }

    @Test
    public void getUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> responses = affiliationResourceBuilder.build(2);
        when(affiliationServiceMock.getUserAffiliations(userId)).thenReturn(serviceSuccess(new AffiliationListResource(responses)));

        mockMvc.perform(get("/affiliation/id/{id}/get-user-affiliations", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserAffiliations() throws Exception {
        Long userId = 1L;
        List<AffiliationResource> affiliations = affiliationResourceBuilder
                .build(2);
        AffiliationListResource affiliationListResource = affiliationListResourceBuilder
                .build();

        when(affiliationServiceMock.updateUserAffiliations(userId, affiliationListResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/affiliation/id/{id}/update-user-affiliations", userId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(affiliationListResource)))
                .andExpect(status().isOk());
    }
}
