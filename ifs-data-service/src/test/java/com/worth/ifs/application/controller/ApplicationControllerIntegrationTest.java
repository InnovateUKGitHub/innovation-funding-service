package com.worth.ifs.application.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.mapper.ApplicationStatusMapper;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.UserRoleType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static com.worth.ifs.commons.security.SecuritySetter.swapOutForUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Rollback
public class ApplicationControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationController> {

    @Autowired
    ApplicationStatusMapper applicationStatusMapper;

    @Autowired
    UserMapper userMapper;

    public static final long APPLICATION_ID = 1L;
    private QuestionController questionController;
    private Long leadApplicantProcessRole;
    private Long leadApplicantId;

    @Before
    public void setUp() throws Exception {
        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        List<ProcessRole> proccessRoles = new ArrayList<>();
        proccessRoles.add(
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
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", proccessRoles, "123abc");
        proccessRoles.get(0).setUser(user);
        swapOutForUser(userMapper.mapToResource(user));
    }

    @After
    public void tearDown() throws Exception {
        swapOutForUser(null);
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
    public void testFindAll() {
        RestResult<List<ApplicationResource>> all = controller.findAll();
        assertEquals(5, all.getSuccessObject().size());
    }

    @Test
    public void testUpdateApplication() {
        String originalTitle= "A novel solution to an old problem";
        String newTitle = "A new title";

        ApplicationResource application = controller.getApplicationById(APPLICATION_ID).getSuccessObject();
        assertEquals(originalTitle, application.getName());

        application.setName(newTitle);
        controller.saveApplicationDetails(APPLICATION_ID, application);

        ApplicationResource updated = controller.getApplicationById(APPLICATION_ID).getSuccessObject();
        assertEquals(newTitle, updated.getName());

    }

    /**
     * Check if progress decreases when marking a question as incomplete.
     */
    @Test
    public void testGetProgressPercentageByApplicationId() throws Exception {
        CompletedPercentageResource response = controller.getProgressPercentageByApplicationId(APPLICATION_ID).getSuccessObject();
        BigDecimal completedPercentage = response.getCompletedPercentage();
        double delta = 0.10;
        assertEquals(33.8709677418, completedPercentage.doubleValue(), delta); //Changed after enabling mark as complete on some more questions for INFUND-446

        questionController.markAsInComplete(28L, APPLICATION_ID, leadApplicantProcessRole);

        CompletedPercentageResource response2  = controller.getProgressPercentageByApplicationId(APPLICATION_ID).getSuccessObject();
        BigDecimal completedPercentage2 = response2.getCompletedPercentage();
        assertEquals(32.258064516, completedPercentage2.doubleValue(), delta); //Changed after enabling mark as complete on some more questions for INFUND-446
    }

    @Test
    public void testUpdateApplicationStatusApproved() throws Exception {
        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.APPROVED.getId());
        assertEquals(ApplicationStatusConstants.APPROVED.getName(), applicationStatusMapper.mapIdToDomain(controller.getApplicationById(APPLICATION_ID).getSuccessObject().getApplicationStatus()).getName());
    }

    @Test
    public void testUpdateApplicationStatusRejected() throws Exception {
        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.REJECTED.getId());
        assertEquals(ApplicationStatusConstants.REJECTED.getName(), applicationStatusMapper.mapIdToDomain(controller.getApplicationById(APPLICATION_ID).getSuccessObject().getApplicationStatus()).getName());
    }

    @Test
    public void testUpdateApplicationStatusCreated() throws Exception {
        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.CREATED.getId());
        assertEquals(ApplicationStatusConstants.CREATED.getName(), applicationStatusMapper.mapIdToDomain(controller.getApplicationById(APPLICATION_ID).getSuccessObject().getApplicationStatus()).getName());
    }

    @Test
    public void testUpdateApplicationStatusSubmitted() throws Exception {
        ApplicationResource applicationBefore = controller.getApplicationById(APPLICATION_ID).getSuccessObject();
        assertNull(applicationBefore.getSubmittedDate());

        controller.updateApplicationStatus(APPLICATION_ID, ApplicationStatusConstants.SUBMITTED.getId());
        assertEquals(ApplicationStatusConstants.SUBMITTED.getName(), applicationStatusMapper.mapIdToDomain(controller.getApplicationById(APPLICATION_ID).getSuccessObject().getApplicationStatus()).getName());

        ApplicationResource applicationAfter = controller.getApplicationById(APPLICATION_ID).getSuccessObject();
        assertNotNull(applicationAfter.getSubmittedDate());
    }

    @Test
    public void testGetApplicationsByCompetitionIdAndUserId() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L ;
        UserRoleType role = UserRoleType.LEADAPPLICANT;
        List<ApplicationResource> applications = controller.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role).getSuccessObject();

        assertEquals(5, applications.size());
        Optional<ApplicationResource> application = applications.stream().filter(a -> a.getId().equals(APPLICATION_ID)).findAny();
        assertTrue(application.isPresent());
        assertEquals(competitionId, application.get().getCompetition());
    }

}
