package com.worth.ifs.project.finance.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.ProjectFinanceController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void generateSpendProfile() throws Exception {

        when(projectFinanceServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/spend-profile/generate", 123L)).
                andExpect(status().isCreated()).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the " +
                                        "Spend Profile information is being generated")
                        )
                ));
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }
}
