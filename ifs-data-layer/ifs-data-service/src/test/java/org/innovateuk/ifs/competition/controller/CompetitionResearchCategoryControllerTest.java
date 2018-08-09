package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.transactional.CompetitionResearchCategoryService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkResourceBuilder.newCompetitionResearchCategoryLinkResource;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionResearchCategoryControllerTest extends BaseControllerMockMVCTest<CompetitionResearchCategoryController> {

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
                .thenReturn(serviceSuccess(newCompetitionResearchCategoryLinkResource().build(3)));

        mockMvc.perform(get("/competition-research-category/{id}", competitionId))
                .andExpect(status().isOk());

        verify(competitionResearchCategoryService, only()).findByCompetition(competitionId);
    }
}
