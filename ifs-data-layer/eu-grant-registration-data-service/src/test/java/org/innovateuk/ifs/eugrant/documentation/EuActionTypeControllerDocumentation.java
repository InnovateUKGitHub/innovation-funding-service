package org.innovateuk.ifs.eugrant.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.*;
import org.innovateuk.ifs.eugrant.transactional.EuActionTypeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuActionTypeResourceBuilder.newEuActionTypeResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuActionTypeControllerDocumentation extends BaseControllerMockMVCTest<EuActionTypeController> {

    @Mock
    private EuActionTypeService euActionTypeService;

    @Override
    public EuActionTypeController supplyControllerUnderTest() {
        return new EuActionTypeController();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void findAll() throws Exception {

        List<EuActionTypeResource> actionTypeResources = newEuActionTypeResource()
                .withId(1L, 2L)
                .withName("action type")
                .withDescription("description")
                .withPriority(1, 2)
                .build(2);

        when(euActionTypeService.findAll()).thenReturn(serviceSuccess(actionTypeResources));

        mockMvc.perform(get("/eu-grant/action-type/find-all"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(actionTypeResources)))
                .andDo(document("eu-grant/{method-name}",
                        responseFields(fieldWithPath("[]").description("List of funding scheme actions"))
                        .andWithPrefix("[].", fields())
                ));
    }

    @Test
    public void getById() throws Exception {

        EuActionTypeResource actionTypeResource = newEuActionTypeResource()
                .withId(1L)
                .withName("action type")
                .withDescription("description")
                .withPriority(1)
                .build();

        when(euActionTypeService.getById(actionTypeResource.getId())).thenReturn(serviceSuccess(actionTypeResource));

        mockMvc.perform(
                get("/eu-grant/action-type/get-by-id/{id}", actionTypeResource.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(actionTypeResource)))
                .andDo(document(
                        "eu-grant/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id the action type.")
                        ),
                        responseFields(fields())
                        )
                );
    }

    private FieldDescriptor[] fields() {
        return new FieldDescriptor[] {
                fieldWithPath("id").description("Unique id for the action type."),
                fieldWithPath("name").description("name of the action type."),
                fieldWithPath("description").description("description of the action type."),
                fieldWithPath("priority").description("priority of which to show the action types in a list."),
        };
    }
}
