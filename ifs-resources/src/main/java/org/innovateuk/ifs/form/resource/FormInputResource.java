package org.innovateuk.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.cache.CacheableWhenCompetitionOpen;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileTypeCategory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FormInputResource implements CacheableWhenCompetitionOpen {
    private Long id;
    private Integer wordCount;
    private FormInputType type;
    private Long question;
    private Set<Long> inputValidators;
    private String description;
    private Boolean includedInApplicationSummary = false;
    private String guidanceTitle;
    private String guidanceAnswer;
    private List<GuidanceRowResource> guidanceRows;
    private List<MultipleChoiceOptionResource> multipleChoiceOptions = new ArrayList<>();
    private Integer priority;
    private FormInputScope scope;
    private Set<FileTypeCategory> allowedFileTypes = new LinkedHashSet<>();
    private FileEntryResource file;
    //Used by @Cacheable
    @JsonIgnore
    private boolean competitionOpen;

    public FormInputResource() {
        inputValidators = new LinkedHashSet<>();
    }

    public Long getId() {
        return id;
    }

    public Integer getWordCount() {
        return wordCount != null ? wordCount : 0;
    }

    public FormInputType getType() {
        return type;
    }

    public Boolean isIncludedInApplicationSummary() {
        return includedInApplicationSummary;
    }

    public String getDescription() {
        return description;
    }

    public Set<Long> getFormValidators() {
        return inputValidators;
    }

    public void setFormValidators(Set<Long> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void addFormValidator(Long inputValidator) {
        this.inputValidators.add(inputValidator);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public void setType(FormInputType type) {
        this.type = type;
    }

    public Set<Long> getInputValidators() {
        return this.inputValidators;
    }

    public void setInputValidators(Set<Long> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIncludedInApplicationSummary() {
        return this.includedInApplicationSummary;
    }

    public void setIncludedInApplicationSummary(Boolean includedInApplicationSummary) {
        this.includedInApplicationSummary = includedInApplicationSummary;
    }

    public Long getQuestion() {
        return question;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public String getGuidanceTitle() {
        return guidanceTitle;
    }

    public void setGuidanceTitle(final String guidanceTitle) {
        this.guidanceTitle = guidanceTitle;
    }

    public String getGuidanceAnswer() {
        return guidanceAnswer;
    }

    public void setGuidanceAnswer(final String guidanceAnswer) {
        this.guidanceAnswer = guidanceAnswer;
    }

    public List<GuidanceRowResource> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowResource> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public FormInputScope getScope() {
        return scope;
    }

    public void setScope(FormInputScope scope) {
        this.scope = scope;
    }

    public Set<FileTypeCategory> getAllowedFileTypes() {
        return this.allowedFileTypes;
    }

    public void setAllowedFileTypes(Set<FileTypeCategory> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public FileEntryResource getFile() {
        return file;
    }

    public void setFile(FileEntryResource file) {
        this.file = file;
    }

    public List<MultipleChoiceOptionResource> getMultipleChoiceOptions() {
        return multipleChoiceOptions;
    }

    public void setMultipleChoiceOptions(List<MultipleChoiceOptionResource> multipleChoiceOptions) {
        this.multipleChoiceOptions = multipleChoiceOptions;
    }

    @Override
    public boolean isCompetitionOpen() {
        return competitionOpen;
    }

    public void setCompetitionOpen(boolean competitionOpen) {
        this.competitionOpen = competitionOpen;
    }
}
