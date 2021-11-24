package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionResearchCategoryController;
import org.innovateuk.ifs.competition.transactional.CompetitionResearchCategoryService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionResearchCategoryLinkDocs.competitionResearchCategoryLinkBuilder;
import static org.mockito.Mockito.when;
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

        mockMvc.perform(RestDocumentationRequestBuilders.get("/competition-research-category/{id}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());


    }
}
