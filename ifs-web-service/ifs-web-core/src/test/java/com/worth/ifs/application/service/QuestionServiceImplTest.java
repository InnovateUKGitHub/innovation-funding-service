package com.worth.ifs.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import org.junit.Test;
import org.mockito.Mock;

import java.util.*;
import java.util.concurrent.Future;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class QuestionServiceImplTest extends BaseServiceUnitTest<QuestionService> {
    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Override
    protected QuestionService supplyServiceUnderTest() { return new QuestionServiceImpl(); }

    @Test
    public void testGetQuestionsByType() {
    	QuestionResource section = newQuestionResource().build();
    	when(questionRestService.getQuestionsBySectionIdAndType(1L, QuestionType.COST)).thenReturn(restSuccess(asList(section)));

    	List<QuestionResource> result = service.getQuestionsBySectionIdAndType(1L, QuestionType.COST);

    	assertEquals(1, result.size());
    	assertEquals(section, result.get(0));
    }

    @Test
    public void testAssign() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long assigneeId = 3L;
        Long assignedById = 4L;

        service.assign(questionId, applicationId, assigneeId, assignedById);

        verify(questionRestService).assign(questionId, applicationId, assigneeId, assignedById);
    }

    @Test
    public void testMarkAsComplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        List<ValidationMessages> validationMessages = new ArrayList<>();
        when(questionRestService.markAsComplete(questionId, applicationId, markedAsCompleteById)).thenReturn(restSuccess(validationMessages));

        List<ValidationMessages> result = service.markAsComplete(questionId, applicationId, markedAsCompleteById);

        verify(questionRestService).assign(questionId, applicationId, 0L, 0L);
        assertEquals(validationMessages, result);
    }

    @Test
    public void testMarkAsInComplete() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long markedAsInCompleteById = 3L;

        service.markAsInComplete(questionId, applicationId, markedAsInCompleteById);

        verify(questionRestService).markAsInComplete(questionId, applicationId, markedAsInCompleteById);
    }

    @Test
    public void testFindByCompetition() throws Exception {
        Long competitionId = 1L;
        List<QuestionResource> questions = Lists.newArrayList(new QuestionResource());
        when(questionRestService.findByCompetition(competitionId)).thenReturn(restSuccess(questions));

        List<QuestionResource> returnedQuestions = service.findByCompetition(competitionId);

        assertEquals(questions, returnedQuestions);
    }


    @Test
    public void testGetNotificationsForUser() throws Exception {
        Long userId = 1L;
        QuestionStatusResource notUserStatus = new QuestionStatusResource();
        notUserStatus.setAssigneeUserId(2L);
        notUserStatus.setNotified(false);
        QuestionStatusResource notifiedStatus = new QuestionStatusResource();
        notifiedStatus.setAssigneeUserId(userId);
        notifiedStatus.setNotified(true);
        QuestionStatusResource notNotifiedStatus =  new QuestionStatusResource();
        notNotifiedStatus.setAssigneeUserId(userId);
        notNotifiedStatus.setNotified(false);
        Collection<QuestionStatusResource> questionStatuses = Lists.newArrayList(notUserStatus, notifiedStatus, notNotifiedStatus);

        List<QuestionStatusResource> returnedQuestionStatuses = service.getNotificationsForUser(questionStatuses, userId);

        assertEquals(returnedQuestionStatuses.size(), 1);
        assertEquals(returnedQuestionStatuses.get(0), notNotifiedStatus);
    }

    @Test
    public void testRemoveNotifications() throws Exception {
        Long status1Id = 1L;
        Long status2Id = 2L;
        QuestionStatusResource status1 = new QuestionStatusResource();
        status1.setId(status1Id);
        QuestionStatusResource status2 = new QuestionStatusResource();
        status2.setId(status2Id);
        List<QuestionStatusResource> questionStatuses = Lists.newArrayList(status1, status2);

        service.removeNotifications(questionStatuses);

        verify(questionRestService).updateNotification(status1Id, true);
        verify(questionRestService).updateNotification(status2Id, true);
    }

    @Test
    public void testGetMarkedAsComplete() throws Exception {
        Long applicationId = 1L;
        Long organisationId = 2L;
        Future<Set<Long>> future = mock(Future.class);
        Set<Long> ids = Sets.newHashSet(1L);
        when(future.get()).thenReturn(ids);

        when(questionRestService.getMarkedAsComplete(applicationId, organisationId)).thenReturn(future);
        Future<Set<Long>> result = service.getMarkedAsComplete(applicationId, organisationId);

        assertEquals(ids, result.get());
    }

    @Test
    public void testGetById() throws Exception {
        Long questionId = 1L;
        QuestionResource question = new QuestionResource();
        when(questionRestService.findById(questionId)).thenReturn(restSuccess(question));

        QuestionResource returnedQuestion = service.getById(questionId);

        assertEquals(question, returnedQuestion);
    }

    @Test
    public void testGetByIdAndAssessmentId() throws Exception {
        Long questionId = 1L;
        Long assessmentId = 2L;
        QuestionResource question = new QuestionResource();
        when(questionRestService.getByIdAndAssessmentId(questionId, assessmentId)).thenReturn(restSuccess(question));

        QuestionResource returnedQuestion = service.getByIdAndAssessmentId(questionId, assessmentId);

        assertEquals(question, returnedQuestion);

        verify(questionRestService, only()).getByIdAndAssessmentId(questionId, assessmentId);
    }

    @Test
    public void testGetNextQuestion() throws Exception {
        Long questionId = 1L;
        QuestionResource question = new QuestionResource();
        when(questionRestService.getNextQuestion(questionId)).thenReturn(restSuccess(question));

        Optional<QuestionResource> result =  service.getNextQuestion(questionId);

        assertTrue(result.isPresent());
        assertEquals(question, result.get());
    }

    @Test
    public void testGetPreviousQuestion() throws Exception {
        Long questionId = 1L;
        QuestionResource question = new QuestionResource();
        when(questionRestService.getPreviousQuestion(questionId)).thenReturn(restSuccess(question));

        Optional<QuestionResource> result =  service.getPreviousQuestion(questionId);

        assertTrue(result.isPresent());
        assertEquals(question, result.get());
    }

    @Test
    public void testGetNextQuestionBySection() throws Exception {
        Long sectionId = 1L;
        QuestionResource question = new QuestionResource();
        when(questionRestService.getNextQuestionBySection(sectionId)).thenReturn(restSuccess(question));

        Optional<QuestionResource> result =  service.getNextQuestionBySection(sectionId);

        assertTrue(result.isPresent());
        assertEquals(question, result.get());
    }

    @Test
    public void testGetPreviousQuestionBySection() throws Exception {
        Long sectionId = 1L;
        QuestionResource question = new QuestionResource();
        when(questionRestService.getPreviousQuestionBySection(sectionId)).thenReturn(restSuccess(question));

        Optional<QuestionResource> result =  service.getPreviousQuestionBySection(sectionId);

        assertTrue(result.isPresent());
        assertEquals(question, result.get());
    }

    @Test
    public void testGetQuestionByFormInputType() throws Exception {
        String formInputType = "formInputType";
        QuestionResource question = new QuestionResource();
        when(questionRestService.getQuestionByCompetitionIdAndFormInputType(123L, formInputType)).thenReturn(restSuccess(question));

        RestResult<QuestionResource> result =  service.getQuestionByCompetitionIdAndFormInputType(123L, formInputType);

        assertTrue(result.isSuccess());
        assertEquals(question, result.getSuccessObject());
    }

    @Test
    public void testGetQuestionStatusesForApplicationAndOrganisation() throws Exception {
        Long applicationId = 1L;
        Long userOrganisationId = 2L;
        Long question1 = 3L;
        Long question2 = 4L;
        QuestionStatusResource status1 = new QuestionStatusResource();
        status1.setQuestion(question1);
        QuestionStatusResource status2 = new QuestionStatusResource();
        status2.setQuestion(question2);
        when(questionStatusRestService.findByApplicationAndOrganisation(applicationId, userOrganisationId)).thenReturn(restSuccess(Lists.newArrayList(status1, status2)));

        Map<Long, QuestionStatusResource>  result =  service.getQuestionStatusesForApplicationAndOrganisation(applicationId, userOrganisationId);

        assertEquals(status1, result.get(question1));
        assertEquals(status2, result.get(question2));
    }

    @Test
    public void testGetByQuestionIdAndApplicationIdAndOrganisationId() throws Exception {
        Long questionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;
        QuestionStatusResource status = new QuestionStatusResource();
        when(questionStatusRestService.findByQuestionAndApplicationAndOrganisation(questionId, applicationId, organisationId)).thenReturn(restSuccess(Lists.newArrayList(status)));

        QuestionStatusResource  result =  service.getByQuestionIdAndApplicationIdAndOrganisationId(questionId, applicationId, organisationId);

        assertEquals(result, status);
    }

    @Test
    public void testGetQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId() throws Exception {
        Long applicationId = 1L;
        Long userOrganisationId = 2L;
        Long question1 = 3L;
        Long question2 = 4L;
        QuestionStatusResource status1 = new QuestionStatusResource();
        status1.setQuestion(question1);
        QuestionStatusResource status2 = new QuestionStatusResource();
        status2.setQuestion(question2);
        List<Long> questionIds = Lists.newArrayList(question1, question2);
        when(questionStatusRestService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, userOrganisationId)).thenReturn(restSuccess(Lists.newArrayList(status1, status2)));

        Map<Long, QuestionStatusResource>  result =  service.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(questionIds, applicationId, userOrganisationId);

        assertEquals(status1, result.get(question1));
        assertEquals(status2, result.get(question2));
    }

    @Test
    public void testFindQuestionStatusesByQuestionAndApplicationId() throws Exception {
        Long applicationId = 1L;
        Long questionId = 2L;
        QuestionStatusResource status1 = new QuestionStatusResource();
        QuestionStatusResource status2 = new QuestionStatusResource();
        List<QuestionStatusResource> statuses = Lists.newArrayList(status1, status2);
        when(questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId)).thenReturn(restSuccess(statuses));

        List<QuestionStatusResource> result = service.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);

        assertEquals(statuses, result);
    }

    @Test
    public void getQuestionsByAssessment() throws Exception {
        Long assessmentId = 1L;
        List<QuestionResource> questions = newQuestionResource().build(2);

        when(questionRestService.getQuestionsByAssessment(assessmentId)).thenReturn(restSuccess(questions));
        List<QuestionResource> result = service.getQuestionsByAssessment(assessmentId);

        assertEquals(questions, result);
    }

    @Test
    public void testSave() throws Exception {
        QuestionResource question = newQuestionResource().build();

        when(questionRestService.save(question)).thenReturn(restSuccess(question));
        QuestionResource result = service.save(question);

        assertEquals(question, result);

        verify(questionRestService, only()).save(question);
    }
}
