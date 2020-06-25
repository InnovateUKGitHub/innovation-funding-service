package org.innovateuk.ifs.form.resource;

import java.io.Serializable;

public class MultipleChoiceOptionResource implements Serializable {

    private Long id;
    private String text;

    public MultipleChoiceOptionResource() {
    }

    public MultipleChoiceOptionResource(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
