package com.worth.ifs.assessment.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentFeedback;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackBuilder.newAssessmentFeedback;
import static org.junit.Assert.*;

public class AssessmentFeedbackRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AssessmentFeedbackRepository> {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    @Override
    protected void setRepository(final AssessmentFeedbackRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findAll() throws Exception {
        final List<AssessmentFeedback> found = repository.findAll();

        // TODO
        assertTrue(found.isEmpty());
    }

    @Test
    @Rollback
    public void findByAssessmentId() throws Exception {
        // save new feedback with a mixture of assessments

        final Assessment expectedAssessment = assessmentRepository.save(newAssessment()
                .build());

        final Assessment otherAssessment1 = assessmentRepository.save(newAssessment()
                .build());

        final Assessment otherAssessment2 = assessmentRepository.save(newAssessment()
                .build());

        final Question question1 = questionRepository.save(newQuestion()
                .build());

        final Question question2 = questionRepository.save(newQuestion()
                .build());

        // create various feedback with a combination of assessments and questions
        // the first entry will be for the expected assessment and question
        final List<AssessmentFeedback> assessmentFeedbacks = newAssessmentFeedback()
                .withAssessment(expectedAssessment, expectedAssessment, otherAssessment1, otherAssessment2)
                .withFeedback("Sample message 1", "Sample message 2", "Sample message 3", "Sample message 4")
                .withScore(10, 10, 10, 10)
                .withQuestion(question1, question2, question1, question1)
                .build(4);

        final List<AssessmentFeedback> saved = assessmentFeedbacks.stream().map(assessmentFeedback -> repository.save(assessmentFeedback)).collect(Collectors.toList());

        // check feedback can be found for the expected assessment
        final List<AssessmentFeedback> found = repository.findByAssessmentId(expectedAssessment.getId());
        assertSame(2, found.size());
        assertEquals(saved.get(0), found.get(0));
        assertEquals(saved.get(1), found.get(1));
    }

    @Test
    @Rollback
    public void findByAssessmentIdAndQuestionId() throws Exception {
        // save new feedback with a mixture of assessments and questions

        final Assessment expectedAssessment = assessmentRepository.save(newAssessment()
                .build());

        final Assessment otherAssessment = assessmentRepository.save(newAssessment()
                .build());

        final Question expectedQuestion = questionRepository.save(newQuestion()
                .build());

        final Question otherQuestion = questionRepository.save(newQuestion()
                .build());

        // create various feedback with a combination of assessments and questions
        // the first entry will be for the expected assessment and question
        final List<AssessmentFeedback> assessmentFeedbacks = newAssessmentFeedback()
                .withAssessment(expectedAssessment, expectedAssessment, otherAssessment, otherAssessment)
                .withFeedback("Sample message 1", "Sample message 2", "Sample message 3", "Sample message 4")
                .withScore(10, 10, 10, 10)
                .withQuestion(expectedQuestion, otherQuestion, expectedQuestion, otherQuestion)
                .build(4);

        final List<AssessmentFeedback> saved = assessmentFeedbacks.stream().map(assessmentFeedback -> repository.save(assessmentFeedback)).collect(Collectors.toList());

        // check a feedback can be found for the expected assessment and question
        final AssessmentFeedback found = repository.findByAssessmentIdAndQuestionId(expectedAssessment.getId(), expectedQuestion.getId());
        assertNotNull(found);
        assertEquals(saved.get(0), found);
    }
}