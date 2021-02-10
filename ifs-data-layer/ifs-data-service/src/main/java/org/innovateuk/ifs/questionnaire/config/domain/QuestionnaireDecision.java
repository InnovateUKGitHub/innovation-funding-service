package org.innovateuk.ifs.questionnaire.config.domain;

import org.innovateuk.ifs.questionnaire.resource.DecisionType;

import javax.persistence.*;

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class QuestionnaireDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract DecisionType getDecisionType();

}
