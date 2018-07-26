package org.innovateuk.ifs.finance.documentation;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.controller.GrantClaimMaximumController;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionResourceDocs.competitionResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionResourceDocs.competitionResourceFields;
import static org.innovateuk.ifs.documentation.GrantClaimMaximumDocs.grantClaimMaximumResourceFields;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
        return new GrantClaimMaximumController();
    }

    @Test
    public void getGrantClaimMaximumById() throws Exception {
        final Long grantClaimMaximumId = 1L;

        when(grantClaimMaximumService.getGrantClaimMaximumById(grantClaimMaximumId)).thenReturn(serviceSuccess(newGrantClaimMaximumResource().build()));

        mockMvc.perform(get("/grantClaimMaximum/{id}", grantClaimMaximumId))
                .andExpect(status().isOk())
                .andDo(document(
                        "grantClaimMaximum/{method-name}",
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

        mockMvc.perform(post("/grantClaimMaximum/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson((gcm))))
                .andExpect(status().isCreated())
                .andDo(document(
                        "grantClaimMaximum/{method-name}",
                        responseFields(grantClaimMaximumResourceFields)
                ));
    }
}
