package org.innovateuk.ifs.finance.documentation;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.controller.GrantClaimMaximumController;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.Set;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.GrantClaimMaximumDocs.grantClaimMaximumResourceFields;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

        mockMvc.perform(get("/grant-claim-maximum/{id}", grantClaimMaximumId)
                .header("IFS_AUTH_TOKEN", "123abc"))
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
    public void getGrantClaimMaximumsForCompetitionType() throws Exception {
        Long competitionType = 1L;
        Set<Long> expectedGcms = CollectionFunctions.asLinkedSet(2L, 3L);
        when(grantClaimMaximumService.getGrantClaimMaximumsForCompetitionType(competitionType)).thenReturn(serviceSuccess(expectedGcms));

        mockMvc.perform(get("/grant-claim-maximum/get-for-competition-type/{competitionTypeId}", competitionType)
                .header("IFS_AUTH_TOKEN", "123abc"))
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

    @Test
    public void update() throws Exception {
        GrantClaimMaximumResource gcm = newGrantClaimMaximumResource().build();
        when(grantClaimMaximumService.save(any(GrantClaimMaximumResource.class))).thenReturn(serviceSuccess(gcm));

        mockMvc.perform(post("/grant-claim-maximum/")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson((gcm))))
                .andExpect(status().isCreated())
                .andDo(document(
                        "grant-claim-maximum/{method-name}",
                        responseFields(grantClaimMaximumResourceFields)
                ));
    }

    @Test
    public void isMaximumFundingLevelOverridden() throws Exception {
        long competitionId = 1L;

        when(grantClaimMaximumService.isMaximumFundingLevelOverridden(competitionId)).thenReturn(serviceSuccess
                (true));

        mockMvc.perform(get("/grant-claim-maximum/maximum-funding-level-overridden/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(true)))
                .andExpect(status().isOk())
                .andDo(document(
                        "grant-claim-maximum/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("id of the Competition for the " +
                                        "maximum funding level to be checked")
                        ))
                );

        verify(grantClaimMaximumService).isMaximumFundingLevelOverridden(competitionId);
    }
}