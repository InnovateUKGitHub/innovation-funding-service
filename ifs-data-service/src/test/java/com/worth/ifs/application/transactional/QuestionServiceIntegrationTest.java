package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Rollback
public class QuestionServiceIntegrationTest extends BaseControllerIntegrationTest<QuestionService> {


    @Autowired
    QuestionStatusRepository questionStatusRepository;

    private final Long userId = 1L;
    private final Long applicationId = 1L;
    private final Long questionId = 13L;
    private Question question;
    private Long newAssigneeProcessRoleId = 5L;
    private Long organisationId = 3L;
    private Long questionStatusId = 2L;
    private Long competitionId = 1L;
    private Long sectionId = 2L;
    public static final long QUESTION_ID_WITH_MULTIPLE = 35L;


    @Before
    public void setup(){
        question = controller.getQuestionById(questionId);
    }


    @Override
    @Autowired
    protected void setControllerUnderTest(QuestionService service) {
        this.controller = service;
    }

    @Test
    public void testIsMarkedAsComplete() throws Exception {
        assertFalse(controller.isMarkedAsComplete(question, applicationId, organisationId));

        controller.markAsComplete(questionId, applicationId, userId);

        assertTrue(controller.isMarkedAsComplete(question, applicationId, organisationId));
    }

    @Test
    public void testIsMarkedAsCompleteMultiple() throws Exception {
        question = controller.getQuestionById(QUESTION_ID_WITH_MULTIPLE);


        assertFalse(controller.isMarkedAsComplete(question, applicationId, organisationId));

        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, userId);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 2L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 8L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 9L);

        assertTrue(controller.isMarkedAsComplete(question, applicationId, organisationId));
    }
}