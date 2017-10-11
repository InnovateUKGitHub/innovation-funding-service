package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.controller.CompetitionTypeController;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionTypeControllerDocumentation extends BaseControllerMockMVCTest<CompetitionTypeController> {
    @Mock
    CompetitionSetupService competitionSetupService;

    @Override
    protected CompetitionTypeController supplyControllerUnderTest() {
        return new CompetitionTypeController();
    }

    @Test
    public void findAll() throws Exception {
        when(competitionSetupService.findAllTypes()).thenReturn(ServiceResult.serviceSuccess(asList(new CompetitionTypeResource())));

        mockMvc.perform(get("/competition-type/findAll"))
                .andExpect(status().isOk())
                .andDo(document("competition-type/{method-name}",
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
