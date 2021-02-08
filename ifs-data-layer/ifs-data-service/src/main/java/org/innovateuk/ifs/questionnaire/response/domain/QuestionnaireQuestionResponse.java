package org.innovateuk.ifs.questionnaire.response.domain;

import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireOption;

import javax.persistence.*;

public class QuestionnaireQuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireResponseId", referencedColumnName="id")
    private QuestionnaireResponse questionnaireResponse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireOptionId", referencedColumnName="id")
    private QuestionnaireOption option;

    public QuestionnaireResponse getQuestionnaireResponse() {
        return questionnaireResponse;
    }

    public void setQuestionnaireResponse(QuestionnaireResponse questionnaireResponse) {
        this.questionnaireResponse = questionnaireResponse;
    }

    public QuestionnaireOption getOption() {
        return option;
    }

    public void setOption(QuestionnaireOption option) {
        this.option = option;
    }
}
