package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

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
    public static final long COMPETITION_ID = 1L;

    @Before
    public void setUp() throws Exception {
        Long compAdminUserId = 2L;
        Long compAdminRoleId = 2L;
        UserResource compAdminUser = newUserResource().withId(compAdminUserId).withFirstName("jim").withLastName("kirk").withEmail("j.kirk@starfleet.org").build();
        RoleResource compAdminRole = new RoleResource(compAdminRoleId, UserRoleType.COMP_ADMIN.getName());
        compAdminUser.getRoles().add(compAdminRole);
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
        ApplicationSummaryPageResource applicationSummaryPageResource = result.getSuccessObject();
        assertNotNull(applicationSummaryPageResource);
        List<ApplicationSummaryResource> orderedOnCompletion = applicationSummaryPageResource.getContent();
        assertNotNull(orderedOnCompletion);
        assertEquals(51, orderedOnCompletion.get(0).getCompletedPercentage());
        assertEquals(33, orderedOnCompletion.get(1).getCompletedPercentage());
        assertEquals(0, orderedOnCompletion.get(2).getCompletedPercentage());
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
        CompetitionSummaryResource resource = result.getSuccessObject();
        assertEquals(6, resource.getTotalNumberOfApplications());
        assertEquals(1, resource.getApplicationsStarted());
        assertEquals(0, resource.getApplicationsInProgress());
        assertEquals(1, resource.getApplicationsNotSubmitted());
        assertEquals(5, resource.getApplicationsSubmitted());
    }

    @Test
    public void testCompetitionSummariesAfterApplicationSubmit() throws Exception {
        RestResult<CompetitionSummaryResource> result = controller.getCompetitionSummary(COMPETITION_ID);

        assertTrue(result.isSuccess());
        CompetitionSummaryResource resource = result.getSuccessObject();
        assertEquals(6, resource.getTotalNumberOfApplications());
        assertEquals(1, resource.getApplicationsStarted());
        assertEquals(0, resource.getApplicationsInProgress());
        assertEquals(1, resource.getApplicationsNotSubmitted());
        assertEquals(5, resource.getApplicationsSubmitted());

        ApplicationResource application = applicationService.findAll().getSuccessObject().get(0);
        applicationService.updateApplicationStatus(application.getId(), ApplicationStatus.SUBMITTED);

        result = controller.getCompetitionSummary(COMPETITION_ID);
        assertTrue(result.isSuccess());
        resource = result.getSuccessObject();
        assertEquals(6, resource.getTotalNumberOfApplications());
        assertEquals(0, resource.getApplicationsStarted());
        assertEquals(0, resource.getApplicationsInProgress());
        assertEquals(0, resource.getApplicationsNotSubmitted());
        assertEquals(6, resource.getApplicationsSubmitted());
    }


    @Test
    public void testApplicationSummariesByCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, null, 0, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(6, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(APPLICATION_ID, result.getSuccessObject().getContent().get(0).getId());
        assertEquals("Started", result.getSuccessObject().getContent().get(0).getStatus());
        assertEquals("A novel solution to an old problem", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccessObject().getContent().get(0).getLeadApplicant());
        assertEquals(33, result.getSuccessObject().getContent().get(0).getCompletedPercentage());
    }

    @Test
    public void testApplicationSummaryiesByCompetitionIdFiltered() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, null, 0, 20, of("3"));

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(1, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(3, result.getSuccessObject().getContent().get(0).getId());
        assertEquals("Submitted", result.getSuccessObject().getContent().get(0).getStatus());
        assertEquals("Mobile Phone Data for Logistics Analytics", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccessObject().getContent().get(0).getLeadApplicant());
        assertEquals(0, result.getSuccessObject().getContent().get(0).getCompletedPercentage());
    }

    @Test
    public void testApplicationSummariesByCompetitionIdSortedId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "id", 0, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(6, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(APPLICATION_ID, result.getSuccessObject().getContent().get(0).getId());
        assertEquals("Started", result.getSuccessObject().getContent().get(0).getStatus());
        assertEquals("A novel solution to an old problem", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccessObject().getContent().get(0).getLeadApplicant());
        assertEquals(33, result.getSuccessObject().getContent().get(0).getCompletedPercentage());
    }

    @Test
    public void testApplicationSummariesByCompetitionIdSortedName() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "name", 0, 20, empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(6, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals("A new innovative solution", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Providing sustainable childcare", result.getSuccessObject().getContent().get(3).getName());
        assertEquals("Using natural gas to heat homes", result.getSuccessObject().getContent().get(5).getName());
    }

    @Test
    public void testApplicationSummariesByClosedCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, null, 0, 20, empty(), empty());

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(5, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
    }

    @Test
    public void testNotSubmittedApplicationSummariesByClosedCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, null, 0, 20);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(1, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(APPLICATION_ID, result.getSuccessObject().getContent().get(0).getId());
        assertEquals(33, result.getSuccessObject().getContent().get(0).getCompletedPercentage());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
    }

}
