package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.security.SecuritySetter.swapOutForUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

@Rollback
public class ApplicationSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationSummaryController> {

    @Autowired
    private ApplicationService applicationService;

    public static final long APPLICATION_ID = 1L;
    public static final long APPLICATION_SUBMITTABLE_ID = 7L;
    public static final long COMPETITION_ID = 1L;

    @Before
    public void setUp() throws Exception {
        Long compAdminUserId = 2L;
        UserResource compAdminUser = newUserResource().withId(compAdminUserId).withFirstName("jim").withLastName("kirk").withEmail("j.kirk@starfleet.org").build();
        compAdminUser.getRoles().add(Role.COMP_ADMIN);
        swapOutForUser(compAdminUser);
    }

    @After
    public void tearDown() throws Exception {
        swapOutForUser(null);
    }

    @Test
    public void testApplicationOrderingOnCompletion() {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "percentageComplete", 0, 10, of(""));
        assertTrue(result.isSuccess());
        ApplicationSummaryPageResource applicationSummaryPageResource = result.getSuccess();
        assertNotNull(applicationSummaryPageResource);
        List<ApplicationSummaryResource> orderedOnCompletion = applicationSummaryPageResource.getContent();
        assertNotNull(orderedOnCompletion);
        assertEquals(51, orderedOnCompletion.get(0).getCompletedPercentage().intValue());
        assertEquals(33, orderedOnCompletion.get(1).getCompletedPercentage().intValue());
        assertEquals(33, orderedOnCompletion.get(2).getCompletedPercentage().intValue());
        assertEquals(0, orderedOnCompletion.get(4).getCompletedPercentage().intValue());
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(ApplicationSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void testCompetitionSummariesByCompetitionId() throws Exception {
        RestResult<CompetitionSummaryResource> result = controller.getCompetitionSummary(COMPETITION_ID);

        assertTrue(result.isSuccess());
        CompetitionSummaryResource resource = result.getSuccess();
        assertEquals(7, resource.getTotalNumberOfApplications());
        assertEquals(2, resource.getApplicationsStarted());
        assertEquals(0, resource.getApplicationsInProgress());
        assertEquals(2, resource.getApplicationsNotSubmitted());
        assertEquals(5, resource.getApplicationsSubmitted());
    }

    @Rollback
    @Test
    public void testCompetitionSummariesAfterApplicationSubmit() throws Exception {
        RestResult<CompetitionSummaryResource> result = controller.getCompetitionSummary(COMPETITION_ID);

        assertTrue(result.isSuccess());
        CompetitionSummaryResource resource = result.getSuccess();
        assertEquals(7, resource.getTotalNumberOfApplications());
        assertEquals(2, resource.getApplicationsStarted());
        assertEquals(0, resource.getApplicationsInProgress());
        assertEquals(2, resource.getApplicationsNotSubmitted());
        assertEquals(5, resource.getApplicationsSubmitted());

        Optional<ApplicationResource> application = applicationService.findAll().getSuccess()
                .stream()
                .filter(applicationResource -> applicationResource.getId().equals(APPLICATION_SUBMITTABLE_ID))
                .findFirst();
        assertTrue(application.isPresent());

        applicationService.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);

        result = controller.getCompetitionSummary(COMPETITION_ID);
        assertTrue(result.isSuccess());
        resource = result.getSuccess();
        assertEquals(7, resource.getTotalNumberOfApplications());
        assertEquals(1, resource.getApplicationsStarted());
        assertEquals(0, resource.getApplicationsInProgress());
        assertEquals(1, resource.getApplicationsNotSubmitted());
        assertEquals(6, resource.getApplicationsSubmitted());
    }

    @Test
    public void testApplicationSummariesByCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, null, 0, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(7, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
        assertEquals(Long.valueOf(APPLICATION_ID), result.getSuccess().getContent().get(0).getId());
        assertEquals("Started", result.getSuccess().getContent().get(0).getStatus());
        assertEquals("A novel solution to an old problem", result.getSuccess().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccess().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccess().getContent().get(0).getLeadApplicant());
        assertEquals(33, result.getSuccess().getContent().get(0).getCompletedPercentage().intValue());
    }

    @Test
    public void testApplicationSummaryiesByCompetitionIdFiltered() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, null, 0, 20, of("3"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(1, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
        assertEquals(3, result.getSuccess().getContent().get(0).getId().longValue());
        assertEquals("Submitted", result.getSuccess().getContent().get(0).getStatus());
        assertEquals("Mobile Phone Data for Logistics Analytics", result.getSuccess().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccess().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccess().getContent().get(0).getLeadApplicant());
        assertEquals(0, result.getSuccess().getContent().get(0).getCompletedPercentage().intValue());
    }

    @Test
    public void testApplicationSummariesByCompetitionIdSortedId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "id", 0, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(7, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
        assertEquals(APPLICATION_ID, result.getSuccess().getContent().get(0).getId().longValue());
        assertEquals("Started", result.getSuccess().getContent().get(0).getStatus());
        assertEquals("A novel solution to an old problem", result.getSuccess().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccess().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccess().getContent().get(0).getLeadApplicant());
        assertEquals(33, result.getSuccess().getContent().get(0).getCompletedPercentage().intValue());
    }

    @Test
    public void testApplicationSummariesByCompetitionIdSortedName() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "name", 0, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(7, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
        assertEquals("A new innovative solution", result.getSuccess().getContent().get(0).getName());
        assertEquals("Providing sustainable childcare", result.getSuccess().getContent().get(4).getName());
        assertEquals("Using natural gas to heat homes", result.getSuccess().getContent().get(6).getName());
    }

    @Test
    public void testApplicationSummariesByClosedCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result =
                controller.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, null, 0, 20, empty(), empty(), empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(5, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
    }

    @Test
    public void testNotSubmittedApplicationSummariesByClosedCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, null, 0, 20);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccess().getNumber());
        assertEquals(20, result.getSuccess().getSize());
        assertEquals(2, result.getSuccess().getTotalElements());
        assertEquals(1, result.getSuccess().getTotalPages());
        assertEquals(APPLICATION_ID, result.getSuccess().getContent().get(0).getId().longValue());
        assertEquals(33, result.getSuccess().getContent().get(0).getCompletedPercentage().intValue());
        assertEquals("Empire Ltd", result.getSuccess().getContent().get(0).getLead());
    }
}
