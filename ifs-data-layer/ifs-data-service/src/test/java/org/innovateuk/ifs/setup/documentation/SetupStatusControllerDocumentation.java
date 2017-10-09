package org.innovateuk.ifs.setup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.SetupStatusResourceDocs;
import org.innovateuk.ifs.setup.controller.SetupStatusController;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.SetupStatusResourceDocs.setupStatusResourceBuilder;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class SetupStatusControllerDocumentation extends BaseControllerMockMVCTest<SetupStatusController> {
    private RestDocumentationResultHandler document;

    @Mock
    private SetupStatusService setupStatusService;

    @Override
    protected SetupStatusController supplyControllerUnderTest() {
        return new SetupStatusController();
    }

    @Before
    public void setup(){
        this.document = document("setupStatus/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findByTarget() throws Exception {
        final String targetClassName = "org.domain.Competition";
        final Long targetId = 925L;

        when(setupStatusService.findByTargetClassNameAndTargetId(targetClassName, targetId))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build(1)));

        mockMvc.perform(get("/questionSetupStatus/findByTarget/{targetClassName}/{targetId}", targetClassName, targetId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("targetClassName").description("Classname of the target object"),
                                parameterWithName("targetId").description("Id of the target object")
                        ),
                        responseFields(
                                SetupStatusResourceDocs.setupStatusListFields
                        )
                ));
    }


    @Test
    public void findByTargetAndParent() throws Exception {
        final String targetClassName = "org.domain.Competition";
        final Long targetId = 925L;
        final Long parentId = 2414L;

        when(setupStatusService.findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build(1)));

        mockMvc.perform(get("/questionSetupStatus/findByTargetAndParent/{targetClassName}/{targetId}/{parentId}",
                targetClassName, targetId, parentId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("targetClassName").description("Classname of the target object"),
                                parameterWithName("targetId").description("Id of the target object"),
                                parameterWithName("parentId").description("Id of the parent setup status")
                        ),
                        responseFields(
                                SetupStatusResourceDocs.setupStatusListFields
                        )
                ));
    }

    @Test
    public void findByClassAndParent() throws Exception {
        String className = "org.domain.Question";
        Long parentId = 925L;

        when(setupStatusService.findByClassNameAndParentId(className, parentId))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build(1)));

        mockMvc.perform(get("/questionSetupStatus/findByClassAndParent/{className}/{parentId}", className, parentId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("className").description("Classname of the object"),
                                parameterWithName("parentId").description("Id of the parent setup status")
                        ),
                        responseFields(
                                SetupStatusResourceDocs.setupStatusListFields
                        )
                ));
    }

    @Test
    public void findSetupStatus() throws Exception {
        String className = "org.domain.Competition";
        Long classPk = 925L;

        when(setupStatusService.findSetupStatus(className, classPk))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build()));

        mockMvc.perform(get("/questionSetupStatus/findSetupStatus/{className}/{classPk}",className, classPk))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("className").description("Classname of the target object"),
                                parameterWithName("classPk").description("Id of the target object")
                        ),
                        responseFields(
                                SetupStatusResourceDocs.setupStatusResourceFields
                        )
                ));
    }

    @Test
    public void saveSetupStatus() throws  Exception {
        SetupStatusResource savedResult = setupStatusResourceBuilder.build();

        when(setupStatusService.saveSetupStatus(any(SetupStatusResource.class))).thenReturn(serviceSuccess(savedResult));

        mockMvc.perform(post("/questionSetupStatus/save")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(setupStatusResourceBuilder.build())))
                .andDo(this.document.snippets(
                    responseFields(
                            SetupStatusResourceDocs.setupStatusResourceFields
                    )
                ));
    }

}
