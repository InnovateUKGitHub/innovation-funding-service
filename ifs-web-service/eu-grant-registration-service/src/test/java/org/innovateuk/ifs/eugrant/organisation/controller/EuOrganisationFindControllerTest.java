package org.innovateuk.ifs.eugrant.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.controller.EuOrganisationFindController;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationTypeForm;
import org.innovateuk.ifs.eugrant.organisation.populator.EuOrganisationFindModelPopulator;
import org.innovateuk.ifs.eugrant.organisation.saver.EuOrganisationSaver;
import org.innovateuk.ifs.eugrant.organisation.service.EuOrganisationCookieService;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.EuOrganisationFindViewModel;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EuOrganisationFindControllerTest extends BaseControllerMockMVCTest<EuOrganisationFindController> {

    @Mock
    private EuOrganisationSaver organisationSaver;

    @Mock
    private EuOrganisationCookieService organisationCookieService;

    @Mock
    private EuOrganisationFindModelPopulator organisationFindModelPopulator;

    @Override
    protected EuOrganisationFindController supplyControllerUnderTest() {
        return new EuOrganisationFindController();
    }

    @Test
    public void findOrganisation() throws Exception {
        EuOrganisationFindViewModel viewModel = mock(EuOrganisationFindViewModel.class);
        EuOrganisationForm form = new EuOrganisationForm();
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.of(new EuOrganisationTypeForm(EuOrganisationType.BUSINESS)));
        when(organisationFindModelPopulator.populate(eq(EuOrganisationType.BUSINESS), eq(form), any())).thenReturn(viewModel);
        mockMvc.perform(get("/organisation/find"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/find"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("organisationForm", form));
    }

    @Test
    public void findOrganisation_withoutTypeCookie() throws Exception {
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.empty());
        mockMvc.perform(get("/organisation/find"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/type"));
    }

    @Test
    public void searchOrganisation() throws Exception {
        EuOrganisationFindViewModel viewModel = mock(EuOrganisationFindViewModel.class);
        EuOrganisationForm form = new EuOrganisationForm();
        form.setOrganisationSearching(true);
        form.setOrganisationSearchName("Some Search String");
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.of(new EuOrganisationTypeForm(EuOrganisationType.BUSINESS)));
        when(organisationFindModelPopulator.populate(eq(EuOrganisationType.BUSINESS), eq(form), any())).thenReturn(viewModel);
        mockMvc.perform(post("/organisation/find")
                .param("organisationSearching", "true")
                .param("organisationSearchName", form.getOrganisationSearchName()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/find"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("organisationForm", form));
    }

    @Test
    public void searchOrganisation_emptySearchField() throws Exception {
        EuOrganisationFindViewModel viewModel = mock(EuOrganisationFindViewModel.class);
        EuOrganisationForm form = new EuOrganisationForm();
        form.setOrganisationSearching(true);
        form.setOrganisationSearchName("");
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.of(new EuOrganisationTypeForm(EuOrganisationType.BUSINESS)));
        when(organisationFindModelPopulator.populate(eq(EuOrganisationType.BUSINESS), eq(form), any())).thenReturn(viewModel);
        mockMvc.perform(post("/organisation/find")
                .param("organisationSearching", "true")
                .param("organisationSearchName", form.getOrganisationSearchName()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/find"))
                .andExpect(model().attributeHasFieldErrorCode("organisationForm", "organisationSearchName", "FieldRequiredIf"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("organisationForm", form));
    }

    @Test
    public void saveOrganisation() throws Exception {
        EuOrganisationForm form = new EuOrganisationForm();
        form.setSelectedOrganisationId("SomeOrgId123");
        when(organisationSaver.save(form, EuOrganisationType.BUSINESS)).thenReturn(serviceSuccess());
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.of(new EuOrganisationTypeForm(EuOrganisationType.BUSINESS)));
        mockMvc.perform(post("/organisation/find")
                .param("selectedOrganisationId", form.getSelectedOrganisationId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view"));

        verify(organisationSaver).save(form, EuOrganisationType.BUSINESS);
    }

    @Test
    public void saveOrganisation_emptyManualEntry() throws Exception {
        EuOrganisationFindViewModel viewModel = mock(EuOrganisationFindViewModel.class);
        EuOrganisationForm form = new EuOrganisationForm();
        form.setManualEntry(true);
        form.setOrganisationName("");
        when(organisationCookieService.getOrganisationTypeCookieValue()).thenReturn(Optional.of(new EuOrganisationTypeForm(EuOrganisationType.BUSINESS)));
        when(organisationFindModelPopulator.populate(eq(EuOrganisationType.BUSINESS), eq(form), any())).thenReturn(viewModel);
        mockMvc.perform(post("/organisation/find")
                .param("manualEntry", "true")
                .param("organisationName", form.getOrganisationName()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("organisation/find"))
                .andExpect(model().attributeHasFieldErrorCode("organisationForm", "organisationName", "FieldRequiredIf"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("organisationForm", form));

        verifyZeroInteractions(organisationSaver);
    }
}
