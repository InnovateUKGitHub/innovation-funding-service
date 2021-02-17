package org.innovateuk.ifs.questionnaire.config.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionnaireQuestion> questions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private QuestionnaireSecurityType securityType;

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

    public QuestionnaireSecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(QuestionnaireSecurityType securityType) {
        this.securityType = securityType;
    }
}
