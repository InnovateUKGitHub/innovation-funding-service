package org.innovateuk.ifs.questionnaire.response.domain;

import org.hibernate.annotations.GenericGenerator;
import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class QuestionnaireResponse extends AuditableEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireId", referencedColumnName="id")
    private Questionnaire questionnaire;

    @OneToMany(mappedBy = "questionnaireResponse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionnaireQuestionResponse> questionnaireQuestionResponse;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
