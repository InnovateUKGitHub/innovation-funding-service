package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competitionsetup.model.Question;

import java.util.List;

/**
 * Form for the application form competition setup section.
 */
public class ApplicationFormForm extends CompetitionSetupForm {
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
