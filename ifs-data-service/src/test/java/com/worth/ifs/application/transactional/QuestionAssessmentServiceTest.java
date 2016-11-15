package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.QuestionAssessmentBuilder;
import com.worth.ifs.application.builder.QuestionAssessmentResourceBuilder;
import com.worth.ifs.application.domain.QuestionAssessment;
import com.worth.ifs.application.mapper.QuestionAssessmentMapper;
import com.worth.ifs.application.repository.QuestionAssessmentRepository;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.*;

public class QuestionAssessmentServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected QuestionAssessmentServiceImpl questionAssessmentService = new QuestionAssessmentServiceImpl();

    @Mock
    private QuestionAssessmentRepository questionAssessmentRepository;

    @Mock
    private QuestionAssessmentMapper questionAssessmentMapper;

    @Test
    public void testGetById() throws Exception {
        long assessmentId = 1L;
        QuestionAssessment assessment = QuestionAssessmentBuilder.newQuestionAssessment().build();
        QuestionAssessmentResource resource = QuestionAssessmentResourceBuilder.newQuestionAssessment().build();

        when(questionAssessmentRepository.findOne(assessmentId)).thenReturn(assessment);
        when(questionAssessmentMapper.mapToResource(assessment)).thenReturn(resource);

        ServiceResult<QuestionAssessmentResource> result = questionAssessmentService.getById(assessmentId);

        assertThat(result.getSuccessObjectOrThrowException(), equalTo(resource));
    }

    @Test
    public void testFindByQuestion() throws Exception {
        long questionId = 1L;
        QuestionAssessment assessment = QuestionAssessmentBuilder.newQuestionAssessment().build();
        QuestionAssessmentResource resource = QuestionAssessmentResourceBuilder.newQuestionAssessment().build();

        when(questionAssessmentRepository.findByQuestionId(questionId)).thenReturn(assessment);
        when(questionAssessmentMapper.mapToResource(assessment)).thenReturn(resource);

        ServiceResult<QuestionAssessmentResource> result = questionAssessmentService.findByQuestion(questionId);

        assertThat(result.getSuccessObjectOrThrowException(), equalTo(resource));
    }
}
