package org.innovateuk.ifs.questionnaire.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;

import java.util.List;

public class QuestionnaireQuestionViewModel {

    private long questionnaireId;
    private long questionnaireQuestionId;
    private String title;
    private String question;
    private String guidance;
    private List<Pair<Long, String>> options;

    //todo previous questions.


    public QuestionnaireQuestionViewModel(long questionnaireId, QuestionnaireQuestionResource question, List<QuestionnaireOptionResource> options) {
        this.questionnaireId = questionnaireId;
        this.questionnaireQuestionId = question.getId();
        
    }
}
