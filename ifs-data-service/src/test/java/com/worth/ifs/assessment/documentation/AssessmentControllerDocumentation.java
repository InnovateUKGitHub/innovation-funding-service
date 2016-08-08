package com.worth.ifs.assessment.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.controller.AssessmentController;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Before;
import org.junit.Test;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

/**
 * Module: innovation-funding-service-dev
 * IBCTEC LTD
 * AUthor: ikenna1
 * DAte: 20/07/2016.
 **/
public class AssessmentControllerDocumentation extends BaseControllerMockMVCTest<AssessmentController> {

    private RestDocumentationResultHandler document;
    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Before
    public void setup(){
        this.document = document("assessment/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findAssessmentById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();
        when(assessmentServiceMock.findById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}", assessmentId))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment that is being requested")
                        ),
                        responseFields(assessmentFields)
                ));
    }

    @Test
    public void updateAssessmentStatus() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Long assessmentId  = 1L;

        ProcessOutcome processOutcome = newProcessOutcome()
                .withDescription("Conflict of interest")
                .withComment("own company")
                .withOutcome("YES")
                .withOutcomeType(AssessmentOutcomes.REJECT.getType())
                .build();
        when(assessmentServiceMock.updateStatus(assessmentId,processOutcome)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/status", assessmentId, processOutcome)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(processOutcome)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the assessment for which to update the assessment status")
                        )
                ));
    }

}
