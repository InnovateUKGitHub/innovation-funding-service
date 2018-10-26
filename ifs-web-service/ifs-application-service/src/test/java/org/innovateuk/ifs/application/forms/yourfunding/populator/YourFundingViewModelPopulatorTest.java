package org.innovateuk.ifs.application.forms.yourfunding.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.yourfunding.viewmodel.ManagementYourFundingViewModel;
import org.innovateuk.ifs.application.forms.yourfunding.viewmodel.YourFundingViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class YourFundingViewModelPopulatorTest extends BaseServiceUnitTest<YourFundingViewModelPopulator> {
    private static final long APPLICATION_ID = 1L;
    private static final long SECTION_ID = 2L;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private SectionRestService sectionRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicantRestService applicantRestService;

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

        YourFundingViewModel viewModel = service.populate(APPLICATION_ID, SECTION_ID, user);

        assertEquals(viewModel.getApplicationId(), APPLICATION_ID);
        assertEquals(viewModel.getApplicationName(), "Name");
        assertEquals((Long) viewModel.getCompetitionId(), competition.getId());
        assertEquals(viewModel.getMaximumFundingLevel(), (Integer) 60);
        assertEquals((Long) viewModel.getYourOrganisationSectionId(), yourOrgSection.getId());
        assertEquals(viewModel.getResearchCategoryQuestionId(), researchCategoryQuestion.getId());
        assertEquals(viewModel.isFundingSectionLocked(), false);
        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE", APPLICATION_ID));
    }

    @Test
    public void populateManagement() {
        long organisationId = 3L;
        long competitionId = 4L;
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID)
                .withName("name")
                .withCompetition(competitionId)
                .build()));

        ManagementYourFundingViewModel viewModel = service.populateManagement(APPLICATION_ID, SECTION_ID, organisationId, "?query");


        assertEquals(viewModel.getApplicationId(), APPLICATION_ID);
        assertEquals(viewModel.getCompetitionId(), competitionId);
        assertEquals(viewModel.getApplicationName(),"name");

        assertEquals(viewModel.isFundingSectionLocked(), false);
        assertEquals(viewModel.isFundingSectionLocked(), false);
        assertEquals(viewModel.getFinancesUrl(), String.format("/application/%d/form/FINANCE/%d%s", APPLICATION_ID, organisationId, "?query"));


    }
}
