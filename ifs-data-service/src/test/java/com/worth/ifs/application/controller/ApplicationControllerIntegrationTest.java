package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Rollback
public class ApplicationControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationController> {

    public static final long APPLICATION_ID = 1L;
    private QuestionController questionController;
    private Long leadApplicantProcessRole;
    private Long leadApplicantId;
    private Long leadApplicantOrganisationId;

    @Before
    public void setUp() throws Exception {
        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        leadApplicantOrganisationId = 3L;
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(ApplicationController controller) {
        this.controller = controller;
    }

    @Autowired
    public void setQuestionController(QuestionController questionController) {
        this.questionController = questionController;
    }

    @Test
    public void test_updateApplication() {

        String originalTitle= "A novel solution to an old problem";
        String newTitle = "A new title";

        ApplicationResource application = controller.getApplicationById(APPLICATION_ID);
        assertEquals(originalTitle, application.getName());

        application.setName(newTitle);
        controller.saveApplicationDetails(APPLICATION_ID, application);

        ApplicationResource updated = controller.getApplicationById(APPLICATION_ID);
        assertEquals(newTitle, updated.getName());

    }

    /**
     * Check if progress decreases when marking a question as incomplete.
     */
    @Test
    public void testGetProgressPercentageByApplicationId() throws Exception {
        ObjectNode response = controller.getProgressPercentageByApplicationId(APPLICATION_ID);
        double completedPercentage = response.get("completedPercentage").asDouble();
        double delta = 0.10;
        assertEquals(42.0, completedPercentage, delta);

        questionController.markAsInComplete(28L, APPLICATION_ID, leadApplicantProcessRole);

        response = controller.getProgressPercentageByApplicationId(APPLICATION_ID);
        completedPercentage = response.get("completedPercentage").asDouble();
        assertEquals(40.0, completedPercentage, delta);
    }

    @Test
    public void testUpdateApplicationStatus() throws Exception {
        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.APPROVED.getId());
        assertEquals(ApplicationStatusConstants.APPROVED.getName(), controller.getApplicationById(APPLICATION_ID).getApplicationStatus().getName());

        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.REJECTED.getId());
        assertEquals(ApplicationStatusConstants.REJECTED.getName(), controller.getApplicationById(APPLICATION_ID).getApplicationStatus().getName());

        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.CREATED.getId());
        assertEquals(ApplicationStatusConstants.CREATED.getName(), controller.getApplicationById(APPLICATION_ID).getApplicationStatus().getName());

        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.SUBMITTED.getId());
        assertEquals(ApplicationStatusConstants.SUBMITTED.getName(), controller.getApplicationById(APPLICATION_ID).getApplicationStatus().getName());
    }

    @Test
    public void testGetApplicationsByCompetitionIdAndUserId() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L ;
        UserRoleType role = UserRoleType.LEADAPPLICANT;
        List<ApplicationResource> applications = controller.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role);

        assertEquals(5, applications.size());
        Optional<ApplicationResource> application = applications.stream().filter(a -> a.getId().equals(APPLICATION_ID)).findAny();
        assertTrue(application.isPresent());
        assertEquals(competitionId, application.get().getCompetitionId());
    }

}
