package com.worth.ifs.assessment.documentation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.controller.AssessmentController;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.AssessmentDocs.assessmentFields;
import static com.worth.ifs.documentation.AssessmentDocs.assessmentResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class AssessmentControllerDocumentation extends BaseControllerMockMVCTest<AssessmentController> {
    private static final String baseURI = "/assessment";

    private RestDocumentationResultHandler document;

    @Mock
    private AssessorService assessorService;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Before
    public void setup() {
        this.document = document("assessment/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findByCompetition() throws Exception {
        final Long assessorId = 1L;
        final Long competitionId = 2L;

        final List<AssessmentResource> returnObj = assessmentResourceBuilder.build(2);

        when(assessorService.getAllByCompetitionAndAssessor(competitionId, assessorId)).thenReturn(serviceSuccess(returnObj));

        mockMvc.perform(get(baseURI + "/findAssessmentsByCompetition/{assessorId}/{competitionId}", assessorId, competitionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("assessorId").description("id of the assessor for which to find the assessments"),
                                parameterWithName("competitionId").description("id of the competition for which to find the assessments")
                        ),
                        responseFields(fieldWithPath("[]").description("list of assessments beloning to an assessor and a competition"))
                ));
    }

    @Test
    public void findByProccessRole() throws Exception {
        final Long processRoleId = 1L;

        final AssessmentResource resultObj = assessmentResourceBuilder.build();

        when(assessorService.getOneByProcessRole(processRoleId)).thenReturn(serviceSuccess(resultObj));

        mockMvc.perform(get(baseURI + "/findAssessmentByProcessRole/{processRoleId}", processRoleId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("processRoleId").description("id of the processrole for which an assessment should be found")
                        ),
                        responseFields(assessmentFields)
                ));
    }

    @Test
    public void getTotalAssigned() throws Exception {
        final Long competitionId = 2L;
        final Long userId = 1L;

        when(assessorService.getTotalAssignedAssessmentsByCompetition(competitionId, userId)).thenReturn(serviceSuccess(3));

        mockMvc.perform(get(baseURI + "/totalAssignedAssessmentsByCompetition/{userId}/{competitionId}", userId, competitionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("userId").description("id of the user for which to find the amount of assigned assessments"),
                                parameterWithName("competitionId").description("id of the competition for which to find the amount of assigned assessments")
                        )
                ));
    }

    @Test
    public void getTotalSubmitted() throws Exception {
        final Long competitionId = 2L;
        final Long userId = 1L;

        when(assessorService.getTotalSubmittedAssessmentsByCompetition(competitionId, userId)).thenReturn(serviceSuccess(3));

        mockMvc.perform(get(baseURI + "/totalSubmittedAssessmentsByCompetition/{userId}/{competitionId}", userId, competitionId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("userId").description("id of the user for which to find the amount of assigned assessments"),
                                parameterWithName("competitionId").description("id of the competition for which to find the amount of assigned assessments")
                        )
                ));
    }

    @Test
    public void acceptAssessment() throws Exception {
        final Long processRoleId = 1L;

        final AssessmentResource assessment = assessmentResourceBuilder.build();
        final ObjectMapper mapper = new ObjectMapper();

        when(assessorService.acceptAssessmentInvitation(processRoleId, assessment)).thenReturn(serviceSuccess(null));
        mockMvc.perform(put(baseURI + "/acceptAssessmentInvitation/{processRoleId}", processRoleId)
                    .content(mapper.writeValueAsString(assessment))
                    .contentType(APPLICATION_JSON)
                )
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("processRoleId").description("Id of the processrole for which to accept the invitation")
                        ),
                        requestFields(assessmentFields)
                ));
    }

    @Test
    @Ignore
    //TODO make this test not throw a null pointer
    public void rejectAssessment() throws Exception {
        final Long processRoleId = 1L;
        final ProcessOutcome outcome = newProcessOutcome().build();
        final ObjectMapper mapper = new ObjectMapper();

        when(assessorService.rejectAssessmentInvitation(processRoleId, outcome)).thenReturn(serviceSuccess(null));

        mockMvc.perform(get(baseURI + "/rejectAssessmentInvitation/{processRoleId}", processRoleId)
                        .content(mapper.writeValueAsString(outcome))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("processRoleId").description("Id of the processrole for which to accept the invitation")
                        ),
                        requestFields(assessmentFields)
                ));
    }



}