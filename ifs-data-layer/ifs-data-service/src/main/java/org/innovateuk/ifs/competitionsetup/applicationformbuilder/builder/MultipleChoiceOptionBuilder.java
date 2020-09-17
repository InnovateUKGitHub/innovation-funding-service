package org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder;

import org.innovateuk.ifs.form.domain.MultipleChoiceOption;

public final class MultipleChoiceOptionBuilder {
    private String text;

    private MultipleChoiceOptionBuilder() {
    }

    public static MultipleChoiceOptionBuilder aMultipleChoiceOption() {
        return new MultipleChoiceOptionBuilder();
    }

    public MultipleChoiceOptionBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public MultipleChoiceOption build() {
        MultipleChoiceOption multipleChoiceOption = new MultipleChoiceOption();
        multipleChoiceOption.setText(text);
        return multipleChoiceOption;
    }
}
