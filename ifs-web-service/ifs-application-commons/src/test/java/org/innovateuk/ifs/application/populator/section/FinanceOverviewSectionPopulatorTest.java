package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.OpenSectionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.FinanceOverviewSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.FUNDING_FINANCES;
import static org.innovateuk.ifs.form.resource.SectionType.ORGANISATION_FINANCES;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FinanceOverviewSectionPopulatorTest {

    @InjectMocks
    private FinanceOverviewSectionPopulator financeOverviewSectionPopulator;

    @Mock
    private OpenSectionModelPopulator openSectionModelPopulator;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private MessageSource messageSource;

    @Test
    public void populateNoReturn() {
        ApplicationForm form = new ApplicationForm();
        boolean readOnly = true;
        long applicantOrganisationId = 1L;
        BindingResult bindingResult = form.getBindingResult();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withApplication(newApplicationResource()
                        .withCollaborativeProject(false)
                        .build())
                .withSection(newSectionResource().build())
                .build();

        OpenSectionViewModel openSectionViewModel = new OpenSectionViewModel();
        FinanceOverviewSectionViewModel financeOverviewSectionViewModel = mock(FinanceOverviewSectionViewModel.class);
        Model model = mock(Model.class);

        when(messageSource.getMessage("ifs.section.financesOverview.description", null, Locale.getDefault()))
                .thenReturn("Finances overview description");
        when(openSectionModelPopulator.populateModel(form, model, bindingResult, applicantSection)).thenReturn(openSectionViewModel);

        financeOverviewSectionPopulator.populateNoReturn(applicantSection, form, financeOverviewSectionViewModel,
                model, bindingResult, readOnly, Optional.of(applicantOrganisationId));

        verify(messageSource, only()).getMessage("ifs.section.financesOverview.description", null, Locale.getDefault());
        verify(openSectionModelPopulator, only()).populateModel(form, model, bindingResult, applicantSection);
        verify(financeOverviewSectionViewModel, only()).setOpenSectionViewModel(openSectionViewModel);
    }

    @Test
    public void populateNoReturn_collaborativeProject() {
        ApplicationForm form = new ApplicationForm();
        boolean readOnly = true;
        long applicantOrganisationId = 1L;
        BindingResult bindingResult = form.getBindingResult();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withApplication(newApplicationResource()
                        .withCollaborativeProject(true)
                        .build())
                .withSection(newSectionResource().build())
                .build();

        OpenSectionViewModel openSectionViewModel = new OpenSectionViewModel();
        FinanceOverviewSectionViewModel financeOverviewSectionViewModel = mock(FinanceOverviewSectionViewModel.class);
        Model model = mock(Model.class);

        when(messageSource.getMessage("ifs.section.financesOverview.collaborative.description", null,
                Locale.getDefault()))
                .thenReturn("Finances overview collaborative description");
        when(openSectionModelPopulator.populateModel(form, model, bindingResult, applicantSection)).thenReturn(openSectionViewModel);

        financeOverviewSectionPopulator.populateNoReturn(applicantSection, form, financeOverviewSectionViewModel,
                model, bindingResult, readOnly, Optional.of(applicantOrganisationId));

        verify(messageSource, only()).getMessage("ifs.section.financesOverview.collaborative.description",
                null, Locale.getDefault());
        verify(openSectionModelPopulator, only()).populateModel(form, model, bindingResult, applicantSection);
        verify(financeOverviewSectionViewModel, only()).setOpenSectionViewModel(openSectionViewModel);
    }

    @Test
    public void createNew() {
        ApplicationResource application = newApplicationResource().build();
        SectionResource section = newSectionResource().build();
        OrganisationResource organisationResource = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId())
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisationResource)
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withApplication(application)
                .withSection(section)
                .withCurrentApplicant(applicantResource)
                .build();
        ApplicationForm form = new ApplicationForm();
        boolean readOnly = true;
        long applicantOrganisationId = 1L;
        boolean readOnlyAllApplicantApplicationFinances = true;

        FinanceOverviewSectionViewModel result = financeOverviewSectionPopulator.createNew(applicantSection, form,
                readOnly, Optional.of(applicantOrganisationId), readOnlyAllApplicantApplicationFinances);

        List<SectionType> expectedSectionTypesToSkipForOrganisationType = asList(ORGANISATION_FINANCES,
                FUNDING_FINANCES);

        assertEquals(applicantSection, result.getApplicantResource());
        assertEquals(readOnly, result.isAllReadOnly());
        assertEquals(applicantOrganisationId, result.getApplicantOrganisationId().longValue());
        assertEquals(readOnlyAllApplicantApplicationFinances, result.isReadOnlyAllApplicantApplicationFinances());

        verify(formInputViewModelGenerator, only()).fromSection(applicantSection, applicantSection, form, readOnly);
        verify(applicationNavigationPopulator, only()).addNavigation(section, application.getId(),
                expectedSectionTypesToSkipForOrganisationType);
    }

    @Test
    public void getSectionType() {
        assertEquals(SectionType.OVERVIEW_FINANCES, financeOverviewSectionPopulator.getSectionType());
    }
}