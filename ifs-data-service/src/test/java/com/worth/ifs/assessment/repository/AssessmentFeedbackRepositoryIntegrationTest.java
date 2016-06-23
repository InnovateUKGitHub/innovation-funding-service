package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.assessment.domain.AssessmentFeedback;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class AssessmentFeedbackRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentFeedbackRepository> {

    @Autowired
    @Override
    protected void setRepository(final AssessmentFeedbackRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findAll() throws Exception {
        final List<AssessmentFeedback> found = repository.findAll();

        assertTrue(found.isEmpty());
    }
}