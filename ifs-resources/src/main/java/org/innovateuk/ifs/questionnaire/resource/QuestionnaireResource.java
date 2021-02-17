package org.innovateuk.ifs.questionnaire.resource;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireResource {

    private Long id;

    private QuestionnaireSecurityType securityType;

    private List<Long> questions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionnaireSecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(QuestionnaireSecurityType securityType) {
        this.securityType = securityType;
    }

    public List<Long> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Long> questions) {
        this.questions = questions;
    }
}
