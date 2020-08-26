package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.*;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FormInputBuilder {
    private Integer wordCount;
    private FormInputType type;
    private Question question;
    private Competition competition;
    private Set<FormValidator> inputValidators;
    private String guidanceTitle;
    private String guidanceAnswer;
    private String description;
    private Integer priority;
    private FormInputScope scope;
    private List<GuidanceRowBuilder> guidanceRows = new ArrayList<>();
    private boolean active = true;
    private List<MultipleChoiceOptionBuilder> multipleChoiceOptions = new ArrayList<>();
    private Set<FileTypeCategory> allowedFileTypes;
    private FileEntry file;

    private FormInputBuilder() {
    }

    public static FormInputBuilder aFormInput() {
        return new FormInputBuilder();
    }

    public FormInputBuilder withWordCount(Integer wordCount) {
        this.wordCount = wordCount;
        return this;
    }

    public FormInputBuilder withType(FormInputType type) {
        this.type = type;
        return this;
    }

    public FormInputBuilder withQuestion(Question question) {
        this.question = question;
        return this;
    }

    public FormInputBuilder withCompetition(Competition competition) {
        this.competition = competition;
        return this;
    }

    public FormInputBuilder withInputValidators(Set<FormValidator> inputValidators) {
        this.inputValidators = inputValidators;
        return this;
    }

    public FormInputBuilder withGuidanceTitle(String guidanceTitle) {
        this.guidanceTitle = guidanceTitle;
        return this;
    }

    public FormInputBuilder withGuidanceAnswer(String guidanceAnswer) {
        this.guidanceAnswer = guidanceAnswer;
        return this;
    }

    public FormInputBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public FormInputBuilder withPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public FormInputBuilder withScope(FormInputScope scope) {
        this.scope = scope;
        return this;
    }

    public FormInputBuilder withGuidanceRows(List<GuidanceRowBuilder> guidanceRows) {
        this.guidanceRows = guidanceRows;
        return this;
    }

    public FormInputBuilder withActive(boolean active) {
        this.active = active;
        return this;
    }

    public FormInputBuilder withMultipleChoiceOptions(List<MultipleChoiceOptionBuilder> multipleChoiceOptions) {
        this.multipleChoiceOptions = multipleChoiceOptions;
        return this;
    }

    public FormInputBuilder withAllowedFileTypes(Set<FileTypeCategory> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
        return this;
    }

    public FormInputBuilder withFile(FileEntry file) {
        this.file = file;
        return this;
    }

    public FormInput build() {
        FormInput formInput = new FormInput();
        formInput.setWordCount(wordCount);
        formInput.setType(type);
        formInput.setQuestion(question);
        formInput.setCompetition(competition);
        formInput.setInputValidators(inputValidators);
        formInput.setGuidanceTitle(guidanceTitle);
        formInput.setGuidanceAnswer(guidanceAnswer);
        formInput.setDescription(description);
        formInput.setPriority(priority);
        formInput.setScope(scope);
        formInput.setGuidanceRows(guidanceRows.stream().map(GuidanceRowBuilder::build).collect(Collectors.toList()));
        formInput.setActive(active);
        formInput.setMultipleChoiceOptions(multipleChoiceOptions.stream().map(MultipleChoiceOptionBuilder::build).collect(Collectors.toList()));
        formInput.setAllowedFileTypes(allowedFileTypes);
        formInput.setFile(file);
        return formInput;
    }
}
