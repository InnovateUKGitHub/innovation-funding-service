package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectYourFundingViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder.newPendingPartnerProgressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectYourFundingModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ProjectRestService projectRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private ProjectFinanceRestService projectFinanceRestService;
    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;
    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @InjectMocks
    private ProjectYourFundingViewModelPopulator projectYourFundingViewModelPopulator;

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withIncludeJesForm(true)
                .withSubsidyControl(true)
                .withCompetitionTypeEnum(CompetitionTypeEnum.HORIZON_2020)
                .build();
        ProjectResource project = newProjectResource()
                .withName("proj")
                .withApplication(3L)
                .withCompetition(competition.getId()).build();
        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withMaximumFundingLevel(10)
                .build();
        PartnerOrganisationResource partnerOrganisation = newPartnerOrganisationResource()
                .withLeadOrganisation(true)
                .build();
        PendingPartnerProgressResource progress = newPendingPartnerProgressResource()
                .withYourFundingCompletedOn(ZonedDateTime.now())
                .withYourOrganisationCompletedOn(ZonedDateTime.now())
                .withTermsAndConditionsCompletedOn(ZonedDateTime.now())
                .withSubsidyBasisCompletedOn(ZonedDateTime.now())
                .build();
        UserResource user = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        QuestionResource question = newQuestionResource().build();

        when(grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId())).thenReturn(restSuccess(false));
        when(projectFinanceRestService.getProjectFinance(project.getId(), organisation.getId())).thenReturn(restSuccess(projectFinance));
        when(partnerOrganisationRestService.getPartnerOrganisation(project.getId(), organisation.getId())).thenReturn(restSuccess(partnerOrganisation));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(progress));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS)).thenReturn(restSuccess(question));
        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(progress));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));

        ProjectYourFundingViewModel actual = projectYourFundingViewModelPopulator.populate(project.getId(),
                organisation.getId());

        assertEquals(project.getName(), actual.getProjectName());
        assertEquals((long)project.getId(), actual.getProjectId());
        assertEquals((long)organisation.getId(), actual.getOrganisationId());
        assertTrue(actual.isReadOnly());
        assertEquals(projectFinance.getMaximumFundingLevel(), actual.getMaximumFundingLevel());
        assertFalse(actual.isFundingSectionLocked());
        assertEquals((long)competition.getId(), actual.getCompetitionId());
        assertFalse(actual.isOverridingFundingRules());
        assertEquals(FundingType.GRANT, actual.getFundingType());
        assertFalse(actual.isKtpFundingType());
        assertEquals((long)organisation.getOrganisationType(), actual.getOrganisationType().getId());
        assertTrue(actual.isBusiness());
        assertFalse(actual.hideAreYouRequestingFunding());
        assertTrue(actual.isLeadOrganisation());
        assertFalse(actual.isOrganisationRequiredAndNotCompleted());
        assertFalse(actual.isSubsidyBasisRequiredAndNotCompleted());
        assertEquals(question.getId(), actual.getSubsidyBasisQuestionId());
    }
}