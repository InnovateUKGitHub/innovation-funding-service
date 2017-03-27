package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationAssessmentSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationAssessmentSummaryResourceDocs.applicationAssessmentSummaryFields;
import static org.innovateuk.ifs.documentation.ApplicationAssessmentSummaryResourceDocs.applicationAssessmentSummaryResourceBuilder;
import static org.innovateuk.ifs.documentation.ApplicationAssessorResourceDocs.applicationAssessorFields;
import static org.innovateuk.ifs.documentation.ApplicationAssessorResourceDocs.applicationAssessorResourceBuilder;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationAssessmentSummaryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationAssessmentSummaryController> {

    @Override
    protected ApplicationAssessmentSummaryController supplyControllerUnderTest() {
        return new ApplicationAssessmentSummaryController();
    }

    @Test
    public void getAssessors() throws Exception {
        Long applicationId = 1L;
        List<ApplicationAssessorResource> applicationAssessorResources = applicationAssessorResourceBuilder.build(2);

        when(applicationAssessmentSummaryServiceMock.getAssignedAssessors(applicationId)).thenReturn(serviceSuccess(applicationAssessorResources));

        mockMvc.perform(get("/applicationAssessmentSummary/{id}/assignedAssessors", applicationId))
                .andExpect(status().isOk())
                .andDo(document("applicationassessmentsummary/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application")
                        ),
                        responseFields(fieldWithPath("[]").description("List of assessors participating on the competition of the application"))
                                .andWithPrefix("[].", applicationAssessorFields)
                ));

        verify(applicationAssessmentSummaryServiceMock, only()).getAssignedAssessors(applicationId);
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Long applicationId = 1L;
        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = applicationAssessmentSummaryResourceBuilder.build();

        when(applicationAssessmentSummaryServiceMock.getApplicationAssessmentSummary(applicationId)).thenReturn(serviceSuccess(applicationAssessmentSummaryResource));

        mockMvc.perform(get("/applicationAssessmentSummary/{id}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("applicationassessmentsummary/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application")
                        ),
                        responseFields(applicationAssessmentSummaryFields)
                ));

        verify(applicationAssessmentSummaryServiceMock, only()).getApplicationAssessmentSummary(applicationId);
    }
}