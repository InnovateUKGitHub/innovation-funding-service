package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.security.SecuritySetter.swapOutForUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

@Rollback
public class ApplicationSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationSummaryController> {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRepository applicationRepository;

    public static final long APPLICATION_ID = 1L;
    public static final long COMPETITION_ID = 1L;

    private Long leadApplicantProcessRole;
    private Long leadApplicantId;

    private Long compAdminUserId;
    private Long compAdminRoleId;

    @Before
    public void setUp() throws Exception {
        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        List<ProcessRole> leadApplicantProccessRoles = new ArrayList<>();
        leadApplicantProccessRoles.add(
                new ProcessRole(
                        leadApplicantProcessRole,
                        null,
                        new Application(
                                APPLICATION_ID,
                                "",
                                new ApplicationStatus(
                                        ApplicationStatusConstants.CREATED.getId(),
                                        ApplicationStatusConstants.CREATED.getName()
                                )
                        ),
                        null,
                        null
                )
        );
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", leadApplicantProccessRoles, "123abc");
        leadApplicantProccessRoles.get(0).setUser(user);

        compAdminUserId = 2L;
        compAdminRoleId = 2L;
        UserResource compAdminUser = newUserResource().withId(compAdminUserId).withFirstName("jim").withLastName("kirk").withEmail("j.kirk@starfleet.org").build();
        RoleResource compAdminRole = new RoleResource(compAdminRoleId, UserRoleType.COMP_ADMIN.getName(), new ArrayList<>());
        compAdminUser.getRoles().add(compAdminRole);
        swapOutForUser(compAdminUser);
    }

    @After
    public void tearDown() throws Exception {
        swapOutForUser(null);
    }

    @Test
    public void testApplicationOrderingOnCompletion() {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "percentageComplete", 0, 10);
        assertTrue(result.isSuccess());
        ApplicationSummaryPageResource applicationSummaryPageResource = result.getSuccessObject();
        assertNotNull(applicationSummaryPageResource);
        List<ApplicationSummaryResource> orderedOnCompletion = applicationSummaryPageResource.getContent();
        assertNotNull(orderedOnCompletion);
        assertEquals(new Integer(51), orderedOnCompletion.get(0).getCompletedPercentage());
        assertEquals(new Integer(33), orderedOnCompletion.get(1).getCompletedPercentage());
        assertEquals(new Integer(0), orderedOnCompletion.get(2).getCompletedPercentage());
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
        assertEquals(6, resource.getTotalNumberOfApplications().intValue());
        assertEquals(1, resource.getApplicationsStarted().intValue());
        assertEquals(0, resource.getApplicationsInProgress().intValue());
        assertEquals(1, resource.getApplicationsNotSubmitted().intValue());
        assertEquals(5, resource.getApplicationsSubmitted().intValue());
    }

    @Test
    public void testCompetitionSummariesAfterApplicationSubmit() throws Exception {
        RestResult<CompetitionSummaryResource> result = controller.getCompetitionSummary(COMPETITION_ID);

        assertTrue(result.isSuccess());
        CompetitionSummaryResource resource = result.getSuccessObject();
        assertEquals(6, resource.getTotalNumberOfApplications().intValue());
        assertEquals(1, resource.getApplicationsStarted().intValue());
        assertEquals(0, resource.getApplicationsInProgress().intValue());
        assertEquals(1, resource.getApplicationsNotSubmitted().intValue());
        assertEquals(5, resource.getApplicationsSubmitted().intValue());

        ApplicationResource application = applicationService.findAll().getSuccessObject().get(0);
        applicationService.updateApplicationStatus(application.getId(), ApplicationStatusConstants.SUBMITTED.getId());

        result = controller.getCompetitionSummary(COMPETITION_ID);
        assertTrue(result.isSuccess());
        resource = result.getSuccessObject();
        assertEquals(6, resource.getTotalNumberOfApplications().intValue());
        assertEquals(0, resource.getApplicationsStarted().intValue());
        assertEquals(0, resource.getApplicationsInProgress().intValue());
        assertEquals(0, resource.getApplicationsNotSubmitted().intValue());
        assertEquals(6, resource.getApplicationsSubmitted().intValue());
    }


    @Test
    public void testApplicationSummariesByCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, null, 0, 20);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(6, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(Long.valueOf(APPLICATION_ID), result.getSuccessObject().getContent().get(0).getId());
        assertEquals("Started", result.getSuccessObject().getContent().get(0).getStatus());
        assertEquals("A novel solution to an old problem", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccessObject().getContent().get(0).getLeadApplicant());
        assertEquals(Integer.valueOf(33), result.getSuccessObject().getContent().get(0).getCompletedPercentage());
    }

    @Test
    public void testApplicationSummariesByCompetitionIdSortedId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "id", 0, 20);

        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(6, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(Long.valueOf(APPLICATION_ID), result.getSuccessObject().getContent().get(0).getId());
        assertEquals("Started", result.getSuccessObject().getContent().get(0).getStatus());
        assertEquals("A novel solution to an old problem", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals("Steve Smith", result.getSuccessObject().getContent().get(0).getLeadApplicant());
        assertEquals(Integer.valueOf(33), result.getSuccessObject().getContent().get(0).getCompletedPercentage());
    }

    @Test
    public void testApplicationSummariesByCompetitionIdSortedName() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, "name", 0, 20);

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
        RestResult<ApplicationSummaryPageResource> result = controller.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, null, 0, 20);

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
        assertEquals(Long.valueOf(APPLICATION_ID), result.getSuccessObject().getContent().get(0).getId());
        assertEquals(Integer.valueOf(33), result.getSuccessObject().getContent().get(0).getCompletedPercentage());
        assertEquals("Empire Ltd", result.getSuccessObject().getContent().get(0).getLead());
    }

}
