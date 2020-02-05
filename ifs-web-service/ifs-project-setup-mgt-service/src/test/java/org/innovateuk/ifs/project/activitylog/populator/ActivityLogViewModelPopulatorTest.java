package org.innovateuk.ifs.project.activitylog.populator;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.service.ActivityLogRestService;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogEntryViewModel;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.activitylog.resource.ActivityLogResourceBuilder.newActivityLogResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private MessageSource messageSource;

    @Test
    public void populate() {
        long projectId = 1L;
        long competitionId = 2L;
        long partnerUserId = 6L;
        long financeUserId = 7L;
        long organisationId = 8L;
        long documentId = 9L;
        long queryId = 10L;
        String organisationName = "My organisation";
        ZonedDateTime now = now();

        UserResource user = newUserResource()
                .withRoleGlobal(Role.STAKEHOLDER)
                .build();

        ProjectResource project = newProjectResource()
                .withName("Project")
                .withCompetition(competitionId)
                .withCompetitionName("Competition")
                .withApplication(2L)
                .build();
        PartnerOrganisationResource partner = newPartnerOrganisationResource()
                .withOrganisation(organisationId)
                .withOrganisationName(organisationName)
                .build();
        ProjectUserResource projectUserResource = newProjectUserResource()
                .withRole(Role.PROJECT_MANAGER)
                .withUser(partnerUserId)
                .withOrganisation(organisationId)
                .build();

        List<ActivityLogResource> activities = newActivityLogResource()
                .withActivityType(ActivityType.APPLICATION_SUBMITTED, ActivityType.BANK_DETAILS_APPROVED, ActivityType.DOCUMENT_UPLOADED, ActivityType.FINANCE_QUERY)
                .withAuthoredBy(partnerUserId, financeUserId, partnerUserId, financeUserId)
                .withAuthoredByRoles(singleton(Role.APPLICANT), singleton(Role.PROJECT_FINANCE), singleton(Role.APPLICANT), singleton(Role.PROJECT_FINANCE))
                .withAuthoredByName("Adam Applicant", "Frank Finance", "Adam Applicant", "Frank Finance")
                .withCreatedOn(now.minusDays(3), now.minusDays(2), now.minusDays(1),  now)
                .withOrganisation(null, organisationId, null, organisationId)
                .withOrganisationName(null, partner.getOrganisationName(), null, partner.getOrganisationName())
                .withDocumentConfig(null, null, documentId, null)
                .withDocumentConfigName(null, null, "Collaboration agreement", null)
                .withQuery(null, null, null, queryId)
                .withQueryType(null, null, null, FinanceChecksSectionType.VIABILITY)
                .build(4);

        when(messageSource.getMessage(eq("ifs.activity.log.APPLICATION_SUBMITTED.title"), aryEq(new Object[]{""}), any())).thenReturn("APPLICATION_SUBMITTED");
        when(messageSource.getMessage(eq("ifs.activity.log.BANK_DETAILS_APPROVED.title"), aryEq(new Object[]{""}), any())).thenReturn("BANK_DETAILS_APPROVED");
        when(messageSource.getMessage(eq("ifs.activity.log.DOCUMENT_UPLOADED.title"), aryEq(new Object[]{""}), any())).thenReturn("DOCUMENT_UPLOADED");
        when(messageSource.getMessage(eq("ifs.activity.log.FINANCE_QUERY.title"), aryEq(new Object[]{"Viability"}), any())).thenReturn("FINANCE_QUERY");

        when(messageSource.getMessage(eq("ifs.activity.log.APPLICATION_SUBMITTED.link"), aryEq(new Object[]{null, null}), any())).thenReturn("APPLICATION_SUBMITTED");
        when(messageSource.getMessage(eq("ifs.activity.log.BANK_DETAILS_APPROVED.link"), aryEq(new Object[]{null, null}), any())).thenReturn("BANK_DETAILS_APPROVED");
        when(messageSource.getMessage(eq("ifs.activity.log.DOCUMENT_UPLOADED.link"), aryEq(new Object[]{null, "collaboration agreement"}), any())).thenReturn("DOCUMENT_UPLOADED");
        when(messageSource.getMessage(eq("ifs.activity.log.FINANCE_QUERY.link"), aryEq(new Object[]{"viability", null}), any())).thenReturn("FINANCE_QUERY");

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(singletonList(partner)));
        when(projectRestService.getProjectUsersForProject(projectId)).thenReturn(restSuccess(singletonList(projectUserResource)));
        when(activityLogRestService.findByApplicationId(project.getApplication())).thenReturn(restSuccess(activities));

        ActivityLogViewModel viewModel = activityLogViewModelPopulator.populate(projectId, user);

        assertEquals("Competition", viewModel.getCompetitionName());

        assertEquals(4, viewModel.getActivities().size());

        ActivityLogEntryViewModel applicationSubmitted = viewModel.getActivities().get(0);
        assertEquals("APPLICATION_SUBMITTED", applicationSubmitted.getTitle());
        assertEquals(null, applicationSubmitted.getOrganisationName());
        assertEquals("Adam Applicant, Project manager for My organisation", applicationSubmitted.getUserText());
        assertEquals(now.minusDays(3), applicationSubmitted.getCreatedOn());
        assertEquals("APPLICATION_SUBMITTED", applicationSubmitted.getLinkText());
        assertEquals(format("/management/competition/%d/application/%d", project.getCompetition(), project.getApplication()), applicationSubmitted.getLinkUrl());
        assertTrue(applicationSubmitted.isDisplayLink());

        ActivityLogEntryViewModel bankDetailsApproved = viewModel.getActivities().get(1);
        assertEquals("BANK_DETAILS_APPROVED", bankDetailsApproved.getTitle());
        assertEquals(organisationName, bankDetailsApproved.getOrganisationName());
        assertEquals("Frank Finance, Project Finance", bankDetailsApproved.getUserText());
        assertEquals(now.minusDays(2), bankDetailsApproved.getCreatedOn());
        assertEquals("BANK_DETAILS_APPROVED", bankDetailsApproved.getLinkText());
        assertEquals(format("/project-setup-management/project/%d/organisation/%d/review-bank-details", project.getId(), organisationId), bankDetailsApproved.getLinkUrl());
        assertFalse(bankDetailsApproved.isDisplayLink());

        ActivityLogEntryViewModel documentUploaded = viewModel.getActivities().get(2);
        assertEquals("DOCUMENT_UPLOADED", documentUploaded.getTitle());
        assertEquals(null, documentUploaded.getOrganisationName());
        assertEquals("Adam Applicant, Project manager for My organisation", documentUploaded.getUserText());
        assertEquals(now.minusDays(1), documentUploaded.getCreatedOn());
        assertEquals("DOCUMENT_UPLOADED", documentUploaded.getLinkText());
        assertEquals(format("/project-setup-management/project/%d/document/config/%d", project.getId(), documentId), documentUploaded.getLinkUrl());
        assertFalse(documentUploaded.isDisplayLink());

        ActivityLogEntryViewModel financeQuery = viewModel.getActivities().get(3);
        assertEquals("FINANCE_QUERY", financeQuery.getTitle());
        assertEquals(organisationName, financeQuery.getOrganisationName());
        assertEquals("Frank Finance, Project Finance", financeQuery.getUserText());
        assertEquals(now, financeQuery.getCreatedOn());
        assertEquals("FINANCE_QUERY", financeQuery.getLinkText());
        assertEquals(format("/project-setup-management/project/%d/finance-check/organisation/%d/query?query_section=%s", project.getId(), organisationId, "VIABILITY"), financeQuery.getLinkUrl());
        assertFalse(financeQuery.isDisplayLink());

    }
}
