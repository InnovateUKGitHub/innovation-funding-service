package org.innovateuk.ifs.questionnaire.response.domain;

import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;

import javax.persistence.*;
import java.util.List;

@Entity
public class QuestionnaireResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireId", referencedColumnName="id")
    private Questionnaire questionnaire;

    @OneToMany(mappedBy = "questionnaireResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionnaireQuestionResponse> questionnaireQuestionResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<QuestionnaireQuestionResponse> getQuestionnaireQuestionResponse() {
        return questionnaireQuestionResponse;
    }

    public void setQuestionnaireQuestionResponse(List<QuestionnaireQuestionResponse> questionnaireQuestionResponse) {
        this.questionnaireQuestionResponse = questionnaireQuestionResponse;
    }
}
