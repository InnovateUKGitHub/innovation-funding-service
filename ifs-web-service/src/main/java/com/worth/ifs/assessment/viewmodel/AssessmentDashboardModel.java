package com.worth.ifs.assessment.viewmodel;


import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.competition.domain.Competition;

import java.util.List;

public class AssessmentDashboardModel {

    public final List<AssessmentWithApplicationAndScore> assessments;
    public final List<AssessmentWithApplicationAndScore> assessmentsStartedAwaitingSubmission;
    public final List<AssessmentWithApplicationAndScore> submittedAssessments;
    public final Competition competition;

    public AssessmentDashboardModel(List<AssessmentWithApplicationAndScore> assessments, List<AssessmentWithApplicationAndScore> assessmentsStartedAwaitingSubmission, List<AssessmentWithApplicationAndScore> submittedAssessments, Competition competition) {
        this.assessmentsStartedAwaitingSubmission = assessmentsStartedAwaitingSubmission;
        this.competition = competition;
        this.assessments = assessments;
        this.submittedAssessments = submittedAssessments;
    }

    public List<AssessmentWithApplicationAndScore> getAssessments() {
        return assessments;
    }

    public List<AssessmentWithApplicationAndScore> getSubmittedAssessments() {
        return submittedAssessments;
    }

    public Competition getCompetition() {
        return competition;
    }

    public List<AssessmentWithApplicationAndScore> getAssessmentsStartedAwaitingSubmission() {
        return assessmentsStartedAwaitingSubmission;
    }

    public static class AssessmentWithApplicationAndScore {
        private final ApplicationResource application;
        private final Assessment assessment;
        private final Score score;

        public AssessmentWithApplicationAndScore(Assessment assessment, ApplicationResource application, Score score) {
            this.application = application;
            this.assessment = assessment;
            this.score = score;
        }

        public Score getScore() {
            return score;
        }

        public Assessment getAssessment() {
            return assessment;
        }

        public ApplicationResource getApplication() {

            return application;
        }
    }
}
