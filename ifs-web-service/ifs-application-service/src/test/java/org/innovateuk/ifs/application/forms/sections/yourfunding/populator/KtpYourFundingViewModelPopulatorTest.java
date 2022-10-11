package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class KtpYourFundingViewModelPopulatorTest extends BaseServiceUnitTest<YourFundingViewModelPopulator> {

    private final FundingType fundingType;

    private static final Long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;
    private long ORGANISATION_ID = 3L;
    private static final long COMPETITION_ID = 4L;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private QuestionService questionService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Override
    protected YourFundingViewModelPopulator supplyServiceUnderTest() {
        return new YourFundingViewModelPopulator();
    }

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    @Before
    public void reset() {
        MockitoAnnotations.openMocks(this);
    }

    public KtpYourFundingViewModelPopulatorTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(fundingType)
                .build();
        SectionResource sectionResource = newSectionResource()
                .withId(SECTION_ID)
                .withChildSections(Collections.emptyList())
                .withCompetition(competition.getId())
                .withType(SectionType.FUNDING_FINANCES).build();
        UserResource user = newUserResource().withRoleGlobal(Role.APPLICANT).build();

        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();

        ApplicantResource applicant = newApplicantResource()
                .withProcessRole(newProcessRoleResource()
                        .withUser(user)
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .build())
                .withOrganisation(organisation)
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(APPLICATION_ID)
                .withName("Name")
                .build();
        ApplicantSectionResource section = newApplicantSectionResource()
                .withApplication(application)
                .withCompetition(competition)
                .withCurrentApplicant(applicant)
                .withApplicants(asList(applicant))
                .withSection(sectionResource)
                .withCurrentUser(user)
                .build();

        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withMaximumFundingLevel(60)
                .withFixedFundingLevel(false)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();
        QuestionResource subsidyBasisQuestion = newQuestionResource().build();
        PublicContentResource publicContent = newPublicContentResource().build();
        PublicContentItemResource publicContentItem = newPublicContentItemResource().withPublicContentResource(publicContent).build();

        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID)
                .withName("name")
                .withCompetition(COMPETITION_ID)
                .build()));

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(publicContentItemRestService.getItemByCompetitionId(COMPETITION_ID)).thenReturn(restSuccess(publicContentItem));

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(), RESEARCH_CATEGORY))
                .thenReturn(restSuccess(researchCategoryQuestion));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(), SUBSIDY_BASIS))
                .thenReturn(restSuccess(subsidyBasisQuestion));

        when(questionService
                .getQuestionStatusesForApplicationAndOrganisation(APPLICATION_ID, section.getCurrentApplicant().getOrganisation().getId()))
                .thenReturn(asMap(researchCategoryQuestion.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build(),
                        subsidyBasisQuestion.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build()));

        SectionResource yourOrgSection = newSectionResource().build();
        when(sectionService.getOrganisationFinanceSection(section.getCompetition().getId())).thenReturn(yourOrgSection);

        when(applicantRestService.getSection(user.getId(), APPLICATION_ID, SECTION_ID)).thenReturn(section);
        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(asList(yourOrgSection.getId()));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, section.getCurrentApplicant().getOrganisation().getId())).thenReturn(restSuccess(finance));
        when(grantClaimMaximumRestService.isMaximumFundingLevelConstant(section.getCompetition().getId())).thenReturn(restSuccess(true));
        when(processRoleRestService.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource().withOrganisation(organisation.getId()).build()));
        YourFundingViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, applicant.getOrganisation().getId(), user);

        assertEquals(APPLICATION_ID, viewModel.getApplicationId());
        assertEquals("Name", viewModel.getApplicationName());
        assertEquals(competition.getId().longValue(), viewModel.getCompetitionId());
        assertEquals(60, viewModel.getMaximumFundingLevel().intValue());
        assertEquals(yourOrgSection.getId().longValue(), viewModel.getYourOrganisationSectionId());
        assertEquals(researchCategoryQuestion.getId(), viewModel.getResearchCategoryQuestionId());
        assertFalse(viewModel.isFundingSectionLocked());
        assertEquals(format("/application/%d/form/FINANCE/%d", APPLICATION_ID, organisation.getId()), viewModel.getFinancesUrl());
        assertTrue(viewModel.isOverridingFundingRules());
        assertFalse(viewModel.isFixedFundingLevel());
        assertTrue(viewModel.isKtpFundingType());
    }
}
