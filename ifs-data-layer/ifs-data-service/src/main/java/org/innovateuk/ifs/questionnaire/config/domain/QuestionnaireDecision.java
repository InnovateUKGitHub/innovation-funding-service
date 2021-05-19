package org.innovateuk.ifs.questionnaire.config.domain;

import org.innovateuk.ifs.questionnaire.resource.DecisionType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class QuestionnaireDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "decision")
    private List<QuestionnaireOption> optionsLinkedToThisDecision = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<QuestionnaireOption> getOptionsLinkedToThisDecision() {
        return optionsLinkedToThisDecision;
    }

    public void setOptionsLinkedToThisDecision(List<QuestionnaireOption> optionsLinkedToThisDecision) {
        this.optionsLinkedToThisDecision = optionsLinkedToThisDecision;
    }

    public abstract DecisionType getDecisionType();
}
