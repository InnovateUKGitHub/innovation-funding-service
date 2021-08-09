package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionThirdPartyConfigService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionThirdPartyConfigControllerTest extends BaseControllerMockMVCTest<CompetitionThirdPartyConfigController> {

    @Mock
    private CompetitionThirdPartyConfigService competitionThirdPartyConfigService;

    @Override
    protected CompetitionThirdPartyConfigController supplyControllerUnderTest() {
        return new CompetitionThirdPartyConfigController();
    }

    @Test
    public void findOneByCompetitionId() throws Exception {
        long competitionId = 100L;

        CompetitionThirdPartyConfigResource resource = newCompetitionThirdPartyConfigResource().build();

        when(competitionThirdPartyConfigService.findOneByCompetitionId(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competition-third-party-config/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(resource)));

        verify(competitionThirdPartyConfigService).findOneByCompetitionId(competitionId);
    }
}
