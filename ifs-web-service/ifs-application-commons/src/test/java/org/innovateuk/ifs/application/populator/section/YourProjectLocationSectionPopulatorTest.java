package org.innovateuk.ifs.application.populator.section;


import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.YourProjectLocationSectionPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.application.viewmodel.section.YourProjectLocationSectionViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link YourProjectLocationSectionPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class YourProjectLocationSectionPopulatorTest {

    @InjectMocks
    private YourProjectLocationSectionPopulator yourProjectLocationSectionPopulator;

    @Mock
    private ApplicationNavigationPopulator navigationPopulator;

    @Mock
    private SectionService sectionService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Test
    public void testPopulate() {
        ApplicantSectionResource section = newApplicantSectionResource()
                .withCurrentApplicant(newApplicantResource().withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build())
                .withCurrentUser(newUserResource().build())
                .withCompetition(newCompetitionResource().withCompetitionStatus(CompetitionStatus.OPEN).build())
                .withApplication(newApplicationResource().withApplicationState(ApplicationState.OPEN).build())
                .withSection(newSectionResource().build())
                .build();
        ApplicationForm form = mock(ApplicationForm.class);
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);

        AbstractFormInputViewModel organisationSize = mock(AbstractFormInputViewModel.class);
        AbstractFormInputViewModel financialYearEnd = mock(AbstractFormInputViewModel.class);
        AbstractFormInputViewModel overviewRow = mock(AbstractFormInputViewModel.class);

        List<AbstractFormInputViewModel> formInputViewModels = asList(organisationSize, financialYearEnd, overviewRow);

        when(organisationSize.getFormInput()).thenReturn(newFormInputResource().withType(FormInputType.ORGANISATION_SIZE).build());
        when(financialYearEnd.getFormInput()).thenReturn(newFormInputResource().withType(FormInputType.FINANCIAL_YEAR_END).build());
        when(overviewRow.getFormInput()).thenReturn(newFormInputResource().withType(FormInputType.FINANCIAL_OVERVIEW_ROW).build());
        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(emptyList());
        when(formInputViewModelGenerator.fromSection(section, section, form, false)).thenReturn(formInputViewModels);
        when(applicationFinanceRestService.getApplicationFinance(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(restSuccess(newApplicationFinanceResource().withWorkPostcode("Project Location").build()));

        YourProjectLocationSectionViewModel viewModel = yourProjectLocationSectionPopulator.populate(section, form, model, bindingResult, false, Optional.of(2L), true);

        assertThat(viewModel.isSection(), equalTo(true));
        assertThat(viewModel.isComplete(), equalTo(false));
        assertThat(viewModel.isReadonly(), equalTo(false));
        assertThat(viewModel.getFormInputViewModels(), equalTo(formInputViewModels));
        assertThat(viewModel.getApplicantOrganisationId(), equalTo(2L));
        assertThat(viewModel.isReadOnlyAllApplicantApplicationFinances(), equalTo(true));
        assertThat(viewModel.getProjectLocationValue(), equalTo("Project Location"));


    }

}
