package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.competition.domain.Competition;

public class QuestionData {
    private QuestionResource questionResource;
    private Competition competition;

    public QuestionResource getQuestionResource() {
        return questionResource;
    }

    public void setQuestionResource(QuestionResource questionResource) {
        this.questionResource = questionResource;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
