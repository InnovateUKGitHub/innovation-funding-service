package org.innovateuk.ifs.cofunder.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.transactional.CofunderDashboardService;
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

public class CofunderDashboardControllerTest extends BaseControllerMockMVCTest<CofunderDashboardController> {
    @Mock
    private CofunderDashboardService cofunderDashboardService;

    @Override
    protected CofunderDashboardController supplyControllerUnderTest() {
        return new CofunderDashboardController();
    }

    @Test
    public void getApplicationsForCofunding() throws Exception {
        long userId = 1L;
        long competitionId = 2L;
        int page = 3;
        int size = 10;
        CofunderDashboardApplicationPageResource expected = new CofunderDashboardApplicationPageResource();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "activityState");

        when(cofunderDashboardService.getApplicationsForCofunding(userId, competitionId, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/cofunder/dashboard/user/{userId}/competition/{competition}?page={page}&size={size}", userId, competitionId, page, size))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderDashboardService, only()).getApplicationsForCofunding(userId, competitionId, pageRequest);
    }
}
