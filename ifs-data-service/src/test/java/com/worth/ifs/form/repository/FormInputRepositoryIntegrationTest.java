package com.worth.ifs.form.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInput;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * Repository Integration tests for Form Inputs.
 */
public class FormInputRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FormInputRepository> {

    @Autowired
    private FormInputRepository repository;

    @Override
    @Autowired
    protected void setRepository(FormInputRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findOne() {

        FormInput input = repository.findOne(1L);
        assertEquals(Long.valueOf(1), input.getId());
        assertEquals(Integer.valueOf(500), input.getWordCount());
        assertEquals("textarea", input.getFormInputType().getTitle());
        assertEquals(Long.valueOf(1L), ((Competition) getField(input, "competition")).getId());
        assertTrue(input.isIncludedInApplicationSummary());
        assertEquals("1. What is the business opportunity that your project addresses?", input.getDescription());
    }

    @Test
    public void test_findByCompetitionId() {

        List<FormInput> competitionInputs = repository.findByCompetitionId(1L);
        assertEquals(35, competitionInputs.size());

        FormInput first = competitionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());

        FormInput last = competitionInputs.get(34);
        assertEquals(Long.valueOf(38), last.getId());
    }

    @Test
    public void test_findByCompetitionId_nonExistentCompetition() {

        List<FormInput> competitionInputs = repository.findByCompetitionId(999L);
        assertEquals(0, competitionInputs.size());
    }

    @Test
    public void test_findOne_nonExistentInput() {

        assertEquals(null, repository.findOne(Long.MAX_VALUE));
    }

}
