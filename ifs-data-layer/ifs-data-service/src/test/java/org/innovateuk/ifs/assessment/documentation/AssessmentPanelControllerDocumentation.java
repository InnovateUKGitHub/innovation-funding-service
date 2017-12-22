package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessmentPanelController;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessmentReviewDocs.assessmentReviewResourceBuilder;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelControllerDocumentation extends BaseControllerMockMVCTest<AssessmentPanelController>{

    private static final long applicationId = 1L;
    private static final long competitionId = 2L;

    @Override
    public AssessmentPanelController supplyControllerUnderTest() {
        return new AssessmentPanelController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(assessmentPanelServiceMock.assignApplicationToPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/assignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to assign to assessment panel")
                        )));

        verify(assessmentPanelServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unassignApplication() throws Exception {
        when(assessmentPanelServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to unassign from assessment panel")
                        )));

        verify(assessmentPanelServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }

    @Test
    public void getAssessmentReviews() throws Exception {
        List<AssessmentReviewResource> assessmentResources = assessmentReviewResourceBuilder.build(2);

        when(assessmentPanelServiceMock.getAssessmentReviews(competitionId)).thenReturn(serviceSuccess(assessmentResources));
        mockMvc.perform(post("/assessmentpanel/get-assessment-review/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to receive assessment reviews for")
                        )));

        verify(assessmentPanelServiceMock, only()).getAssessmentReviews(competitionId);
    }
}
