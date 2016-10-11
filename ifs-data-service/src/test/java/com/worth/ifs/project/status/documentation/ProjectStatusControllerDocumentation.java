package com.worth.ifs.project.status.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.status.controller.ProjectStatusController;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.ProjectStatusDocs.competitionProjectsStatusResourceFields;
import static com.worth.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class ProjectStatusControllerDocumentation extends BaseControllerMockMVCTest<ProjectStatusController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup(){
        this.document = document("project/{method-name}", preprocessResponse(prettyPrint()));
    }

    @Test
    public void getCompetitionStatus() throws Exception {
        Long competitionId = 1L;
        CompetitionProjectsStatusResource competitionProjectsStatusResource = newCompetitionProjectsStatusResource().build();

        when(projectStatusServiceMock.getCompetitionStatus(competitionId)).thenReturn(serviceSuccess(competitionProjectsStatusResource));

        mockMvc.perform(get("/project/competition/{id}", competitionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the competition for which project status details are being requested")
                        ),
                        responseFields(competitionProjectsStatusResourceFields)
                ));
    }

    @Override
    protected ProjectStatusController supplyControllerUnderTest() {
        return new ProjectStatusController();
    }
}
