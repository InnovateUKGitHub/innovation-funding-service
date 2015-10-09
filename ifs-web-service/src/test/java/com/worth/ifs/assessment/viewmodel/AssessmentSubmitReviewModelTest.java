package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.incrementingIds;
import static com.worth.ifs.application.domain.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.domain.ResponseBuilder.newResponse;
import static com.worth.ifs.assessment.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.domain.CompetitionBuilder.newCompetition;
import static com.worth.ifs.user.domain.ProcessRoleBuilder.newProcessRole;

/**
 * Tests for the view model that backs the Assessor's Assessment Review page.
 *
 * Created by dwatson on 09/10/15.
 */
@Ignore("test in progress alongside development of builder framework")
public class AssessmentSubmitReviewModelTest {


    @Test
    public void test_newReviewModel() {

//        Assessment assessment, List<Response > responses, ProcessRole assessorProcessRole

        Application application = newApplication().
                build();

        Competition competition = newCompetition()
                .build();

        List<Response> responses = newResponse().
                withApplication(application).
                with(incrementingIds()).
                build(3);

        Assessment assessment = newAssessment().
                withApplication(application).
                build();

        ProcessRole assessorProcessRole = newProcessRole().
                build();

        AssessmentSubmitReviewModel model = new AssessmentSubmitReviewModel(assessment, responses, assessorProcessRole);

    }

}
