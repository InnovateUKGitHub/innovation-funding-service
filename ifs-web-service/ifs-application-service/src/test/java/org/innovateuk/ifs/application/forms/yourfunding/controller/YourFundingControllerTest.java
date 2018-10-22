package org.innovateuk.ifs.application.forms.yourfunding.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.application.forms.yourfunding.populator.YourFundingFormPopulator;
import org.innovateuk.ifs.application.forms.yourfunding.populator.YourFundingViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourfunding.saver.YourFundingSaver;
import org.innovateuk.ifs.application.forms.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class YourFundingControllerTest extends BaseControllerMockMVCTest<YourFundingController> {

    @Override
    protected YourFundingController supplyControllerUnderTest() {
        return new YourFundingController();
    }

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long PROCESS_ROLE_ID = 3L;
    private static final String VIEW = "application/your-funding";

    @Mock
    private YourFundingFormPopulator formPopulator;

    @Mock
    private YourFundingViewModelPopulator viewModelPopulator;

    @Mock
    private YourFundingSaver saver;

    @Mock
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Test
    public void viewYourFunding() throws Exception {
        YourFundingViewModel viewModel = mockUnlockedViewModel();

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verify(formPopulator).populateForm(any(YourFundingForm.class), eq(APPLICATION_ID), eq(getLoggedInUser()));
    }

    @Test
    public void viewYourFunding_sectionIsLocked() throws Exception {
        YourFundingViewModel viewModel = mock(YourFundingViewModel.class);
        when(viewModelPopulator.populate(APPLICATION_ID, SECTION_ID, getLoggedInUser())).thenReturn(viewModel);
        when(viewModel.isFundingSectionLocked()).thenReturn(true);

        mockMvc.perform(get(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verifyZeroInteractions(formPopulator);
    }

    @Test
    public void saveYourFunding() throws Exception {
        when(saver.save(eq(APPLICATION_ID), any(), eq(getLoggedInUser()))).thenReturn(serviceSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID)
                .param("requestingFunding", "true")
                .param("grantClaimPercentage", "100")
                .param("otherFunding", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));
    }

    @Test
    public void edit() throws Exception {
        YourFundingViewModel viewModel = mockUnlockedViewModel();
        when(userRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(newProcessRoleResource().withId(PROCESS_ROLE_ID).build()));
        when(sectionStatusRestService.markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess());

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID)
                .param("edit", "true"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verifyZeroInteractions(saver);
        verify(sectionStatusRestService).markAsInComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
    }

    @Test
    public void complete() throws Exception {
        when(saver.save(eq(APPLICATION_ID), any(), eq(getLoggedInUser()))).thenReturn(serviceSuccess());
        when(userRestService.findProcessRole(APPLICATION_ID, getLoggedInUser().getId()))
                .thenReturn(restSuccess(newProcessRoleResource().withId(PROCESS_ROLE_ID).build()));
        when(sectionStatusRestService.markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID)).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID)
                .param("complete", "true")
                .param("requestingFunding", "true")
                .param("grantClaimPercentage", "100")
                .param("otherFunding", "false")
                .param("termsAgreed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%s/form/%s", APPLICATION_ID, SectionType.FINANCE)));

        verify(saver).save(eq(APPLICATION_ID), any(), eq(getLoggedInUser()));
        verify(sectionStatusRestService).markAsComplete(SECTION_ID, APPLICATION_ID, PROCESS_ROLE_ID);
    }


    @Test
    public void complete_missingRequiredFields() throws Exception {
        YourFundingViewModel viewModel = mockUnlockedViewModel();

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID)
                .param("complete", "true"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attributeHasFieldErrorCode("form", "requestingFunding", "NotNull"))
                .andExpect(model().attributeHasFieldErrorCode("form", "otherFunding", "NotNull"))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verifyZeroInteractions(saver);
    }

    @Test
    public void complete_withoutTermsAgreed() throws Exception {
        YourFundingViewModel viewModel = mockUnlockedViewModel();

        mockMvc.perform(post(APPLICATION_BASE_URL + "{applicationId}/form/your-funding/{sectionId}",
                APPLICATION_ID, SECTION_ID)
                .param("complete", "true")
                .param("requestingFunding", "true")
                .param("grantClaimPercentage", "100")
                .param("otherFunding", "false"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attributeHasFieldErrorCode("form", "termsAgreed", "NotNull"))
                .andExpect(view().name(VIEW))
                .andExpect(status().isOk());

        verifyZeroInteractions(sectionStatusRestService);
    }
    private YourFundingViewModel mockUnlockedViewModel() {
        YourFundingViewModel viewModel = mock(YourFundingViewModel.class);
        when(viewModelPopulator.populate(APPLICATION_ID, SECTION_ID, getLoggedInUser())).thenReturn(viewModel);
        when(viewModel.isFundingSectionLocked()).thenReturn(false);
        return viewModel;
    }
}