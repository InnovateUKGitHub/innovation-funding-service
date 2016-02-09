package com.worth.ifs.application.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.QuestionStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by rav on 01/02/2016.
 *
 */
public class QuestionStatusRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<QuestionStatusRepository> {

    private long questionId;
    private long applicationId;


    @Autowired
    private QuestionStatusRepository repository;

    @Override
    @Autowired
    protected void setRepository(QuestionStatusRepository repository) {
        this.repository = repository;
    }

    @Before
    public void setup(){
        questionId = 13L;
        applicationId = 1L;
    }

    @Test
    public void test_findByQuestionIdAndApplicationIdAndAssigneeIdAndOrganisationId() {
        List<QuestionStatus> questionStatuses = repository.findByQuestionIdAndApplicationId(questionId, applicationId);
        assertEquals(1, questionStatuses.size());
    }

    public void test_findByApplicationIdAndAssigneeOrganisationId(){
        List<QuestionStatus> questionStatuses = repository.findByApplicationId(applicationId);
        assertEquals(13, questionStatuses.size());
    }
}
