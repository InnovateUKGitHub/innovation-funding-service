package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.domain.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.domain.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.domain.ResponseBuilder.newResponse;
import static com.worth.ifs.application.domain.SectionBuilder.newSection;
import static com.worth.ifs.assessment.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.domain.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.domain.ProcessRoleBuilder.newProcessRole;
import static java.util.Arrays.asList;

/**
 * Tests for the view model that backs the Assessor's Assessment Review page.
 *
 * Created by dwatson on 09/10/15.
 */
public class AssessmentSubmitReviewModelTest {


    @Test
    public void test_newReviewModel() {

        List<Question> section1Questions = newQuestion().build(3);
        List<Question> section2Questions = newQuestion().build(3);
        List<Question> section3Questions = newQuestion().build(3);

        List<Section> sections = newSection()
                .withQuestionSets(asList(section1Questions, section2Questions, section3Questions))
                .build(3);

        Competition competition = newCompetition()
                .withSections(sections)
                .build();

        Application application = newApplication().
                withCompetition(competition).
                build();

        ResponseBuilder responseBuilder = newResponse().
                withApplication(application);

        List<Response> section1Responses = responseBuilder.withQuestions(section1Questions).build(3);
        List<Response> section2Responses = responseBuilder.withQuestions(section2Questions).build(3);
        List<Response> section3Responses = responseBuilder.withQuestions(section3Questions).build(3);

        Assessment assessment = newAssessment().
                withApplication(application).
                build();

        ProcessRole assessorProcessRole = newProcessRole().build();

        List<Response> allResponses = new ArrayList<>();
        allResponses.addAll(section1Responses);
        allResponses.addAll(section2Responses);
        allResponses.addAll(section3Responses);

        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, allResponses, assessorProcessRole);

    }

}
