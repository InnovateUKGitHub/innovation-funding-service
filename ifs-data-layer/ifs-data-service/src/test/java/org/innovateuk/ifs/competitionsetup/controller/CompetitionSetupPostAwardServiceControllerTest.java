package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupPostAwardServiceService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupPostAwardServiceControllerTest extends BaseControllerMockMVCTest<CompetitionSetupPostAwardServiceController> {

    @Mock
    private CompetitionSetupPostAwardServiceService competitionSetupPostAwardServiceService;

    @Override
    protected CompetitionSetupPostAwardServiceController supplyControllerUnderTest() {
        return new CompetitionSetupPostAwardServiceController();
    }

    @Test
    public void configurePostAwardService() throws Exception {
        final long competitionId = 1L;
        final PostAwardService postAwardService = PostAwardService.CONNECT;

        when(competitionSetupPostAwardServiceService.configurePostAwardService(competitionId, postAwardService)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{id}/post-award-service/{innovationLeadUserId}", competitionId, postAwardService.name()))
                .andExpect(status().isOk());

        verify(competitionSetupPostAwardServiceService, only()).configurePostAwardService(competitionId, postAwardService);
    }
}
