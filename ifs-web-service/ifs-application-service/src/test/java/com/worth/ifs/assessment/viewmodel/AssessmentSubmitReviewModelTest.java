package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.application.builder.*;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.AssessorFeedbackBuilder.newFeedback;
import static com.worth.ifs.application.builder.AssessorFeedbackResourceBuilder.newAssessorFeedbackResource;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.builder.ResponseResourceBuilder.newResponseResource;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the view model that backs the Assessor's Assessment Review page.
 * TODO The long term plan for this is that model should live in the data layer
 * TODO In the meantime there is duplication here with functionality in AssesssmentHandler.getScore
 */
public class AssessmentSubmitReviewModelTest {

    @Ignore
    @Test
    public void test_newReviewModel() {

        //
        // Build the data
        //
        ProcessRoleResource assessorProcessRole = newProcessRoleResource().build();
//        Assessment assessment = newAssessment().withProcessRole(assessorProcessRole).build();

        List<QuestionResource> section1Questions = newQuestionResource().build(2);
        List<QuestionResource> section2Questions = newQuestionResource().build(2);

        List<QuestionResource> questions = new ArrayList<>(section1Questions.size() + section2Questions.size());
        questions.addAll(section1Questions);
        questions.addAll(section2Questions);

        List<SectionResource> sections = newSectionResource()
                .withId(501L, 502L)
                .with(BuilderAmendFunctions.idBasedNames("Section "))
//                .withQuestionSets(asList(section1Questions, section2Questions))
                .build(2);

        List<SectionResource> sectionResources = newSectionResource()
                .withId(501L, 502L)
                .with(BuilderAmendFunctions.idBasedNames("Section "))
                .withQuestionSets(asList(simpleMap(section1Questions, QuestionResource::getId), simpleMap(section2Questions, QuestionResource::getId)))
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withSections(simpleMap(sections, SectionResource::getId))
                .build();

        CompetitionResource competitionResource = newCompetitionResource()
                .withSections(simpleMap(sections, SectionResource::getId))
                .build();

        ApplicationResource application = newApplicationResource().
                withCompetition(competition.getId()).
                build();

        ApplicationResource applicationResource = newApplicationResource().
            withCompetition(competition.getId()).
            build();

        ResponseResourceBuilder responseResourceBuilder = newResponseResource().
                withApplication(application);

        AssessorFeedbackResourceBuilder feedbackResourceBuilder = newAssessorFeedbackResource().
//                withAssessor(assessorProcessRole).
                withAssessmentValue((i, feedback) -> (i + 1) + "");

        List<AssessorFeedbackResource> section1ResponseFeedback = feedbackResourceBuilder.build(2);
        List<AssessorFeedbackResource> section2ResponseFeedback = feedbackResourceBuilder.build(2);

        List<ResponseResource> section1Responses = responseResourceBuilder.
                withFeedback(section1ResponseFeedback).
                withQuestion(questions.get(0)).
                build(2);

        List<ResponseResource> section2Responses = responseResourceBuilder.
                withFeedback(section2ResponseFeedback).
                withQuestion(questions.get(1)).
                build(2);

        //assessorProcessRole.setApplication(application);
        List<ResponseResource> allResponses = combineLists(section1Responses, section2Responses);
        Optional<AssessorFeedbackResource> assessorFeedback = null;

        //
        // Build the model
        //
//        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, allResponses, applicationResource, competitionResource, null, questions, sectionResources);

        Map<QuestionResource, AssessorFeedbackResource> originalQuestionToFeedback = new HashMap<>();
        IntStream.range(0, section1Questions.size()).forEach(i -> originalQuestionToFeedback.put(section1Questions.get(i), section1ResponseFeedback.get(i)));
        IntStream.range(0, section2Questions.size()).forEach(i -> originalQuestionToFeedback.put(section2Questions.get(i), section2ResponseFeedback.get(i)));

        //
        // Test the top-level model attributes
        //
//        assertNotNull(model);
//        assertEquals(applicationResource, model.getApplication());
//        assertEquals(competitionResource, model.getCompetition());

        originalQuestionToFeedback.entrySet().forEach(entry -> {
            QuestionResource question = entry.getKey();
            AssessorFeedbackResource feedback = entry.getValue();
//            assertEquals(feedback, model.getFeedbackForQuestion(question));
        });

        //
        // test the questions and score sections
        //
        List<QuestionResource> allQuestions = combineLists(section1Questions, section2Questions);
//        assertEquals(allQuestions, model.getQuestions());
//        assertEquals(allQuestions, model.getScorableQuestions());
//        allQuestions.forEach(question -> assertNotNull(model.getFeedbackForQuestion(question)));

        //
        // test the section details
        //
//        assertNotNull(model.getAssessmentSummarySections());
//        assertEquals(2, model.getAssessmentSummarySections().size());

//        forEachWithIndex(model.getAssessmentSummarySections(), (i, summarySection) -> {
//
//            // check the original section details against the modelled section details
//            Section originalSection = sections.get(i);
//            assertEquals(originalSection.getId(), summarySection.getId());
//            assertEquals(originalSection.getName(), summarySection.getName());
//            assertEquals(originalSection.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());
//            assertEquals(true, summarySection.isAssessmentComplete());
//
//            // check the original questions and each relevant response for this assessor with the modelled question and
//            // feedback details
//            assertEquals(2, summarySection.getQuestionsRequiringFeedback().size());
//            forEachWithIndex(summarySection.getQuestionsRequiringFeedback(), (j, summaryQuestion) -> {
//
//                Question originalQuestion = originalSection.getQuestions().get(j);
//                assertEquals(originalQuestion.getId(), summaryQuestion.getId());
//                assertEquals(originalQuestion.getName(), summaryQuestion.getName());
//
//                AssessorFeedbackResource originalFeedback = originalQuestionToFeedback.get(originalQuestion);
//                assertEquals(originalFeedback.getAssessmentFeedback(), summaryQuestion.getFeedback().getFeedbackText());
//                assertEquals(originalFeedback.getAssessmentValue(), summaryQuestion.getFeedback().getFeedbackValue());
//            });
//        });
    }

