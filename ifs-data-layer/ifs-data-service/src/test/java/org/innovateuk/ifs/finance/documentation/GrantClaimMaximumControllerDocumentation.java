package org.innovateuk.ifs.finance.documentation;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.controller.GrantClaimMaximumController;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.GrantClaimMaximumDocs.grantClaimMaximumResourceFields;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GrantClaimMaximumControllerDocumentation extends MockMvcTest<GrantClaimMaximumController> {

    @Mock
    private GrantClaimMaximumService grantClaimMaximumService;

    @Override
    public GrantClaimMaximumController supplyControllerUnderTest() {
        return new GrantClaimMaximumController(grantClaimMaximumService);
    }

    @Test
    public void getGrantClaimMaximumById() throws Exception {
        final Long grantClaimMaximumId = 1L;

        when(grantClaimMaximumService.getGrantClaimMaximumById(grantClaimMaximumId)).thenReturn(serviceSuccess(newGrantClaimMaximumResource().build()));

        mockMvc.perform(get("/grant-claim-maximum/{id}", grantClaimMaximumId))
                .andExpect(status().isOk())
                .andDo(document(
                        "grant-claim-maximum/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the grantClaimMaximum to be retrieved")
                        ),
                        responseFields(grantClaimMaximumResourceFields)
                ));
    }

    @Test
    public void save() throws Exception {
        GrantClaimMaximumResource gcm = newGrantClaimMaximumResource().build();
        when(grantClaimMaximumService.save(any(GrantClaimMaximumResource.class))).thenReturn(serviceSuccess(gcm));

        mockMvc.perform(post("/grant-claim-maximum/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson((gcm))))
                .andExpect(status().isCreated())
                .andDo(document(
                        "grant-claim-maximum/{method-name}",
                        responseFields(grantClaimMaximumResourceFields)
                ));
    }

    @Test
    public void getGrantClaimMaximumsForCompetitionType() throws Exception {
        Long competitionType = 1L;
        Set<Long> expectedGcms = CollectionFunctions.asLinkedSet(2L, 3L);
        when(grantClaimMaximumService.getGrantClaimMaximumsForCompetitionType(competitionType)).thenReturn(serviceSuccess(expectedGcms));

        mockMvc.perform(get("/grant-claim-maximum/get-for-competition-type/{competitionTypeId}", competitionType))
                .andExpect(status().isOk())
                .andDo(document(
                        "grant-claim-maximum/{method-name}",
                        pathParameters(
                                parameterWithName("competitionTypeId").description("id of the CompetitionType to be retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("Set of Grant Claim Maximums for given CompetitionType"))
                ));
    }
}