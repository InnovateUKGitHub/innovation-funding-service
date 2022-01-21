package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
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

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private CompetitionThirdPartyConfigRestService competitionThirdPartyConfigRestService;

    @Override
    protected YourProjectCostsViewModelPopulator supplyServiceUnderTest() {
        YourProjectCostsViewModelPopulator populator = new YourProjectCostsViewModelPopulator();
        ReflectionTestUtils.setField(populator, "fecFinanceModelEnabled", true);
        return populator;
    }

    @Test
    public void populate() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Procurement")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.PROCUREMENT)
                .withTermsAndConditions(termsAndConditionsResource)
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
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

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
        assertEquals(BigDecimal.ZERO, viewModel.getGrantClaimPercentage());
        assertNull(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_nonProcurement() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Innovate UK")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.GRANT)
                .withTermsAndConditions(termsAndConditionsResource)
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
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertFalse(viewModel.isProcurementCompetition());
        assertEquals("state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());
        assertEquals(BigDecimal.ZERO, viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ofGem_thirdPartyProcurement() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource().build();
        CompetitionFunderResource competitionFunderResource = newCompetitionFunderResource()
                .withFunder(Funder.OFFICE_OF_GAS_AND_ELECTRICITY_MARKETS_OFGEM)
                .build();
        GrantTermsAndConditionsResource grantTermsAndConditions = newGrantTermsAndConditionsResource()
                .withName("Procurement Third Party")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.PROCUREMENT)
                .withFunders(Collections.singletonList(competitionFunderResource))
                .withTermsAndConditions(grantTermsAndConditions)
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
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertTrue(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_nonOfGem_thirdPartyProcurement() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource().build();
        CompetitionFunderResource competitionFunderResource = newCompetitionFunderResource()
                .withFunder(Funder.OTHER_STAKEHOLDERS)
                .build();
        GrantTermsAndConditionsResource grantTermsAndConditions = newGrantTermsAndConditionsResource()
                .withName("Procurement Third Party")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.PROCUREMENT)
                .withFunders(Collections.singletonList(competitionFunderResource))
                .withTermsAndConditions(grantTermsAndConditions)
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
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(ORGANISATION_ID)).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(APPLICATION_ID, ORGANISATION_ID)).thenReturn(singletonList(SECTION_ID));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, ORGANISATION_ID, user);

        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_lead_applicant_with_fec_feature_disabled() {
        ReflectionTestUtils.setField(service, "fecFinanceModelEnabled", false);
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(null)
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(null);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(null);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertFalse(viewModel.isProjectCostSectionLocked());
        assertFalse(viewModel.isYourFundingRequired());
        assertNull(viewModel.getYourFundingSectionId());
        assertFalse(viewModel.isYourFecCostRequired());
        assertNull(viewModel.getYourFecCostSectionId());
        assertNull(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_lead_applicant_with_null_your_fec_cost_section() {
        Long yourFundingSectionId = 4L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(null)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID, yourFundingSectionId));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(null);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertFalse(viewModel.isProjectCostSectionLocked());
        assertFalse(viewModel.isYourFundingRequired());
        assertEquals(yourFundingSectionId, viewModel.getYourFundingSectionId());
        assertFalse(viewModel.isYourFecCostRequired());
        assertNull(viewModel.getYourFecCostSectionId());
        assertNull(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
   }

    @Test
    public void populate_ktp_for_lead_applicant_with_project_cost_enabled() {
        Long yourFundingSectionId = 4L;
        Long yourFecCostSectionId = 5L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withTermsAndConditions(termsAndConditionsResource)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        SectionResource fecCostFinanceSection = newSectionResource()
                .withId(yourFecCostSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .withApplication(application.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID, yourFundingSectionId, yourFecCostSectionId));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(fecCostFinanceSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());
        assertFalse(viewModel.isProjectCostSectionLocked());
        assertFalse(viewModel.isYourFundingRequired());
        assertEquals(yourFundingSectionId, viewModel.getYourFundingSectionId());
        assertFalse(viewModel.isYourFecCostRequired());
        assertEquals(yourFecCostSectionId, viewModel.getYourFecCostSectionId());
        assertTrue(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_lead_applicant_with_project_cost_locked() {
        Long yourFundingSectionId = 4L;
        Long yourFecCostSectionId = 5L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        SectionResource fecCostFinanceSection = newSectionResource()
                .withId(yourFecCostSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Collections.singletonList(SECTION_ID));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(fecCostFinanceSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());
        assertTrue(viewModel.isProjectCostSectionLocked());
        assertTrue(viewModel.isYourFundingRequired());
        assertEquals(yourFundingSectionId, viewModel.getYourFundingSectionId());
        assertTrue(viewModel.isYourFecCostRequired());
        assertEquals(yourFecCostSectionId, viewModel.getYourFecCostSectionId());
        assertTrue(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_collaborator_with_default_fec_values() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));

        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Collections.singletonList(SECTION_ID));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());
        assertFalse(viewModel.isProjectCostSectionLocked());
        assertFalse(viewModel.isYourFundingRequired());
        assertNull(viewModel.getYourFundingSectionId());
        assertFalse(viewModel.isYourFecCostRequired());
        assertNull(viewModel.getYourFecCostSectionId());
        assertTrue(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_lead_applicant_withFecModel() {
        Long yourFundingSectionId = 4L;
        Long yourFecCostSectionId = 5L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        List<FinanceRowType> expectedOrganisationFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        SectionResource fecCostFinanceSection = newSectionResource()
                .withId(yourFecCostSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID, yourFundingSectionId, yourFecCostSectionId));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(fecCostFinanceSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());

        assertNotNull(viewModel.getFinanceRowTypes());
        assertThat(viewModel.getFinanceRowTypes(), containsInAnyOrder(expectedOrganisationFinanceRowTypes.toArray()));
        assertTrue(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_lead_applicant_withNonFecModel() {
        Long yourFundingSectionId = 4L;
        Long yourFecCostSectionId = 5L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        List<FinanceRowType> expectedOrganisationFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        SectionResource fecCostFinanceSection = newSectionResource()
                .withId(yourFecCostSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(false)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID, yourFundingSectionId, yourFecCostSectionId));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(fecCostFinanceSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());

        assertNotNull(viewModel.getFinanceRowTypes());
        assertThat(viewModel.getFinanceRowTypes(), containsInAnyOrder(expectedOrganisationFinanceRowTypes.toArray()));
        assertFalse(viewModel.getFecModelEnabled());
        assertTrue(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_admin_withFecModel() {
        Long yourFundingSectionId = 4L;
        Long yourFecCostSectionId = 5L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        List<FinanceRowType> expectedOrganisationFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        SectionResource fecCostFinanceSection = newSectionResource()
                .withId(yourFecCostSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID, yourFundingSectionId, yourFecCostSectionId));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(fecCostFinanceSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());

        assertNotNull(viewModel.getFinanceRowTypes());
        assertThat(viewModel.getFinanceRowTypes(), containsInAnyOrder(expectedOrganisationFinanceRowTypes.toArray()));
        assertTrue(viewModel.getFecModelEnabled());
        assertFalse(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_ktp_for_admin_withNonFecModel() {
        Long yourFundingSectionId = 4L;
        Long yourFecCostSectionId = 5L;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .build();
        GrantTermsAndConditionsResource termsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Knowledge Transfer Partnership (KTP)")
                .build();
        List<FinanceRowType> expectedOrganisationFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(ApplicationFinanceType.STANDARD_WITH_VAT)
                .withFundingType(FundingType.KTP)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withTermsAndConditions(termsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withName("orgname")
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withCompetition(competition.getId())
                .withName("Name")
                .withApplicationState(ApplicationState.OPENED)
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
                .build();
        ApplicantResource applicantResource = newApplicantResource()
                .withOrganisation(organisation)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(competition.getId())
                .build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource()
                .withSection(section)
                .withCompetition(competition)
                .withCurrentApplicant(applicantResource)
                .withCurrentUser(user)
                .build();
        SectionResource fundingFinanceSection = newSectionResource()
                .withId(yourFundingSectionId)
                .withCompetition(competition.getId())
                .build();
        SectionResource fecCostFinanceSection = newSectionResource()
                .withId(yourFecCostSectionId)
                .withCompetition(competition.getId())
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withFecEnabled(false)
                .withGrantClaimPercentage(BigDecimal.valueOf(50))
                .build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID, yourFundingSectionId, yourFecCostSectionId));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(fundingFinanceSection);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(fecCostFinanceSection);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertTrue(viewModel.isKtpCompetition());
        assertEquals("ktp_state_aid_checkbox_label", viewModel.getStateAidCheckboxLabelFragment());

        assertNotNull(viewModel.getFinanceRowTypes());
        assertThat(viewModel.getFinanceRowTypes(), containsInAnyOrder(expectedOrganisationFinanceRowTypes.toArray()));
        assertFalse(viewModel.getFecModelEnabled());
        assertTrue(viewModel.isFecModelDisabled());
        assertEquals(BigDecimal.valueOf(50), viewModel.getGrantClaimPercentage());
        assertFalse(viewModel.isOfGemCompetition());
    }

    @Test
    public void populate_thirdPartyApplication() {
        CompetitionThirdPartyConfigResource thirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .withTermsAndConditionsLabel("Test label")
                .withTermsAndConditionsGuidance("Test guidance")
                .withProjectCostGuidanceUrl("https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance")
                .build();
        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withTemplate("third-party-terms-and-conditions")
                .withName("Procurement Third Party")
                .build();
        CompetitionResource thirdPartyCompetition = newCompetitionResource()
                .withCompetitionStatus(OPEN)
                .withCompetitionThirdPartyConfig(thirdPartyConfigResource)
                .withName("Third party competition")
                .withTermsAndConditions(grantTermsAndConditionsResource)
                .build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationNumber("88L")
                .withOrganisationType(1L)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(thirdPartyCompetition.getId())
                .withLeadOrganisationId(organisation.getId())
                .withApplicationState(OPENED)
                .withName("Third party competition application")
                .withCollaborativeProject(false)
                .build();
        SectionResource section = newSectionResource()
                .withId(SECTION_ID)
                .withCompetition(thirdPartyCompetition.getId())
                .build();
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        ApplicantSectionResource applicantSectionResource = newApplicantSectionResource()
                .withCurrentUser(user)
                .withApplication(application)
                .withSection(section)
                .build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withApplication(application.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(thirdPartyCompetition.getId())).thenReturn(restSuccess(thirdPartyCompetition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(applicantRestService.getSection(user.getId(), application.getId(), section.getId())).thenReturn(applicantSectionResource);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinanceResource));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(Arrays.asList(SECTION_ID));
        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinanceResource));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(thirdPartyCompetition.getId())).thenReturn(restSuccess(thirdPartyConfigResource));

        YourProjectCostsViewModel viewModel = service.populate(application.getId(), SECTION_ID, organisation.getId(), user);

        assertEquals(thirdPartyConfigResource.getProjectCostGuidanceUrl(), viewModel.getThirdPartyProjectCostGuidanceLink());
        assertEquals(thirdPartyCompetition.getId(), viewModel.getCompetitionId());
        assertFalse(viewModel.isProcurementCompetition());
    }
}