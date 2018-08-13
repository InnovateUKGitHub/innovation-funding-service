package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionResearchCategoryController;
import org.innovateuk.ifs.competition.transactional.CompetitionResearchCategoryService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkResourceBuilder.newCompetitionResearchCategoryLinkResource;
import static org.innovateuk.ifs.documentation.CompetitionResearchCategoryLinkDocs.competitionResearchCategoryLinkBuilder;
import static org.innovateuk.ifs.documentation.CompetitionResearchCategoryLinkDocs.competitionResearchCategoryLinkResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionResearchCategoryControllerDocumentation extends BaseControllerMockMVCTest<CompetitionResearchCategoryController> {

    @Mock
    private CompetitionResearchCategoryService competitionResearchCategoryService;

    @Override
    protected CompetitionResearchCategoryController supplyControllerUnderTest() {
        return new CompetitionResearchCategoryController(competitionResearchCategoryService);
    }

    @Test
    public void findByCompetition() throws Exception {
        final Long competitionId = 1L;

        when(competitionResearchCategoryService.findByCompetition(competitionId))
                .thenReturn(serviceSuccess(competitionResearchCategoryLinkBuilder.build(3)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/competition-research-category/{id}", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition-research-category/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition we want the chosen research categories from")
                        ),
                        responseFields(competitionResearchCategoryLinkResourceFields)
                ));


    }
}
