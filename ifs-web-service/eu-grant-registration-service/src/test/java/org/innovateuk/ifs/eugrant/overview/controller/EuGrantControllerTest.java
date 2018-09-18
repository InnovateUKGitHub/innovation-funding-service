package org.innovateuk.ifs.eugrant.overview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.eugrant.overview.populator.EuGrantOverviewViewModelPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Optional;
import java.util.UUID;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EuGrantControllerTest extends BaseControllerMockMVCTest<EuGrantController> {

    @Spy
    @InjectMocks
    private EuGrantOverviewViewModelPopulator euGrantOverviewViewModelPopulator;

    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Mock
    private EuGrantRestService euGrantRestService;

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

    @Test
    public void submit() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource()
                .withId(UUID.randomUUID())
                .build();

        EuGrantResource submitted = newEuGrantResource()
                .withId(euGrantResource.getId())
                .withShortCode("1234")
                .build();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);
        when(euGrantRestService.submit(euGrantResource.getId())).thenReturn(restSuccess(submitted));

        mockMvc.perform(post("/overview")
                .param("agreeTerms", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/submitted"));

        verify(euGrantCookieService).clear();
        verify(euGrantCookieService).setPreviouslySubmitted(submitted);
        verify(euGrantRestService).submit(euGrantResource.getId());
    }

    @Test
    public void submit_withMissingTerms() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource().build();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);
        mockMvc.perform(post("/overview"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("eugrant/overview"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void submitted() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource().build();
        when(euGrantCookieService.getPreviouslySubmitted()).thenReturn(Optional.of(euGrantResource));

        mockMvc.perform(get("/submitted"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("eugrant/submitted"))
                .andExpect(model().attribute("model", euGrantResource));
    }
}
