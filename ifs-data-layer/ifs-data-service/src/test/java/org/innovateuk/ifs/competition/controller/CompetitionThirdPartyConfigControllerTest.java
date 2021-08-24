package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.transactional.CompetitionThirdPartyConfigService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionThirdPartyConfigControllerTest extends BaseControllerMockMVCTest<CompetitionThirdPartyConfigController> {

    @Mock
    private CompetitionThirdPartyConfigService competitionThirdPartyConfigServiceMock;

    @Override
    protected CompetitionThirdPartyConfigController supplyControllerUnderTest() {
        return new CompetitionThirdPartyConfigController();
    }

    @Test
    public void create() throws Exception {
        CompetitionThirdPartyConfigResource resource = newCompetitionThirdPartyConfigResource()
                .build();

        CompetitionThirdPartyConfigResource expectedResource = newCompetitionThirdPartyConfigResource()
                .withId(1L)
                .build();

        when(competitionThirdPartyConfigServiceMock.create(any(CompetitionThirdPartyConfigResource.class))).thenReturn(serviceSuccess(expectedResource));

        mockMvc.perform(post("/competition-third-party-config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(resource)))
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(expectedResource)));

        verify(competitionThirdPartyConfigServiceMock).create(any(CompetitionThirdPartyConfigResource.class));
    }

    @Test
    public void findOneByCompetitionId() throws Exception {
        long competitionId = 100L;

        CompetitionThirdPartyConfigResource resource = newCompetitionThirdPartyConfigResource().build();

        when(competitionThirdPartyConfigServiceMock.findOneByCompetitionId(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competition-third-party-config/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(resource)));

        verify(competitionThirdPartyConfigServiceMock).findOneByCompetitionId(competitionId);
    }

    @Test
    public void update() throws Exception {
        long competitionId = 100L;

        CompetitionThirdPartyConfigResource resource = newCompetitionThirdPartyConfigResource()
                .withId(1L)
                .build();

        when(competitionThirdPartyConfigServiceMock.update(eq(competitionId), any(CompetitionThirdPartyConfigResource.class)))
                .thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition-third-party-config/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(resource)))
                .andExpect(status().isOk());

        verify(competitionThirdPartyConfigServiceMock).update(eq(competitionId), any(CompetitionThirdPartyConfigResource.class));
    }
}
