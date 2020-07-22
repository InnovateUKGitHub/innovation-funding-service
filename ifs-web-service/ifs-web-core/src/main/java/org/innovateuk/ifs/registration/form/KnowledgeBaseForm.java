package org.innovateuk.ifs.registration.form;

import javax.validation.constraints.NotBlank;

public class KnowledgeBaseForm {

    @NotBlank(message = "{validation.standard.organisation.required}")
    private String knowledgeBase;

    public String getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(String knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
}