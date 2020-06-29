package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource.MultipleChoiceValidationGroup;

import javax.validation.constraints.NotBlank;

public class MultipleChoiceOptionResource {

    private Long id;
    @NotBlank(groups = MultipleChoiceValidationGroup.class)
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
