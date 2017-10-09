package org.innovateuk.ifs.setup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.innovateuk.ifs.setup.transactional.SetupStatusService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.SetupStatusResourceDocs.setupStatusResourceBuilder;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SetupStatusControllerTest extends BaseControllerMockMVCTest<SetupStatusController> {

    @Override
    protected SetupStatusController supplyControllerUnderTest() {
        return new SetupStatusController();
    }

    @Mock
    private SetupStatusService setupStatusService;

    @Test
    public void testFindByTarget() throws Exception {
        final String targetClassName = "org.domain.Competition";
        final Long targetId = 925L;

        when(setupStatusService.findByTargetClassNameAndTargetId(targetClassName, targetId))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build(1)));

        mockMvc.perform(get("/questionSetupStatus/findByTarget/{targetClassName}/{targetId}", targetClassName, targetId))
                .andExpect(status().is2xxSuccessful());

        verify(setupStatusService, times(1)).findByTargetClassNameAndTargetId(targetClassName, targetId);
    }


    @Test
    public void testFindByTargetAndParent() throws Exception {
        final String targetClassName = "org.domain.Competition";
        final Long targetId = 925L;
        final Long parentId = 2414L;

        when(setupStatusService.findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build(1)));

        mockMvc.perform(get("/questionSetupStatus/findByTargetAndParent/{targetClassName}/{targetId}/{parentId}",
                targetClassName, targetId, parentId))
                .andExpect(status().is2xxSuccessful());

        verify(setupStatusService, times(1)).findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId);
    }

    @Test
    public void testFindByClassAndParent() throws Exception {
        String className = "org.domain.Question";
        Long parentId = 925L;

        when(setupStatusService.findByClassNameAndParentId(className, parentId))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build(1)));

        mockMvc.perform(get("/questionSetupStatus/findByClassAndParent/{className}/{parentId}", className, parentId))
                .andExpect(status().is2xxSuccessful());

        verify(setupStatusService, times(1)).findByClassNameAndParentId(className, parentId);
    }

    @Test
    public void testFindSetupStatus() throws Exception {
        String className = "org.domain.Competition";
        Long classPk = 925L;

        when(setupStatusService.findSetupStatus(className, classPk))
                .thenReturn(serviceSuccess(setupStatusResourceBuilder.build()));

        mockMvc.perform(get("/questionSetupStatus/findSetupStatus/{className}/{classPk}", className, classPk))
                .andExpect(status().is2xxSuccessful());

        verify(setupStatusService, times(1)).findSetupStatus(className, classPk);
    }

    @Test
    public void saveSetupStatus() throws Exception {
        SetupStatusResource savedResult = setupStatusResourceBuilder.build();

        when(setupStatusService.saveSetupStatus(any(SetupStatusResource.class))).thenReturn(serviceSuccess(savedResult));

        mockMvc.perform(post("/questionSetupStatus/save")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(setupStatusResourceBuilder.build())))
                .andExpect(status().is2xxSuccessful());

        verify(setupStatusService, times(1)).saveSetupStatus(any(SetupStatusResource.class));
    }
}
