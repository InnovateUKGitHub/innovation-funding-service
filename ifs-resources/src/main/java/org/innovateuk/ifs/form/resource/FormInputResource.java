package org.innovateuk.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.file.resource.FileTypeCategories;

import java.io.IOException;
import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

public class FormInputResource {
    private Long id;
    private Integer wordCount;
    private FormInputType type;
    private Long question;
    private Long competition;
    private Set<Long> inputValidators;
    private String description;
    private Boolean includedInApplicationSummary = false;
    private String guidanceTitle;
    private String guidanceAnswer;
    private List<GuidanceRowResource> guidanceRows;
    private Integer priority;
    private FormInputScope scope;
    private Set<FileTypeCategories> allowedFileTypes = new LinkedHashSet<>();

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

    public Long getCompetition() {
        return this.competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
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

    /**
     * TODO: IFS-2564 - Remove in ZDD migrate.
     */
    public String getAllowedFileTypes() {
        return simpleJoiner(allowedFileTypes, ",");
    }

    /**
     * TODO: IFS-2564 - Rename and remove JsonIgnore in ZDD migrate.
     */
    @JsonIgnore
    public Set<FileTypeCategories> getAllowedFileTypesSet() {
        return this.allowedFileTypes;
    }

    /**
     * TODO: IFS-2564 - Remove deserializer in ZDD contract.
     */
    @JsonDeserialize(using = AllowedFileTypesDeserializer.class)
    public void setAllowedFileTypes(Set<FileTypeCategories> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    /**
     * Custom deserializer for the 'allowedFileTypes' property.
     *
     * This is required as this property may come through from the
     * data-tier as either an array or a string.
     * Ideally we would use overloading on setter methods,
     * but Jackson doesn't seem to deserialize correctly if we do this.
     *
     * TODO: IFS-2564 - Remove in ZDD contract.
     */
    static class AllowedFileTypesDeserializer extends JsonDeserializer<Set<FileTypeCategories>> {
        @Override
        public Set<FileTypeCategories> deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException
        {
            if (p.isExpectedStartArrayToken()) {
                Set<FileTypeCategories> result = newHashSet();

                while (p.nextToken() != JsonToken.END_ARRAY) {
                    result.add(FileTypeCategories.valueOf(p.getValueAsString()));
                }

                return result;
            }

            if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                String valueAsString = p.getValueAsString();

                if (valueAsString.isEmpty()) {
                    return emptySet();
                }

                return simpleMapSet(valueAsString.split(","), FileTypeCategories::valueOf);
            }

            throw ctxt.wrongTokenException(p, p.getCurrentToken(), "Token should be an Array or String.");
        }
    }
}
