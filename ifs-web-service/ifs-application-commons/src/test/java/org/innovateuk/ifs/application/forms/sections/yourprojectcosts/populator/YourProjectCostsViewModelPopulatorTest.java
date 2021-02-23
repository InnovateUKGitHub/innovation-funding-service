package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class YourProjectCostsViewModelPopulatorTest extends BaseServiceUnitTest<YourProjectCostsViewModelPopulator> {
    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private static final long ORGANISATION_ID = 3L;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected YourProjectCostsViewModelPopulator supplyServiceUnderTest() {
        return new YourProjectCostsViewModelPopulator();
    }

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.PROCUREMENT)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .build();
        UserResource user = newUserResource().build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertEquals((long) viewModel.getApplicationId(), APPLICATION_ID);
        assertEquals((long) viewModel.getSectionId(), SECTION_ID);
        assertEquals((long) viewModel.getCompetitionId(), (long) competition.getId());
        assertEquals(viewModel.getApplicationName(), application.getName());
        assertEquals(viewModel.getCompetitionId(), competition.getId());
        assertEquals(viewModel.getOrganisationName(), organisation.getName());
        assertTrue(viewModel.isIncludeVat());
        assertTrue(viewModel.isComplete());
        assertFalse(viewModel.isOpen());
        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE/%d", APPLICATION_ID, ORGANISATION_ID));
        assertFalse(viewModel.isInternal());
        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.isProcurementCompetition());
        assertEquals("state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());
    }

    @Test
    public void populate_nonProcurement() {
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.GRANT)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .build();
        UserResource user = newUserResource().build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertFalse(viewModel.isProcurementCompetition());
        assertEquals("state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());
    }

    @Test
    public void populate_ktp_withFecModel() {
        List<FinanceRowType> expectedOrganisationFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .build();
        UserResource user = newUserResource().build();

        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(applicationFinance));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());

        assertNotNull(viewModel.getFinanceRowTypes());
        assertThat(viewModel.getFinanceRowTypes(), containsInAnyOrder(expectedOrganisationFinanceRowTypes.toArray()));
    }

    @Test
    public void populate_ktp_withNonFecModel() {
        List<FinanceRowType> expectedOrganisationFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .build();
        UserResource user = newUserResource().build();

        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(false)
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(applicationFinance));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());

        assertNotNull(viewModel.getFinanceRowTypes());
        assertThat(viewModel.getFinanceRowTypes(), containsInAnyOrder(expectedOrganisationFinanceRowTypes.toArray()));
    }
}