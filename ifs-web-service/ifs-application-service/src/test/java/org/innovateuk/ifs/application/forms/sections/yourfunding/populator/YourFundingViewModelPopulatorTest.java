package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.sections.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
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
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class YourFundingViewModelPopulatorTest extends BaseServiceUnitTest<YourFundingViewModelPopulator> {
    private static final Long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;

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

    @Override
    protected YourFundingViewModelPopulator supplyServiceUnderTest() {
        return new YourFundingViewModelPopulator();
    }

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource().build();
        SectionResource sectionResource = newSectionResource()
                .withId(SECTION_ID)
                .withChildSections(Collections.emptyList())
                .withCompetition(competition.getId())
                .withType(SectionType.FUNDING_FINANCES).build();
        UserResource user = newUserResource().build();

        ApplicantResource applicant = newApplicantResource()
                .withProcessRole(newProcessRoleResource()
                        .withUser(user)
                        .withRoleName("leadapplicant")
                        .build())
                .withOrganisation(newOrganisationResource()
                        .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                        .build())
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
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(), RESEARCH_CATEGORY))
                .thenReturn(restSuccess(researchCategoryQuestion));

        when(questionService
                .getQuestionStatusesForApplicationAndOrganisation(APPLICATION_ID, section.getCurrentApplicant().getOrganisation().getId()))
                .thenReturn(asMap(researchCategoryQuestion.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build()));

        SectionResource yourOrgSection = newSectionResource().build();
        when(sectionService.getOrganisationFinanceSection(section.getCompetition().getId())).thenReturn(yourOrgSection);

        when(applicantRestService.getSection(user.getId(), APPLICATION_ID, SECTION_ID)).thenReturn(section);
        when(sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId())).thenReturn(asList(yourOrgSection.getId()));
        when(applicationFinanceRestService.getApplicationFinance(APPLICATION_ID, section.getCurrentApplicant().getOrganisation().getId())).thenReturn(restSuccess(finance));
        when(grantClaimMaximumRestService.isMaximumFundingLevelOverridden(section.getCompetition().getId())).thenReturn(restSuccess(true));

        YourFundingViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, applicant.getOrganisation().getId(), user);

        assertEquals(APPLICATION_ID, viewModel.getApplicationId());
        assertEquals("Name", viewModel.getApplicationName());
        assertEquals(competition.getId().longValue(), viewModel.getCompetitionId());
        assertEquals(60, viewModel.getMaximumFundingLevel().intValue());
        assertEquals(yourOrgSection.getId().longValue(), viewModel.getYourOrganisationSectionId());
        assertEquals(researchCategoryQuestion.getId(), viewModel.getResearchCategoryQuestionId());
        assertFalse(viewModel.isFundingSectionLocked());
        assertEquals(format("/application/%d/form/FINANCE", APPLICATION_ID), viewModel.getFinancesUrl());
        assertTrue(viewModel.isOverridingFundingRules());
    }

    @Test
    public void populateManagement() {
        long organisationId = 3L;
        long competitionId = 4L;
        CompetitionResource competition = newCompetitionResource().build();
        OrganisationResource organisation = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID)
                .withName("name")
                .withCompetition(competitionId)
                .build()));

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisation));

        YourFundingViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, organisationId, newUserResource().withRoleGlobal(Role.COMP_ADMIN).build());

        assertEquals(APPLICATION_ID, viewModel.getApplicationId());
        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals("name", viewModel.getApplicationName());
        assertEquals(OrganisationTypeEnum.BUSINESS, viewModel.getOrganisationType());
        assertFalse(viewModel.isFundingSectionLocked());
        assertFalse(viewModel.isFundingSectionLocked());
        assertEquals(format("/application/%d/form/FINANCE/%d", APPLICATION_ID, organisationId), viewModel.getFinancesUrl());
    }
}