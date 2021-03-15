package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder.newPendingPartnerProgressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectYourOrganisationViewModelPopulatorTest extends BaseUnitTest {

    @Mock
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @InjectMocks
    private YourOrganisationViewModelPopulator yourOrganisationViewModelPopulator;

    @Test
    public void populate() {
//        long projectId = 1L;
//        long organisationId = 1L;

        CompetitionResource competition = newCompetitionResource()
            .withFundingType(FundingType.GRANT)
            .withIncludeJesForm(true)
            .withCompetitionTypeEnum(CompetitionTypeEnum.HORIZON_2020)
            .build();
        ProjectResource project = newProjectResource()
            .withName("proj")
            .withApplication(3L)
            .withCompetition(competition.getId()).build();
        PendingPartnerProgressResource progress = newPendingPartnerProgressResource()
            .withYourFundingCompletedOn(ZonedDateTime.now())
            .withYourOrganisationCompletedOn(ZonedDateTime.now())
            .withTermsAndConditionsCompletedOn(ZonedDateTime.now())
            .withSubsidyBasisRequired(true)
            .build();
        UserResource user = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        QuestionResource questionResource = newQuestionResource().build();

        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(progress));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), SUBSIDY_BASIS)).thenReturn(restSuccess(questionResource));
        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(project.getId(), organisation.getId())).thenReturn(restSuccess(progress));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));

        ProjectYourOrganisationViewModel actual = yourOrganisationViewModelPopulator.populate(project.getId(),
            organisation.getId(), user);

        List<FormOption> expectedOrgSizeOptions = simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));

        assertEquals(expectedOrgSizeOptions, actual.getOrganisationSizeOptions());
        assertFalse(actual.isShowOrganisationSizeAlert());
        assertEquals((long) organisation.getId(), actual.getOrganisationId());
        assertEquals((long)project.getId(), actual.getProjectId());
        assertEquals("proj", actual.getProjectName());
        assertTrue(actual.isReadOnly());
        assertTrue(actual.isSubsidyBasisRequiredAndNotCompleted());
        assertEquals(questionResource.getId(), actual.getSubsidyBasisQuestionId());

    }
}
