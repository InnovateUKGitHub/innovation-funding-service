package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeResourceBuilder.newIneligibleOutcomeResource;
import static org.innovateuk.ifs.commons.security.SecuritySetter.swapOutForUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

@Rollback
public class ApplicationControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationController> {

    private static final long APPLICATION_ID = 1L;
    private static final long APPLICATION_SUBMITTABLE_ID = 7L;

    @Autowired
    private UserMapper userMapper;

    private QuestionStatusController questionStatusController;
    private Long leadApplicantProcessRole;
    private Long leadApplicantId;

    @Before
    public void setUp() throws Exception {
        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        List<ProcessRole> processRoles = new ArrayList<>();
        Application application = new Application("");
        application.setId(APPLICATION_ID);
        processRoles.add(newProcessRole().withId(leadApplicantProcessRole).withApplication(application).build());
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", "123abc");
        processRoles.get(0).setUser(user);
        swapOutForUser(userMapper.mapToResource(user));
    }

    @After
    public void tearDown() {
        swapOutForUser(null);
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(ApplicationController controller) {
        this.controller = controller;
    }

    @Autowired
    public void setQuestionStatusController(QuestionStatusController questionStatusController) {
        this.questionStatusController = questionStatusController;
    }

    @Test
    public void testFindAll() {
        RestResult<List<ApplicationResource>> all = controller.findAll();
        assertEquals(6, all.getSuccess().size());
    }

    @Test
    public void testUpdateApplication() {
        String originalTitle= "A novel solution to an old problem";
        String newTitle = "A new title";

        ApplicationResource application = controller.getApplicationById(APPLICATION_ID).getSuccess();
        assertEquals(originalTitle, application.getName());

        application.setName(newTitle);
        controller.saveApplicationDetails(APPLICATION_ID, application);

        ApplicationResource updated = controller.getApplicationById(APPLICATION_ID).getSuccess();
        assertEquals(newTitle, updated.getName());

    }

    /**
     * Check if progress decreases when marking a question as incomplete.
     */
    @Test
    public void testGetProgressPercentageByApplicationId() {
        CompletedPercentageResource response = controller.getProgressPercentageByApplicationId(APPLICATION_ID).getSuccess();
        BigDecimal completedPercentage = response.getCompletedPercentage();
        double delta = 0.10;
        assertEquals(33.8709677418, completedPercentage.doubleValue(), delta); //Changed after enabling mark as complete on some more questions for INFUND-446

        questionStatusController.markAsInComplete(28L, APPLICATION_ID, leadApplicantProcessRole);

        CompletedPercentageResource response2  = controller.getProgressPercentageByApplicationId(APPLICATION_ID).getSuccess();
        BigDecimal completedPercentage2 = response2.getCompletedPercentage();
        assertEquals(32.258064516, completedPercentage2.doubleValue(), delta); //Changed after enabling mark as complete on some more questions for INFUND-446
    }

    @Rollback
    @Test
    public void testUpdateApplicationStateApproved() {
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.OPEN);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.APPROVED);
        assertEquals(ApplicationState.APPROVED, controller.getApplicationById(APPLICATION_SUBMITTABLE_ID).getSuccess().getApplicationState());
    }

    @Test
    @Rollback
    public void testUpdateApplicationStateSubmittedNotPossible() {
        controller.updateApplicationState(APPLICATION_ID, ApplicationState.OPEN);
        controller.updateApplicationState(APPLICATION_ID, ApplicationState.SUBMITTED);
        assertEquals(ApplicationState.OPEN, controller.getApplicationById(APPLICATION_ID).getSuccess().getApplicationState());
    }

    @Rollback
    @Test
    public void testUpdateApplicationStateRejected() {
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.OPEN);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.REJECTED);
        assertEquals(ApplicationState.REJECTED, controller.getApplicationById(APPLICATION_SUBMITTABLE_ID).getSuccess().getApplicationState());
    }

    @Test
    public void testUpdateApplicationStateOpened() {
        controller.updateApplicationState(APPLICATION_ID, ApplicationState.OPEN);
        assertEquals(ApplicationState.OPEN, controller.getApplicationById(APPLICATION_ID).getSuccess().getApplicationState());
    }

    @Rollback
    @Test
    public void testUpdateApplicationStateSubmitted() {
        ApplicationResource applicationBefore = controller.getApplicationById(APPLICATION_SUBMITTABLE_ID).getSuccess();
        assertNull(applicationBefore.getSubmittedDate());

        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);
        assertEquals(ApplicationState.SUBMITTED, controller.getApplicationById(APPLICATION_SUBMITTABLE_ID).getSuccess().getApplicationState());

        ApplicationResource applicationAfter = controller.getApplicationById(APPLICATION_SUBMITTABLE_ID).getSuccess();
        assertNotNull(applicationAfter.getSubmittedDate());
    }

    @Test
    public void testGetApplicationsByCompetitionIdAndUserId() {
        Long competitionId = 1L;
        Long userId = 1L ;
        List<ApplicationResource> applications = controller.getApplicationsByCompetitionIdAndUserId(competitionId, userId, Role.LEADAPPLICANT).getSuccess();

        assertEquals(6, applications.size());
        Optional<ApplicationResource> application = applications.stream().filter(a -> a.getId().equals(APPLICATION_ID)).findAny();
        assertTrue(application.isPresent());
        assertEquals(competitionId, application.get().getCompetition());
    }

    @Rollback
    @Test
    public void markAsIneligible() {
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.OPEN);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);
        loginCompAdmin();

        IneligibleOutcomeResource reason = newIneligibleOutcomeResource()
                .withReason("Reason")
                .build();

        controller.markAsIneligible(APPLICATION_SUBMITTABLE_ID, reason);
        ApplicationResource applicationAfter = controller.getApplicationById(APPLICATION_SUBMITTABLE_ID).getSuccess();
        assertEquals(ApplicationState.INELIGIBLE, applicationAfter.getApplicationState());
        assertEquals(reason.getReason(), applicationAfter.getIneligibleOutcome().getReason());
    }

    @Rollback
    @Test
    public void informIneligible() {
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.OPEN);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);
        loginCompAdmin();
        controller.markAsIneligible(APPLICATION_SUBMITTABLE_ID, newIneligibleOutcomeResource().build());
        ApplicationIneligibleSendResource applicationIneligibleSendResource =
                newApplicationIneligibleSendResource()
                        .withSubject("Subject")
                        .withMessage("Message")
                        .build();

        RestResult<Void> result = controller.informIneligible(APPLICATION_SUBMITTABLE_ID, applicationIneligibleSendResource);
        assertTrue(result.isSuccess());
    }

    @Rollback
    @Test
    public void withdrawApplication() {
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.OPEN);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.SUBMITTED);
        controller.updateApplicationState(APPLICATION_SUBMITTABLE_ID, ApplicationState.APPROVED);

        loginIfsAdmin();

        RestResult<Void> result = controller.withdrawApplication(APPLICATION_SUBMITTABLE_ID);
        assertTrue(result.isSuccess());
    }
}