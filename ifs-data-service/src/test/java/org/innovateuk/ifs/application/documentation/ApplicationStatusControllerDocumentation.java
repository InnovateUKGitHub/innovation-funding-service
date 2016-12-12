package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationStatusController;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.application.transactional.ApplicationStatusService;
import org.innovateuk.ifs.commons.service.ServiceResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.application.builder.ApplicationStatusResourceBuilder.newApplicationStatusResource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class ApplicationStatusControllerDocumentation extends BaseControllerMockMVCTest<ApplicationStatusController> {
    private RestDocumentationResultHandler document;

    @Override
    protected ApplicationStatusController supplyControllerUnderTest() {
        return new ApplicationStatusController();
    }

    @Mock
    ApplicationStatusService applicationStatusService;

    @Before
    public void setup(){
        this.document = document("application-status/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getApplicationStatusById() throws Exception {
        Long statusId = 1L;

        ApplicationStatusResource status = newApplicationStatusResource().withName("Open").build();

        when(applicationStatusService.getById(statusId)).thenReturn(ServiceResult.serviceSuccess(status));

        mockMvc.perform(get("/applicationstatus/{id}", statusId))
                .andDo(this.document.snippets(
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
