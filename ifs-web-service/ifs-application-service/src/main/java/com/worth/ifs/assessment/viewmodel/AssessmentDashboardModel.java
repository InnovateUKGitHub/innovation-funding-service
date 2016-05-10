package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.competition.resource.CompetitionResource;

import java.beans.Transient;
import java.util.List;

public class AssessmentDashboardModel {

    public final List<AssessmentWithApplicationAndScore> assessments;
    public final List<AssessmentWithApplicationAndScore> submittedAssessments;
    public final CompetitionResource competition;

    public final long noOfAsssessmentsStartedAwaitingSubmission;

    public AssessmentDashboardModel(List<AssessmentWithApplicationAndScore> assessments, List<AssessmentWithApplicationAndScore> submittedAssessments, long noOfAssesmentsStartedAwaitingSubmission, CompetitionResource competition) {
        this.competition = competition;
        this.assessments = assessments;
        this.submittedAssessments = submittedAssessments;
        this.noOfAsssessmentsStartedAwaitingSubmission = noOfAssesmentsStartedAwaitingSubmission;
    }

    public List<AssessmentWithApplicationAndScore> getAssessments() {
        return assessments;
    }

    public List<AssessmentWithApplicationAndScore> getSubmittedAssessments() {
        return submittedAssessments;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public static class AssessmentWithApplicationAndScore {
        private final ApplicationResource application;
        private final AssessmentResource assessment;
        private final Score score;

        public AssessmentWithApplicationAndScore(AssessmentResource assessment, ApplicationResource application, Score score) {
            this.application = application;
            this.assessment = assessment;
            this.score = score;
        }

        public Score getScore() {
            return score;
        }

        public AssessmentResource getAssessment() {
            return assessment;
        }

        public ApplicationResource getApplication() {

            return application;
        }
    }

    public long getNoOfAsssessmentsStartedAwaitingSubmission() {
        return noOfAsssessmentsStartedAwaitingSubmission;
    }

    @Transient
    public boolean hasAssesmentsForSubmission(){
        return noOfAsssessmentsStartedAwaitingSubmission > 0;
    }
}
