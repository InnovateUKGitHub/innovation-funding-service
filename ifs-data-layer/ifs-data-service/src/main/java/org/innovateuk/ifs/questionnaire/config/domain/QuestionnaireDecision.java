package org.innovateuk.ifs.questionnaire.config.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class QuestionnaireDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionnaireQuestion> questions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<QuestionnaireQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionnaireQuestion> questions) {
        this.questions = questions;
    }
}
