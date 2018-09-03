package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.controller.EuOrganisationTypeController;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationTypeForm;
import org.innovateuk.ifs.eugrant.organisation.service.EuOrganisationCookieService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EuOrganisationTypeControllerTest extends BaseControllerMockMVCTest<EuOrganisationTypeController> {

    @Mock
    protected EuOrganisationCookieService organisationCookieService;

    @Override
    protected EuOrganisationTypeController supplyControllerUnderTest() {
        return new EuOrganisationTypeController();
    }

    @Test
    public void selectOrganisationType() throws Exception {
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.empty());
        mockMvc.perform(get("/organisation/type"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/type"))
                .andExpect(model().attribute("organisationForm", new EuOrganisationTypeForm()));
    }

    @Test
    public void selectOrganisationType_existingCookie() throws Exception {
        EuOrganisationTypeForm form = new EuOrganisationTypeForm();
        form.setOrganisationType(EuOrganisationType.BUSINESS);
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.of(form));
        mockMvc.perform(get("/organisation/type"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/type"))
                .andExpect(model().attribute("organisationForm", form));
    }

    @Test
    public void confirmSelectOrganisationType() throws Exception {
        mockMvc.perform(post("/organisation/type")
                .param("organisationType", EuOrganisationType.BUSINESS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/find"));

        verify(organisationCookieService).saveToOrganisationTypeCookie(new EuOrganisationTypeForm(EuOrganisationType.BUSINESS));
    }

    @Test
    public void confirmSelectOrganisationType_noTypeSelected() throws Exception {
        mockMvc.perform(post("/organisation/type"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/type"))
                .andExpect(model().attributeHasFieldErrorCode("organisationForm", "organisationType", "NotNull"));
        verifyZeroInteractions(organisationCookieService);
    }

}
