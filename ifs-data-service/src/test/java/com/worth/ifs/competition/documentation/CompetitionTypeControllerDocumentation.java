package com.worth.ifs.competition.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.controller.CompetitionTypeController;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.transactional.CompetitionSetupService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionTypeControllerDocumentation extends BaseControllerMockMVCTest<CompetitionTypeController> {
    @Mock
    CompetitionSetupService competitionSetupService;
    private RestDocumentationResultHandler document;

    @Override
    protected CompetitionTypeController supplyControllerUnderTest() {
        return new CompetitionTypeController();
    }

    @Before
    public void setup() {
        this.document = document("competition-type/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findAll() throws Exception {
        when(competitionSetupService.findAllTypes()).thenReturn(ServiceResult.serviceSuccess(asList(new CompetitionTypeResource())));

        mockMvc.perform(get("/competition-type/findAll"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                    responseFields(
                            fieldWithPath("[].id").description("id of the competition type"),
                            fieldWithPath("[].name").description("name of the competition type"),
                            fieldWithPath("[].competitions").description("competition ids that have this type"),
                            fieldWithPath("[].stateAid").description("the competition id"),
                            fieldWithPath("[].active").description("indicates if the competition type is active")
                    )
                ));
    }
}