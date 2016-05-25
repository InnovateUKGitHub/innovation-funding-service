package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.application.builder.*;
import com.worth.ifs.application.resource.*;
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

    }

    @Ignore
    @Test
    public void test_onlyCertainSectionsIncludedInSummary() {


    }

    // TODO DW - test questions that aren't scoreable
    @Ignore
    @Test
    public void test_onlyScorableQuestionsIncluded() {


    }

}
