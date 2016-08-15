package com.worth.ifs.form.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInput;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
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
        assertEquals(Integer.valueOf(400), input.getWordCount());
        assertEquals("textarea", input.getFormInputType().getTitle());
        assertEquals(Long.valueOf(1L), ((Competition) getField(input, "competition")).getId());
        assertTrue(input.isIncludedInApplicationSummary());
        assertEquals("1. What is the business opportunity that your project addresses?", input.getDescription());
    }

    @Test
    public void test_findOne_nonExistentInput() {
        assertEquals(null, repository.findOne(Long.MAX_VALUE));
    }

    @Test
    public void test_findByCompetitionId() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdOrderByPriorityAsc(1L);
        assertEquals(61, competitionInputs.size());

        FormInput first = competitionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());

        FormInput last = competitionInputs.get(competitionInputs.size() - 1);
        assertEquals(Long.valueOf(186), last.getId());
    }

    @Test
    public void test_findByCompetitionId_nonExistentCompetition() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdOrderByPriorityAsc(999L);
        assertEquals(0, competitionInputs.size());
    }

    @Test
    public void test_findByCompetitionIdAndScope() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdAndScopeOrderByPriorityAsc(1L, APPLICATION);
        assertEquals(38, competitionInputs.size());

        FormInput first = competitionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());

        FormInput last = competitionInputs.get(competitionInputs.size() - 1);
        assertEquals(Long.valueOf(18), last.getId());
    }

    @Test
    public void test_findByCompetitionIdAndScope_nonExistingCompetition() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdAndScopeOrderByPriorityAsc(999L, APPLICATION);
        assertTrue(competitionInputs.isEmpty());
    }

    @Test
    public void test_findByQuestionId() {
        List<FormInput> questionInputs = repository.findByQuestionIdOrderByPriorityAsc(1L);
        assertThat(questionInputs, hasSize(3));
        assertEquals(Long.valueOf(1), questionInputs.get(0).getId());
        assertEquals(Long.valueOf(168), questionInputs.get(1).getId());
        assertEquals(Long.valueOf(169), questionInputs.get(2).getId());
    }

    @Test
    public void test_findByQuestionId_nonExistingQuestion() {
        List<FormInput> formInputs = repository.findByQuestionIdOrderByPriorityAsc(999L);
        assertTrue(formInputs.isEmpty());
    }

    @Test
    public void test_findByQuestionIdAndScope() {
        List<FormInput> questionInputs = repository.findByQuestionIdAndScopeOrderByPriorityAsc(1L, APPLICATION);
        FormInput first = questionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());
    }

    @Test
    public void test_findByQuestionIdAndScope_nonExistingQuestion() {
        List<FormInput> questionInputs = repository.findByQuestionIdAndScopeOrderByPriorityAsc(999L, APPLICATION);
        assertTrue(questionInputs.isEmpty());
    }
}
