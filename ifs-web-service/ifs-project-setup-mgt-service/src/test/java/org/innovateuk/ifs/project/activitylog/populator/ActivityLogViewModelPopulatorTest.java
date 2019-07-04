package org.innovateuk.ifs.project.activitylog.populator;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.service.ActivityLogRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.activitylog.resource.ActivityLogResourceBuilder.newActivityLogResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivityLogViewModelPopulatorTest {

    @InjectMocks
    private ActivityLogViewModelPopulator activityLogViewModelPopulator;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ActivityLogRestService activityLogRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private MessageSource messageSource;

    @Test
    public void populate() {
        long projectId = 1L;
        long partnerUserId = 6L;
        long financeUserId = 7L;
        long organisationId = 8L;
        long documentId = 9L;
        long queryId = 10L;
        ZonedDateTime now = now();

        CompetitionResource competition = newCompetitionResource()
                .withName("Competition")
                .build();
        ProjectResource project = newProjectResource()
                .withName("Project")
                .withCompetition(competition.getId())
                .withApplication(2L)
                .build();
        PartnerOrganisationResource partner = newPartnerOrganisationResource()
                .withOrganisation(organisationId)
                .withOrganisationName("My organisation")
                .build();
        ProjectUserResource projectUserResource = newProjectUserResource()
                .withRole(Role.PROJECT_MANAGER)
                .withUser(partnerUserId)
                .withOrganisation(organisationId)
                .build();

        List<ActivityLogResource> activities = newActivityLogResource()
                .withActivityType(ActivityType.APPLICATION_SUBMITTED, ActivityType.BANK_DETAILS_APPROVED, ActivityType.DOCUMENT_UPLOADED, ActivityType.FINANCE_QUERY)
                .withCreatedBy(partnerUserId, financeUserId)
                .withCreatedByRoles(singleton(Role.APPLICANT), singleton(Role.PROJECT_FINANCE))
                .withCreatedByName("Adam applicant", "Frank finance")
                .withCreatedOn(now.minusDays(3), now.minusDays(2), now.minusDays(1),  now)
                .withOrganisation(null, organisationId)
                .withOrganisationName(null, partner.getOrganisationName())
                .withDocumentConfig(null, null, documentId, null)
                .withDocumentConfigName(null, null, "Collaboration agreement", null)
                .withQuery(null, null, null, queryId)
                .withQueryType(null, null, null, FinanceChecksSectionType.VIABILITY)
                .build(4);

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        /*

        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        List<PartnerOrganisationResource> partnerOrganisationResources = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        List<ProjectUserResource> projectUserResources = projectRestService.getProjectUsersForProject(projectId).getSuccess();
        List<ActivityLogResource> activities = activityLogRestService.findByApplicationId(project.getApplication()).getSuccess();

         */
        ActivityLogViewModel viewModel = activityLogViewModelPopulator.populate(projectId);




    }
}
