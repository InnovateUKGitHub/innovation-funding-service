package org.innovateuk.ifs.eugrant.overview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.overview.controller.EuGrantController;
import org.innovateuk.ifs.eugrant.overview.populator.EuGrantOverviewViewModelPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EuGrantControllerTest extends BaseControllerMockMVCTest<EuGrantController> {

    @Spy
    @InjectMocks
    private EuGrantOverviewViewModelPopulator euGrantOverviewViewModelPopulator;

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Override
    protected EuGrantController supplyControllerUnderTest() {
        return new EuGrantController();
    }

    @Test
    public void viewOverview() throws Exception {

        EuGrantResource euGrantResource = newEuGrantResource().build();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        mockMvc.perform(get("/overview"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("eugrant/overview"));
    }
}
