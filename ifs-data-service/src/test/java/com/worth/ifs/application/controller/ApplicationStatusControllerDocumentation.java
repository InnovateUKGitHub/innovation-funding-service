package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationStatusResource;
import com.worth.ifs.application.transactional.ApplicationStatusService;
import com.worth.ifs.commons.service.ServiceResult;

import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.application.builder.ApplicationStatusResourceBuilder.newApplicationStatusResource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class ApplicationStatusControllerDocumentation extends BaseControllerMockMVCTest<ApplicationStatusController> {
    @Override
    protected ApplicationStatusController supplyControllerUnderTest() {
        return new ApplicationStatusController();
    }

    @Mock
    ApplicationStatusService applicationStatusService;

    @Test
    public void documentGetApplicationStatusById() throws Exception {
        Long statusId = 1L;

        ApplicationStatusResource status = newApplicationStatusResource().withName("Open").build();

        when(applicationStatusService.getById(statusId)).thenReturn(ServiceResult.serviceSuccess(status));

        mockMvc.perform(get("/applicationstatus/{id}", statusId))
                .andDo(document("application-status/get-application-status",
                    pathParameters(
                            parameterWithName("id").description("applications status id that is being requested")
                    ),
                    responseFields(
                            fieldWithPath("id").description("Id of the application status"),
                            fieldWithPath("name").description("name of the application status")
                    )
                ));
    }

}
