package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.controller.AssessorCountOptionsController;
import org.innovateuk.ifs.competition.fixtures.AssessorCountOptionFixture;
import org.innovateuk.ifs.competition.transactional.AssessorCountOptionService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.documentation.AssessorCountOptionResourceDocs.assessorCountOptionResourceFields;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorCountOptionsControllerDocumentation extends BaseControllerMockMVCTest<AssessorCountOptionsController> {

    @Mock
    private AssessorCountOptionService assessorCountOptionService;

    @Override
    protected AssessorCountOptionsController supplyControllerUnderTest() {
        return new AssessorCountOptionsController();
    }

    @Test
    public void getAllByCompetitionType() throws Exception {
        when(assessorCountOptionService.findAllByCompetitionType(anyLong())).thenReturn(ServiceResult.serviceSuccess(AssessorCountOptionFixture.programmeAssessorOptionResourcesList()));

        mockMvc.perform(get("/assessor-count-options/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("assessor-count-options/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition type")
                        ),
                        responseFields(assessorCountOptionResourceFields)
                ));
    }
}
