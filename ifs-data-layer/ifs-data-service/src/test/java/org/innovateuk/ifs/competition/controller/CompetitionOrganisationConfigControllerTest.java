package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionOrganisationConfigService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionOrganisationConfigControllerTest extends BaseControllerMockMVCTest<CompetitionOrganisationConfigController> {

    @Mock
    private CompetitionOrganisationConfigService competitionOrganisationConfigService;

    @Override
    protected CompetitionOrganisationConfigController supplyControllerUnderTest() {
        return new CompetitionOrganisationConfigController();
    }

    @Test
    public void findOneByCompetitionId() throws Exception {
        long competitionId = 100L;

        CompetitionOrganisationConfigResource resource = new CompetitionOrganisationConfigResource();

        when(competitionOrganisationConfigService.findOneByCompetitionId(competitionId)).thenReturn(serviceSuccess(Optional.of(resource)));

        mockMvc.perform(get("/competition-organisation-config/find-by-competition-id/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(resource)));

        verify(competitionOrganisationConfigService).findOneByCompetitionId(competitionId);
    }
}