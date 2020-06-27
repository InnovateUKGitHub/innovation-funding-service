package org.innovateuk.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;

/**
 * FormInput represents an Input field and associated value on a Form (e.g. an Application Form, a piece of Recommendation Feedback etc).
 * <p>
 * A single FormInput would represent an input field under, for example, an Application Form Question, and will have one
 * or more FOrmInputResponses for that input field (so that more than one parties can respond to the same FormInput in,
 * for example, collaborative Application Forms
 */
@Entity
public class FormInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5000)
    private Integer wordCount;

    @Column(name = "form_input_type_id")
    private FormInputType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    private Competition competition;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "form_input_validator",
            joinColumns = {@JoinColumn(name = "form_input_id")},
            inverseJoinColumns = {@JoinColumn(name = "form_validator_id")})
    private Set<FormValidator> inputValidators;

    @Column(length=5000)
    private String guidanceTitle;

    @Column(length=5000)
    private String guidanceAnswer;

    private String description;

    private Boolean includedInApplicationSummary = false;

    @NotNull
    private Integer priority;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FormInputScope scope;

    @OneToMany(mappedBy = "formInput", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("priority")
    private List<GuidanceRow> guidanceRows;

    private boolean active = true;

    @OneToMany(mappedBy = "formInput", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MultipleChoiceOption> multipleChoiceOptions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "appendix_file_types")
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Set<FileTypeCategory> allowedFileTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_entry_id", referencedColumnName = "id")
    private FileEntry file;

    public FormInput() {
        inputValidators = new LinkedHashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWordCount() {
        return wordCount != null ? wordCount : 0;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public FormInputType getType() {
        return type;
    }

    public void setType(FormInputType type) {
        this.type = type;
    }

    public Boolean isIncludedInApplicationSummary() {
        return includedInApplicationSummary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<FormValidator> getFormValidators() {
        return inputValidators;
    }

    public void setFormValidators(Set<FormValidator> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public void addFormValidator(FormValidator inputValidator) {
        this.inputValidators.add(inputValidator);
    }

    @JsonIgnore
    public Competition getCompetition() {
        return this.competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Set<FormValidator> getInputValidators() {
        return this.inputValidators;
    }

    public void setInputValidators(Set<FormValidator> inputValidators) {
        this.inputValidators = inputValidators;
    }

    public Boolean getIncludedInApplicationSummary() {
        return this.includedInApplicationSummary;
    }

    public void setIncludedInApplicationSummary(Boolean includedInApplicationSummary) {
        this.includedInApplicationSummary = includedInApplicationSummary;
    }

    @JsonIgnore
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
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

    public List<GuidanceRow> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRow> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<FileTypeCategory> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(Set<FileTypeCategory> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public FileEntry getFile() {
        return file;
    }

    public void setFile(FileEntry file) {
        this.file = file;
    }

    public List<MultipleChoiceOption> getMultipleChoiceOptions() {
        return multipleChoiceOptions;
    }

    public void setMultipleChoiceOptions(List<MultipleChoiceOption> multipleChoiceOptions) {
        this.multipleChoiceOptions = multipleChoiceOptions;
    }

    public boolean isCompetitionOpen() {
        return competition.getCompetitionStatus().isLaterThan(READY_TO_OPEN);
    }
}
