package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourFinancesViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationFormPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class YourProjectLocationControllerTest extends AbstractAsyncWaitMockMVCTest<YourProjectLocationController> {

    @Mock
    private CommonYourFinancesViewModelPopulator commonYourFinancesViewModelPopulatorMock;

    @Mock
    private YourProjectLocationFormPopulator formPopulatorMock;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestServiceMock;

    @Mock
    private SectionService sectionServiceMock;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private OrganisationRestService organisationRestServiceMock;

    private long applicationId = 123L;
    private long sectionId = 456L;
    private long organisationId = 789L;

    private String postcode = "S2 5AB";
    private String postcodeTooShort = "S2";
    private String postcodeTooShortUntrimmed = "S2   ";
    private String postcodeNeedsTrimming = "S2 5AB            ";
    private String postcodeTooLong = "12345678901";

    private String yourFinancesRedirectUrl = String.format("redirect:/application/%d/form/FINANCE", applicationId);

    private ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

    private CommonYourProjectFinancesViewModel commonFinancesViewModel =
            new CommonYourProjectFinancesViewModel("/finances", "Competition name", "Application name", 1L, 2L, false, false, true, false, false);

    @Test
    public void viewPage() throws Exception {
        assertViewPageSuccessful(false);
    }

    @Test
    public void viewPageInternalUser() throws Exception {
        setLoggedInUser(admin);
        assertViewPageSuccessful(true);
    }

    private void assertViewPageSuccessful(boolean internalUser) throws Exception {

        YourProjectLocationForm form = new YourProjectLocationForm("S2 5AB", null);

        when(commonYourFinancesViewModelPopulatorMock.populate(organisationId, applicationId, sectionId, internalUser)).thenReturn(commonFinancesViewModel);
        when(formPopulatorMock.populate(applicationId, organisationId)).thenReturn(form);

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/sections/your-project-location/your-project-location"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        assertThat(model.get("model")).matches(futureMatcher(commonFinancesViewModel));
        assertThat(model.get("form")).matches(futureMatcher(form));

        verify(commonYourFinancesViewModelPopulatorMock, times(1)).populate(organisationId, applicationId, sectionId, internalUser);
        verify(formPopulatorMock, times(1)).populate(applicationId, organisationId);

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void updatePostcode() throws Exception {
        assertUpdatePostcodeSuccessful(postcode);
    }

    @Test
    public void updatePostcodeTooShortButNoValidationYet() throws Exception {
        assertUpdatePostcodeSuccessful(postcodeTooShort);
    }

    @Test
    public void updatePostcodeTooLongButNoValidationYet() throws Exception {
        assertUpdatePostcodeSuccessful(postcodeTooLong);
    }

    @Test
    public void updatePostcodeWithTrimming() throws Exception {
        assertUpdatePostcodeSuccessful(postcodeNeedsTrimming);
    }

    @Test
    public void updateInternationalTown() throws Exception {
        assertUpdateInternationalTownSuccessful("Amsterdam", "Amsterdam");
    }

    @Test
    public void updateInternationalTownWithTrimming() throws Exception {
        assertUpdateInternationalTownSuccessful("Amsterdam", "      Amsterdam  ");
    }

    @Test
    public void updateInternationalTownFixingCasing() throws Exception {
        assertUpdateInternationalTownSuccessful("Amsterdam", "aMsTeRdAm");
    }

    @Test
    public void updateInternationalTownFixingCasingForMultipleWords() throws Exception {
        assertUpdateInternationalTownSuccessful("The Hague", "the hague");
    }

    private void assertUpdatePostcodeSuccessful(String postcode) throws Exception {

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));

        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = ArgumentCaptor.forClass(ApplicationFinanceResource.class);

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        when(organisationRestServiceMock.getOrganisationById(organisationId)).thenReturn(restSuccess(OrganisationResourceBuilder.newOrganisationResource().build()));

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("postcode", postcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(yourFinancesRedirectUrl))
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getWorkPostcode()).isEqualTo(postcode.trim());

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);

        verifyNoMoreInteractionsWithMocks();
    }

    private void assertUpdateInternationalTownSuccessful(String persistedTown, String givenTown) throws Exception {

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));

        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = ArgumentCaptor.forClass(ApplicationFinanceResource.class);

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        when(organisationRestServiceMock.getOrganisationById(organisationId)).thenReturn(restSuccess(OrganisationResourceBuilder.newOrganisationResource().build()));

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("town", givenTown))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(yourFinancesRedirectUrl))
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getInternationalLocation()).isEqualTo(persistedTown);

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void autosave() throws Exception {
        assertAutosavePostcodeSuccessful(postcode);
    }

    @Test
    public void autosavePostcodeTooShortButNoValidationYet() throws Exception {
        assertAutosavePostcodeSuccessful(postcodeTooShort);
    }

    @Test
    public void autosavePostcodeTooLongButNoValidationYet() throws Exception {
        assertAutosavePostcodeSuccessful(postcodeTooLong);
    }

    @Test
    public void autosaveWithTrimming() throws Exception {
        assertAutosavePostcodeSuccessful(postcodeNeedsTrimming);
    }

    private void assertAutosavePostcodeSuccessful(String postcode) throws Exception {

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));

        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = ArgumentCaptor.forClass(ApplicationFinanceResource.class);

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}/auto-save", applicationId, organisationId, sectionId)
                    .param("postcode", postcode))
                .andExpect(status().isOk())
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getWorkPostcode()).isEqualTo(postcode.trim());

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void markAsComplete() throws Exception {
        assertMarkAsCompleteSuccessful(postcode);
    }

    @Test
    public void markAsCompleteWithTrimming() throws Exception {
        assertMarkAsCompleteSuccessful(postcodeNeedsTrimming);
    }

    private void assertMarkAsCompleteSuccessful(String postcode) throws Exception {

        when(organisationRestServiceMock.getOrganisationById(organisationId)).thenReturn(
                restSuccess(OrganisationResourceBuilder.newOrganisationResource().build()));

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));

        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = ArgumentCaptor.forClass(ApplicationFinanceResource.class);

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        ProcessRoleResource processRole = newProcessRoleResource().build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(processRole));

        when(sectionServiceMock.markAsComplete(sectionId, applicationId, processRole.getId())).thenReturn(noErrors());

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                    .param("postcode", postcode)
                    .param("mark-as-complete", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(yourFinancesRedirectUrl))
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getWorkPostcode()).isEqualTo(postcode.trim());

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);
        verify(userRestServiceMock, times(1)).findProcessRole(loggedInUser.getId(), applicationId);
        verify(sectionServiceMock, times(1)).markAsComplete(sectionId, applicationId, processRole.getId());

        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void markAsCompletePostcodeTooShort() throws Exception {
        assertPostcodeValidationErrorsWhenMarkingAsComplete(postcodeTooShort);
    }

    @Test
    public void markAsCompletePostcodeTooShortUntrimmed() throws Exception {
        assertPostcodeValidationErrorsWhenMarkingAsComplete(postcodeTooShortUntrimmed);
    }

    @Test
    public void markAsCompletePostcodeTooLong() throws Exception {
        assertPostcodeValidationErrorsWhenMarkingAsComplete(postcodeTooLong);
    }

    private void assertPostcodeValidationErrorsWhenMarkingAsComplete(String invalidPostcode) throws Exception {
        
        YourProjectLocationForm form = new YourProjectLocationForm(invalidPostcode.trim(), null);

        when(organisationRestServiceMock.getOrganisationById(organisationId)).thenReturn(
                restSuccess(OrganisationResourceBuilder.newOrganisationResource().build()));

        when(commonYourFinancesViewModelPopulatorMock.populate(organisationId, applicationId, sectionId, false)).thenReturn(commonFinancesViewModel);

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("postcode", invalidPostcode)
                .param("mark-as-complete", ""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("application/sections/your-project-location/your-project-location"))
                .andExpect(model().attribute("model", commonFinancesViewModel))
                .andExpect(model().attribute("form", form))
                .andExpect(model().attributeHasFieldErrorCode("form", "postcode", "APPLICATION_PROJECT_LOCATION_REQUIRED"));

        verify(commonYourFinancesViewModelPopulatorMock, times(1)).populate(organisationId, applicationId, sectionId, false);
        verifyNoMoreInteractionsWithMocks();
    }

    @Test
    public void markAsIncomplete() throws Exception {

        ProcessRoleResource processRole = newProcessRoleResource().build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), applicationId)).thenReturn(restSuccess(processRole));

        String viewUrl = String.format("redirect:/application/%d/form/your-project-location/" +
                "organisation/%d/section/%d", applicationId, organisationId, sectionId);

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                .param("mark-as-incomplete", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(viewUrl))
                .andReturn();

        verify(userRestServiceMock, times(1)).findProcessRole(loggedInUser.getId(), applicationId);
        verify(sectionServiceMock, times(1)).markAsInComplete(sectionId, applicationId, processRole.getId());

        verifyNoMoreInteractionsWithMocks();
    }

    private void verifyNoMoreInteractionsWithMocks() {
        verifyNoMoreInteractions(formPopulatorMock, applicationFinanceRestServiceMock,
                sectionServiceMock, userRestServiceMock);
    }

    private Predicate<Object> futureMatcher(Object object) {

        return value -> {

            if (!(value instanceof CompletableFuture)) {
                return false;
            }

            CompletableFuture future = (CompletableFuture) value;

            try {
                return future.get() == object;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    protected YourProjectLocationController supplyControllerUnderTest() {

        return new YourProjectLocationController(
                commonYourFinancesViewModelPopulatorMock,
                formPopulatorMock,
                applicationFinanceRestServiceMock,
                sectionServiceMock,
                userRestServiceMock,
                organisationRestServiceMock);
    }
}
