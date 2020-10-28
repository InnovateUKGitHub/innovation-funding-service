package org.innovateuk.ifs.supporter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.transactional.SupporterDashboardService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SupporterDashboardControllerTest extends BaseControllerMockMVCTest<SupporterDashboardController> {
    @Mock
    private SupporterDashboardService supporterDashboardService;

    @Override
    protected SupporterDashboardController supplyControllerUnderTest() {
        return new SupporterDashboardController();
    }

    @Test
    public void getApplicationsForCofunding() throws Exception {
        long userId = 1L;
        long competitionId = 2L;
        int page = 3;
        int size = 10;
        SupporterDashboardApplicationPageResource expected = new SupporterDashboardApplicationPageResource();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "activityState");

        when(supporterDashboardService.getApplicationsForCofunding(userId, competitionId, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/supporter/dashboard/user/{userId}/competition/{competition}?page={page}&size={size}", userId, competitionId, page, size))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(supporterDashboardService, only()).getApplicationsForCofunding(userId, competitionId, pageRequest);
    }
}
