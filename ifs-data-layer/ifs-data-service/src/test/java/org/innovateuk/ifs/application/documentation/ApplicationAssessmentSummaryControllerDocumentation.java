package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationAssessmentSummaryController;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationAssessmentSummaryResourceDocs.applicationAssessmentSummaryFields;
import static org.innovateuk.ifs.documentation.ApplicationAssessmentSummaryResourceDocs.applicationAssessmentSummaryResourceBuilder;
import static org.innovateuk.ifs.documentation.ApplicationAssessorResourceDocs.applicationAssessorFields;
import static org.innovateuk.ifs.documentation.ApplicationAssessorResourceDocs.applicationAssessorResourceBuilder;
import static org.innovateuk.ifs.documentation.PageResourceDocs.pageResourceFields;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationAssessmentSummaryControllerDocumentation extends BaseControllerMockMVCTest<ApplicationAssessmentSummaryController> {

    @Mock
    private ApplicationAssessmentSummaryService applicationAssessmentSummaryServiceMock;

    @Override
    protected ApplicationAssessmentSummaryController supplyControllerUnderTest() {
        return new ApplicationAssessmentSummaryController();
    }

    @Test
    public void getAssessors() throws Exception {
        Long applicationId = 1L;
        List<ApplicationAssessorResource> applicationAssessorResources = applicationAssessorResourceBuilder.build(2);

        when(applicationAssessmentSummaryServiceMock.getAssignedAssessors(applicationId)).thenReturn(serviceSuccess(applicationAssessorResources));

        mockMvc.perform(get("/application-assessment-summary/{id}/assigned-assessors", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
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

        mockMvc.perform(get("/application-assessment-summary/{id}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("applicationassessmentsummary/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application")
                        ),
                        responseFields(applicationAssessmentSummaryFields)
                ));

        verify(applicationAssessmentSummaryServiceMock, only()).getApplicationAssessmentSummary(applicationId);
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long applicationId = 1L;
        int pageIndex = 1;
        int pageSize = 1;
        String assessorNameFilter = "assessorNameFilter";
        Sort sort = Sort.ASSESSOR;
        ApplicationAvailableAssessorPageResource page = new ApplicationAvailableAssessorPageResource(1L, 1, Collections.emptyList(),1 ,1);

        when(applicationAssessmentSummaryServiceMock.getAvailableAssessors(applicationId, pageIndex, pageSize, assessorNameFilter, sort)).thenReturn(serviceSuccess(page));

        mockMvc.perform(get("/application-assessment-summary/{applicationId}/available-assessors?page={page}&size={size}&assessorNameFilter={assessorNameFilter}&sort={sort}", applicationId, pageIndex, pageSize, assessorNameFilter, sort)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("applicationassessmentsummary/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application")
                        ),
                        requestParameters(
                                parameterWithName("page").description("Index of the page to get"),
                                parameterWithName("size").description("Size of the page"),
                                parameterWithName("assessorNameFilter").description("Assessor name filter"),
                                parameterWithName("sort").description("Sort order")
                        ),
                        responseFields(
                                pageResourceFields
                        )
                ));

        verify(applicationAssessmentSummaryServiceMock, only()).getAvailableAssessors(applicationId, pageIndex, pageSize, assessorNameFilter, sort);
    }

    @Test
    public void getAvailableAssessorsIds() throws Exception {
        long applicationId = 1L;
        String assessorNameFilter = "assessorNameFilter";

        when(applicationAssessmentSummaryServiceMock.getAvailableAssessorIds(applicationId, assessorNameFilter)).thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/application-assessment-summary/{applicationId}/available-assessors-ids?assessorNameFilter={assessorNameFilter}", applicationId, assessorNameFilter)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("applicationassessmentsummary/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application")
                        ),
                        requestParameters(
                                parameterWithName("assessorNameFilter").description("Assessor name filter")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("ids")
                        )
                ));

        verify(applicationAssessmentSummaryServiceMock, only()).getAvailableAssessorIds(applicationId, assessorNameFilter);
    }
}