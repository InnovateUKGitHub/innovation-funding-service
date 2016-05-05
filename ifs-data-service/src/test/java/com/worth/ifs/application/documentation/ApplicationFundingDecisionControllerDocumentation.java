package com.worth.ifs.application.documentation;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.ApplicationFundingDecisionController;
import com.worth.ifs.application.resource.FundingDecision;

public class ApplicationFundingDecisionControllerDocumentation extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    private RestDocumentationResultHandler document;

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Before
    public void setup(){
        this.document = document("applicationfunding/{method-name}",
                preprocessResponse(prettyPrint()));
    }
    
    @Test
    public void makeFundingDecision() throws Exception {
    	Long competitionId = 1L;
        Map<Long, FundingDecision> decision = ImmutableMap.of(1L, FundingDecision.FUNDED, 2L, FundingDecision.NOT_FUNDED);

        when(applicationFundingService.makeFundingDecision(competitionId, decision)).thenReturn(serviceSuccess(null));
        
        mockMvc.perform(post("/applicationfunding/1")
	        		.contentType(MediaType.APPLICATION_JSON)
	    			.content(new ObjectMapper().writeValueAsString(decision)))
                .andDo( this.document.snippets());
    }

}
