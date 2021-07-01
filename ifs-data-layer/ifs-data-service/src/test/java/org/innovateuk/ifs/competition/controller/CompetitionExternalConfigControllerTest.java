package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionExternalConfigService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionExternalConfigResourceBuilder.newCompetitionExternalConfigResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionExternalConfigControllerTest extends BaseControllerMockMVCTest<CompetitionExternalConfigController> {

    @Mock
    private CompetitionExternalConfigService competitionExternalConfigService;


    @Override
    protected CompetitionExternalConfigController supplyControllerUnderTest() {
        return new CompetitionExternalConfigController();
    }

    @Test
    public void findOneByCompetitionId() throws Exception {
        long competitionId = 100L;

        CompetitionExternalConfigResource resource = newCompetitionExternalConfigResource().build();

        when(competitionExternalConfigService.findOneByCompetitionId(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competition-external-config/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(resource)));

        verify(competitionExternalConfigService).findOneByCompetitionId(competitionId);
    }
}
