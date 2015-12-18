package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.builder.AssessorFeedbackBuilder;
import com.worth.ifs.application.builder.ResponseBuilder;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.AssessorFeedbackBuilder.newFeedback;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.ResponseBuilder.newResponse;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AssessmentHandlerTest extends BaseUnitTestMocksTest {


    @InjectMocks
    AssessmentHandler assessmentHandler = new AssessmentHandler();

    @Mock
    AssessmentRepository assessmentRepository;

    @Test
    public void test_score() {

        //
        // Build the data
        //
        long assessmentId = 1L;
        ProcessRole assessorProcessRole = newProcessRole().build();
        Assessment assessment = newAssessment().withId(1L).withProcessRole(assessorProcessRole).build();

        List<Question> section1Questions = newQuestion().build(2);
        List<Question> section2Questions = newQuestion().build(2);

        List<Section> sections = newSection()
                .withQuestionSets(asList(section1Questions, section2Questions))
                .build(2);

        Competition competition = newCompetition()
                .withSections(sections)
                .build();

        long applicationId = 2L;
        Application application = newApplication()
                .withId(applicationId).
                withCompetition(competition).
                build();


        ResponseBuilder responseBuilder = newResponse().
                withApplication(application);

        AssessorFeedbackBuilder feedbackBuilder = newFeedback().
                withAssessor(assessorProcessRole).
                withAssessmentValue((i, feedback) -> (i + 1) + "");

        List<AssessorFeedback> section1ResponseFeedback = feedbackBuilder.build(2);
        List<AssessorFeedback> section2ResponseFeedback = feedbackBuilder.build(2);

        List<Response> section1Responses = responseBuilder.
                withQuestions(section1Questions).
                withFeedback(section1ResponseFeedback).
                build(2);

        List<Response> section2Responses = responseBuilder.
                withQuestions(section2Questions).
                withFeedback(section2ResponseFeedback).
                build(2);

        assessorProcessRole.setApplication(application);
        List<Response> allResponses = combineLists(section1Responses, section2Responses);

        //
        // Set the mocks
        //
        when(assessmentRepository.findById(assessmentId)).thenReturn(assessment);
        when(responseService.findResponsesByApplication(applicationId)).thenReturn(allResponses);

        //
        // Call the method under test
        //
        Score score = assessmentHandler.getScore(assessmentId);

        //
        // Assert
        //
        assertEquals(1 + 2 + 1 + 2, score.getTotal());
        assertEquals(10 * 2 * 2, score.getPossible());
        assertEquals(15, score.getPercentage());
    }

    @Test
    public void test_onlyScorableQuestionsIncluded() {

        //
        // Build the data
        //
        ProcessRole assessorProcessRole = newProcessRole().build();

        Question scorableQuestion = newQuestion().build();
        Question nonScorableQuestion = newQuestion().withNeedingAssessorScore(false).build();

        List<Section> sections = newSection()
                .withQuestions(asList(scorableQuestion, nonScorableQuestion))
                .build(1);

        Competition competition = newCompetition().withSections(sections).build();
        long applicationId = 2L;
        Application application = newApplication().withId(applicationId).withCompetition(competition).build();
        ApplicationResource applicationResource = new ApplicationResource(application);
        long assessmentId = 1L;
        Assessment assessment = newAssessment().withId(assessmentId).withProcessRole(assessorProcessRole).build();

        assessorProcessRole.setApplication(application);

        List<AssessorFeedback> feedback = newFeedback().
                withAssessor(assessorProcessRole).
                withAssessmentValue((i, fb) -> (i + 1) + "").
                build(1);

        List<Response> responses = newResponse().withApplication(application).
                withQuestions(asList(scorableQuestion)).withFeedback(feedback).build(1);

        //
        // Set the mocks
        //
        when(assessmentRepository.findById(assessmentId)).thenReturn(assessment);
        when(responseService.findResponsesByApplication(applicationId)).thenReturn(responses);

        //
        // Call the method under test
        //
        Score score = assessmentHandler.getScore(assessmentId);

        //
        // Assert
        //
        assertEquals(1, score.getTotal());
        assertEquals(10 * 1 * 1, score.getPossible());
        assertEquals(10, score.getPercentage());
    }

}