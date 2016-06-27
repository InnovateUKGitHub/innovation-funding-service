package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.AssessmentFeedback;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackBuilder.newAssessmentFeedback;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentFeedbackServiceImplTest extends BaseUnitTestMocksTest {
    @InjectMocks
    private final AssessmentFeedbackService assessmentFeedbackService = new AssessmentFeedbackServiceImpl();

    @Before
    public void setUp() throws Exception {
        when(assessmentFeedbackMapperMock.mapToDomain(any(AssessmentFeedbackResource.class))).thenAnswer(invocation -> {
            final AssessmentFeedbackResource assessmentFeedbackResource = invocation.getArgumentAt(0, AssessmentFeedbackResource.class);
            return newAssessmentFeedback()
                    .with(id(assessmentFeedbackResource.getId()))
                    .withAssessment(newAssessment().with(id(assessmentFeedbackResource.getAssessment())).build())
                    .withFeedback(assessmentFeedbackResource.getFeedback())
                    .withScore(assessmentFeedbackResource.getScore())
                    .withQuestion(newQuestion().with(id(assessmentFeedbackResource.getQuestion())).build())
                    .build();
        });
    }

    @Test
    public void getAllAssessmentFeedback() throws Exception {
        final AssessmentFeedback assessmentFeedback1 = newAssessmentFeedback().build();
        final AssessmentFeedback assessmentFeedback2 = newAssessmentFeedback().build();

        final List<AssessmentFeedback> assessmentFeedback = asList(assessmentFeedback1, assessmentFeedback2);

        final AssessmentFeedbackResource expected1 = newAssessmentFeedbackResource()
                .build();

        final AssessmentFeedbackResource expected2 = newAssessmentFeedbackResource()
                .build();

        final Long assessmentId = 1L;

        when(assessmentFeedbackRepositoryMock.findByAssessmentId(assessmentId)).thenReturn(assessmentFeedback);
        when(assessmentFeedbackMapperMock.mapToResource(same(assessmentFeedback1))).thenReturn(expected1);
        when(assessmentFeedbackMapperMock.mapToResource(same(assessmentFeedback2))).thenReturn(expected2);

        final List<AssessmentFeedbackResource> found = assessmentFeedbackService.getAllAssessmentFeedback(assessmentId).getSuccessObject();

        assertSame(expected1, found.get(0));
        assertSame(expected2, found.get(1));
        verify(assessmentFeedbackRepositoryMock, only()).findByAssessmentId(assessmentId);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedback assessmentFeedback = newAssessmentFeedback().build();

        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .build();

        final Long assessmentId = 1L;
        final Long questionId = 2L;

        when(assessmentFeedbackRepositoryMock.findByAssessmentIdAndQuestionId(assessmentId, questionId)).thenReturn(assessmentFeedback);
        when(assessmentFeedbackMapperMock.mapToResource(same(assessmentFeedback))).thenReturn(expected);

        final AssessmentFeedbackResource found = assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentFeedbackRepositoryMock, only()).findByAssessmentIdAndQuestionId(assessmentId, questionId);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion_notExists() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;

        final AssessmentFeedbackResource found = assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();
        // Despite the repository returning no result, the service should create a new assessment feedback that has yet to be saved
        assertNull(found.getId());
        assertSame(assessmentId, found.getAssessment());
        assertSame(questionId, found.getQuestion());
        verify(assessmentFeedbackRepositoryMock, only()).findByAssessmentIdAndQuestionId(assessmentId, questionId);
    }

    @Test
    public void updateFeedbackValue() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final String value = "Blah";
        final String oldValue = "Old feedback";
        final Integer score = 10;
        final AssessmentFeedback existingAssessmentFeedback = newAssessmentFeedback().build();
        final AssessmentFeedbackResource existingAssessmentFeedbackResource = newAssessmentFeedbackResource()
                .withAssessment(assessmentId)
                .withFeedback(oldValue)
                .withScore(score)
                .withQuestion(questionId)
                .build();

        final ArgumentCaptor<AssessmentFeedback> argument = ArgumentCaptor.forClass(AssessmentFeedback.class);

        when(assessmentFeedbackRepositoryMock.findByAssessmentIdAndQuestionId(assessmentId, questionId)).thenReturn(existingAssessmentFeedback);
        when(assessmentFeedbackMapperMock.mapToResource(same(existingAssessmentFeedback))).thenReturn(existingAssessmentFeedbackResource);

        final ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackValue(assessmentId, questionId, value);
        assertTrue(result.isSuccess());
        verify(assessmentFeedbackRepositoryMock, times(1)).findByAssessmentIdAndQuestionId(assessmentId, questionId);
        verify(assessmentFeedbackRepositoryMock, times(1)).save(argument.capture());

        final AssessmentFeedback saved = argument.getValue();
        assertEquals(existingAssessmentFeedbackResource.getId(), saved.getId());
        assertEquals(existingAssessmentFeedbackResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(value, saved.getFeedback());
        assertEquals(score, saved.getScore());
        assertEquals(existingAssessmentFeedbackResource.getQuestion(), saved.getQuestion().getId());
    }

    @Test
    public void updateFeedbackValue_notExists() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final String value = "Blah";
        final ArgumentCaptor<AssessmentFeedback> argument = ArgumentCaptor.forClass(AssessmentFeedback.class);

        final ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackValue(assessmentId, questionId, value);
        assertTrue(result.isSuccess());
        verify(assessmentFeedbackRepositoryMock, times(1)).findByAssessmentIdAndQuestionId(assessmentId, questionId);
        verify(assessmentFeedbackRepositoryMock, times(1)).save(argument.capture());

        final AssessmentFeedback saved = argument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertEquals(value, saved.getFeedback());
        assertNull(saved.getScore());
        assertEquals(questionId, saved.getQuestion().getId());
    }

    @Test
    public void updateScore() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final String value = "Blah";
        final Integer score = 10;
        final Integer oldScore = 5;
        final AssessmentFeedback existingAssessmentFeedback = newAssessmentFeedback().build();
        final AssessmentFeedbackResource existingAssessmentFeedbackResource = newAssessmentFeedbackResource()
                .withAssessment(assessmentId)
                .withFeedback(value)
                .withScore(oldScore)
                .withQuestion(questionId)
                .build();

        final ArgumentCaptor<AssessmentFeedback> argument = ArgumentCaptor.forClass(AssessmentFeedback.class);

        when(assessmentFeedbackRepositoryMock.findByAssessmentIdAndQuestionId(assessmentId, questionId)).thenReturn(existingAssessmentFeedback);
        when(assessmentFeedbackMapperMock.mapToResource(same(existingAssessmentFeedback))).thenReturn(existingAssessmentFeedbackResource);

        final ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackScore(assessmentId, questionId, score);
        assertTrue(result.isSuccess());
        verify(assessmentFeedbackRepositoryMock, times(1)).findByAssessmentIdAndQuestionId(assessmentId, questionId);
        verify(assessmentFeedbackRepositoryMock, times(1)).save(argument.capture());

        final AssessmentFeedback saved = argument.getValue();
        assertEquals(existingAssessmentFeedbackResource.getId(), saved.getId());
        assertEquals(existingAssessmentFeedbackResource.getAssessment(), saved.getAssessment().getId());
        assertEquals(value, saved.getFeedback());
        assertEquals(score, saved.getScore());
        assertEquals(existingAssessmentFeedbackResource.getQuestion(), saved.getQuestion().getId());
    }

    @Test
    public void updateScore_notExists() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final Integer score = 10;
        final ArgumentCaptor<AssessmentFeedback> argument = ArgumentCaptor.forClass(AssessmentFeedback.class);

        final ServiceResult<Void> result = assessmentFeedbackService.updateFeedbackScore(assessmentId, questionId, score);
        assertTrue(result.isSuccess());
        verify(assessmentFeedbackRepositoryMock, times(1)).findByAssessmentIdAndQuestionId(assessmentId, questionId);
        verify(assessmentFeedbackRepositoryMock, times(1)).save(argument.capture());

        final AssessmentFeedback saved = argument.getValue();
        assertNull(saved.getId());
        assertEquals(assessmentId, saved.getAssessment().getId());
        assertNull(saved.getFeedback());
        assertEquals(score, saved.getScore());
        assertEquals(questionId, saved.getQuestion().getId());
    }
}