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
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class KtpYourProjectCostsViewModelPopulatorTest extends BaseServiceUnitTest<YourProjectCostsViewModelPopulator> {

    private final FundingType fundingType;

    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;

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

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Override
    protected YourProjectCostsViewModelPopulator supplyServiceUnderTest() {
        YourProjectCostsViewModelPopulator populator = new YourProjectCostsViewModelPopulator();
        ReflectionTestUtils.setField(populator, "fecFinanceModelEnabled", true);
        return populator;
    }

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public KtpYourProjectCostsViewModelPopulatorTest(FundingType fundingType) {
        this.fundingType = fundingType;
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withOrganisation(organisation.getId())
                .build()));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(sectionService.getCompleted(application.getId(), organisation.getId())).thenReturn(List.of(SECTION_ID));
        when(applicantRestService.getSection(user.getId(), application.getId(), SECTION_ID)).thenReturn(applicantSection);
        when(sectionService.getFundingFinanceSection(competition.getId())).thenReturn(null);
        when(sectionService.getFecCostFinanceSection(competition.getId())).thenReturn(null);
        when(applicationFinanceRestService.getApplicationFinance(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId())).thenReturn(restSuccess(applicationFinance));
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
                .withFundingType(fundingType)
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

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

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
        when(publicContentItemRestService.getItemByCompetitionId(application.getCompetition())).thenReturn(restSuccess(publicContentItem));
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
}
