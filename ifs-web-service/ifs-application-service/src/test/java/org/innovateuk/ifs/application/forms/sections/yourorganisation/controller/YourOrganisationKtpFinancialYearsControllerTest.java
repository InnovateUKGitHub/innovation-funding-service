package org.innovateuk.ifs.application.forms.sections.yourorganisation.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourFinancesViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationKtpFinancialYearsFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.populator.ApplicationYourOrganisationViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationViewModel;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesKtpYearsResource;
import org.innovateuk.ifs.finance.service.ApplicationYourOrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.finance.builder.OrganisationFinancesKtpYearsResourceBuilder.newOrganisationFinancesKtpYearsResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class YourOrganisationKtpFinancialYearsControllerTest extends AbstractAsyncWaitMockMVCTest<YourOrganisationKtpFinancialYearsController> {

    @Mock
    private CommonYourFinancesViewModelPopulator commonFinancesViewModelPopulator;

    @Mock
    private ApplicationYourOrganisationViewModelPopulator viewModelPopulator;

    @Mock
    private ApplicationYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private YourOrganisationKtpFinancialYearsFormPopulator formPopulator;

    private long competitionId = 111L;
    private long applicationId = 123L;
    private long sectionId = 456L;
    private long organisationId = 789L;

    @Override
    protected YourOrganisationKtpFinancialYearsController supplyControllerUnderTest() {
        return new YourOrganisationKtpFinancialYearsController();
    }

    @Test
    public void viewPageAsKta() throws Exception {
        OrganisationFinancesKtpYearsResource organisationFinancesKtpYearsResource = newOrganisationFinancesKtpYearsResource().build();
        CommonYourProjectFinancesViewModel commonYourProjectFinancesViewModel = mock(CommonYourProjectFinancesViewModel.class);
        YourOrganisationViewModel yourOrganisationViewModel = mock(YourOrganisationViewModel.class);
        YourOrganisationKtpFinancialYearsForm yourOrganisationKtpFinancialYearsForm = mock(YourOrganisationKtpFinancialYearsForm.class);

        when(commonFinancesViewModelPopulator.populate(organisationId, applicationId, sectionId, getLoggedInUser())).thenReturn(commonYourProjectFinancesViewModel);
        when(viewModelPopulator.populate(applicationId, competitionId, organisationId)).thenReturn(yourOrganisationViewModel);
        when(yourOrganisationRestService.getOrganisationKtpYears(applicationId, organisationId)).thenReturn(ServiceResult.serviceSuccess(organisationFinancesKtpYearsResource));
        when(formPopulator.populate(organisationFinancesKtpYearsResource)).thenReturn(yourOrganisationKtpFinancialYearsForm);

        setLoggedInUser(kta);
        mockMvc.perform(get("/application/{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}/ktp-financial-years",
                applicationId, competitionId, organisationId, sectionId))
                .andExpect(model().attributeExists("commonFinancesModel"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("formFragment", "ktp-financial-years"))
                .andExpect(view().name("application/sections/your-organisation/your-organisation"))
                .andExpect(status().isOk());
    }

    @Test
    public void viewPageAsCofunder() throws Exception {
        OrganisationFinancesKtpYearsResource organisationFinancesKtpYearsResource = newOrganisationFinancesKtpYearsResource().build();
        CommonYourProjectFinancesViewModel commonYourProjectFinancesViewModel = mock(CommonYourProjectFinancesViewModel.class);
        YourOrganisationViewModel yourOrganisationViewModel = mock(YourOrganisationViewModel.class);
        YourOrganisationKtpFinancialYearsForm yourOrganisationKtpFinancialYearsForm = mock(YourOrganisationKtpFinancialYearsForm.class);

        when(commonFinancesViewModelPopulator.populate(organisationId, applicationId, sectionId, getLoggedInUser())).thenReturn(commonYourProjectFinancesViewModel);
        when(viewModelPopulator.populate(applicationId, competitionId, organisationId)).thenReturn(yourOrganisationViewModel);
        when(yourOrganisationRestService.getOrganisationKtpYears(applicationId, organisationId)).thenReturn(ServiceResult.serviceSuccess(organisationFinancesKtpYearsResource));
        when(formPopulator.populate(organisationFinancesKtpYearsResource)).thenReturn(yourOrganisationKtpFinancialYearsForm);

        setLoggedInUser(cofunder);
        mockMvc.perform(get("/application/{applicationId}/form/your-organisation/competition/{competitionId}/organisation/{organisationId}/section/{sectionId}/ktp-financial-years",
                applicationId, competitionId, organisationId, sectionId))
                .andExpect(model().attributeExists("commonFinancesModel"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("formFragment", "ktp-financial-years"))
                .andExpect(view().name("application/sections/your-organisation/your-organisation"))
                .andExpect(status().isOk());
    }
}
