package org.innovateuk.ifs.questionnaire.response.domain;

import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;

import javax.persistence.*;

@Entity
public class QuestionnaireResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireId", referencedColumnName="id")
    private Questionnaire questionnaire;

    @OneToMany(mappedBy = "questionnaireResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private QuestionnaireQuestionResponse questionnaireQuestionResponse;

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public QuestionnaireQuestionResponse getQuestionnaireQuestionResponse() {
        return questionnaireQuestionResponse;
    }

    public void setQuestionnaireQuestionResponse(QuestionnaireQuestionResponse questionnaireQuestionResponse) {
        this.questionnaireQuestionResponse = questionnaireQuestionResponse;
    }
}