    @Ignore
    @Test
    public void test_onlyCertainSectionsIncludedInSummary() {

        //
        // Build the data
        //
//        ProcessRole assessorProcessRole = newProcessRole().build();

        SectionBuilder sectionBuilder = newSection().withDisplayInAssessmentApplicationSummary(false);
        SectionResourceBuilder sectionResourceBuilder = newSectionResource().withDisplayInAssessmentApplicationSummary(false);
        
        List<QuestionResource> questions = newQuestionResource().build(3);

//        Section section1 = sectionBuilder.build();
//        Section section2ToBeIncluded = sectionBuilder
//                .with(BuilderAmendFunctions.idBasedNames("Section "))
//                .withDisplayInAssessmentApplicationSummary(true).build();
//        Section section3 = sectionBuilder.build();
//        List<Section> sections = asList(section1, section2ToBeIncluded, section3);

//        SectionResource sectionResource1 = sectionResourceBuilder
//                .withId(section1.getId())
//                .withQuestions(singletonList(questions.get(0).getId())).build();
//        SectionResource sectionResource2ToBeIncluded = sectionResourceBuilder
//                .withId(section2ToBeIncluded.getId())
//                .with(BuilderAmendFunctions.idBasedNames("Section "))
//                .withDisplayInAssessmentApplicationSummary(true).withQuestions(singletonList(questions.get(1).getId())).build();
//        SectionResource sectionResource3 = sectionResourceBuilder
//                .withId(section3.getId())
//                .withQuestions(singletonList(questions.get(2).getId())).build();
//        List<SectionResource> sectionResources = asList(sectionResource1, sectionResource2ToBeIncluded, sectionResource3);
//
//        Competition competition = newCompetition().withSections(sections).build();
//        CompetitionResource competitionResource = newCompetitionResource().withSections(simpleMap(sections, Section::getId)).build();
//        Application application = newApplication().withCompetition(competition).build();
//        ApplicationResource applicationResource = newApplicationResource().withCompetition(competition.getId()).build();
//        Assessment assessment = newAssessment().withProcessRole(assessorProcessRole).build();
//
//        assessorProcessRole.setApplication(application);
//        Optional<AssessorFeedbackResource> assessorFeedback = null;
//
//        //
//        // Build the model
//        //
//        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, emptyList(), applicationResource, competitionResource, null, questions, sectionResources);
//
//        //
//        // test we only see the section marked to be included
//        //
//        assertEquals(1, model.getAssessmentSummarySections().size());
//        AssessmentSummarySection summarySection = model.getAssessmentSummarySections().get(0);
//        assertEquals(section2ToBeIncluded.getId(), summarySection.getId());
//        assertEquals(section2ToBeIncluded.getName(), summarySection.getName());
//        assertEquals(section2ToBeIncluded.getQuestions().size(), summarySection.getQuestionsRequiringFeedback().size());
    }

