package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotNull;

public class KnowledgeBaseForm {

    @NotNull
    private String knowledgeBase;

    public String getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(String knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
}