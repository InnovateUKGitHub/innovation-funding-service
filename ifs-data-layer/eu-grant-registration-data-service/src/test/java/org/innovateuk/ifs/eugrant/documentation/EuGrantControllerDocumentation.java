package org.innovateuk.ifs.eugrant.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.controller.EuGrantController;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.UUID;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuGrantControllerDocumentation extends BaseControllerMockMVCTest<EuGrantController> {

    @Mock
    private EuGrantService euGrantService;

    @Override
    public EuGrantController supplyControllerUnderTest() {
        return new EuGrantController();
    }

    @Test
    public void save() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource()
                .build();

        when(euGrantService.save(euGrantResource)).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                post("/eu-grant")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(euGrantResource))
                )
                .andExpect(status().isCreated())
                .andDo(document(
                        "eu-grant/{method-name}",
                        requestFields(fields()),
                        responseFields(fields())
                        )
                );
    }

    @Test
    public void findById() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource()
                .build();
        UUID uuid = UUID.randomUUID();

        when(euGrantService.findById(uuid)).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                get("/eu-grant/{uuid}", uuid.toString()))
                .andExpect(status().isOk())
                .andDo(document(
                        "eu-grant/{method-name}",
                        pathParameters(
                                parameterWithName("uuid").description("Id the grant registration")
                        ),
                        responseFields(fields())
                    )
                );
    }


    private FieldDescriptor[] fields() {
        return new FieldDescriptor[] {
                fieldWithPath("id").description("Unique id for the eu grant.")
        };
    }
}