    // TODO DW - test questions that aren't scoreable
    @Ignore
    @Test
    public void test_onlyScorableQuestionsIncluded() {

        //
        // Build the data
        //
//        ProcessRole assessorProcessRole = newProcessRole().build();
//
//        QuestionResource scorableQuestionResource = newQuestionResource().build();
//        QuestionResource nonScorableQuestionResource = newQuestionResource().withNeedingAssessorScore(false).build();
//
//        Question scorableQuestion = newQuestion().build();
//        Question nonScorableQuestion = newQuestion().withNeedingAssessorScore(false).build();
//
//
//        List<QuestionResource> questions = asList(scorableQuestionResource, nonScorableQuestionResource);
//
//        List<Section> sections = newSection()
//                .withQuestions(asList(scorableQuestion, nonScorableQuestion))
//                .build(1);
//
//        List<SectionResource> sectionResources = newSectionResource()
//                .withQuestions(simpleMap(questions, QuestionResource::getId))
//                .build(1);
//
//        Competition competition = newCompetition().withSections(sections).build();
//        CompetitionResource competitionResource = newCompetitionResource().withSections(simpleMap(sections, Section::getId)).build();
//        Application application = newApplication().withCompetition(competition).build();
//        ApplicationResource applicationResource = newApplicationResource().withCompetition(competition.getId()).build();
//        Assessment assessment = newAssessment().withProcessRole(assessorProcessRole).build();
//
//        assessorProcessRole.setApplication(application);
//
//        List<AssessorFeedbackResource> feedback = newAssessorFeedbackResource().
//                withAssessor(assessorProcessRole).
//                withAssessmentValue((i, fb) -> (i + 1) + "").
//                build(1);
//
//        List<ResponseResource> responses = newResponseResource().withApplication(application).withFeedback(feedback).build(1);
//        Optional<AssessorFeedbackResource> assessorFeedback = null;
//        //
//        // Build the model
//        //
//        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, responses, applicationResource, competitionResource, null, questions, sectionResources);
//
//        //
//        // Test the top-level model attributes
//        //
//        assertEquals(2, model.getQuestions().size());
//        assertEquals(1, model.getScorableQuestions().size());
//
//        //
//        // test the section details - in particular, test that only the scorable question is in the Section
//        //
//        assertNotNull(model.getAssessmentSummarySections());
//        assertEquals(1, model.getAssessmentSummarySections().size());
//
//        AssessmentSummarySection summarySection = model.getAssessmentSummarySections().get(0);
//        assertEquals(1, summarySection.getQuestionsRequiringFeedback().size());
//        AssessmentSummarySectionQuestion summaryQuestion = summarySection.getQuestionsRequiringFeedback().get(0);
//        assertEquals(scorableQuestion.getId(), summaryQuestion.getId());
//        assertEquals(scorableQuestion.getName(), summaryQuestion.getName());
    }

}
