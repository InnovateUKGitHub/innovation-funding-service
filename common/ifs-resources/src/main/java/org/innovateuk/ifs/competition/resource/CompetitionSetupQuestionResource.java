package org.innovateuk.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;

@FieldRequiredIf(required = "assessmentGuidanceTitle", argument = "writtenFeedback", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "assessmentGuidance", argument = "writtenFeedback", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "assessmentMaxWords", argument = "writtenFeedback", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "scoreTotal", argument = "scored", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "allowedTemplateResponseFileTypes", argument = "templateDocument", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "templateTitle", argument = "templateDocument", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "guidance", argument="guidanceRequired", predicate=true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "subTitle2", argument="subTitle2Required", predicate=true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "guidanceTitle", argument="guidanceRequired", predicate=true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "shortTitle", argument="titleRequired", predicate=true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "title", argument="titleRequired", predicate=true, message = "{validation.field.must.not.be.blank}")
public class CompetitionSetupQuestionResource {
    public interface TextAreaValidationGroup { }
    public interface MultipleChoiceValidationGroup { }

    private Long questionId;

    private QuestionSetupType type;

    private String number;

    private String shortTitle;

    private String title;

    private String subTitle;

    private String guidanceTitle;

    private String guidance;
    private String subTitle2;

    /* text area */
    private Boolean textArea;

    @Min(value = 1, message = "{validation.applicationquestionform.maxwords.min}", groups = TextAreaValidationGroup.class)
    @NotNull(message = "{validation.field.must.not.be.blank}", groups = TextAreaValidationGroup.class)
    private Integer maxWords;

    /* multiple choice */
    private Boolean multipleChoice;
    @Valid
    @Size(min = 2, max = 30, groups = MultipleChoiceValidationGroup.class)
    private List<MultipleChoiceOptionResource> choices = new ArrayList<>();

    /* Expression of interest */
    private Boolean expressionOfInterest;

    /* appendix */
    private Boolean appendix;
    private Integer numberOfUploads;
    private Set<FileTypeCategory> allowedAppendixResponseFileTypes = new LinkedHashSet<>();
    private String appendixGuidance;

    /* template document */
    private Boolean templateDocument;
    private Set<FileTypeCategory> allowedTemplateResponseFileTypes = new LinkedHashSet<>();
    private String templateTitle;
    private String templateFilename;
    private Long templateFormInput;

    /* assessment */
    private Boolean writtenFeedback;
    private String assessmentGuidanceTitle;
    private String assessmentGuidance;
    @Min(value = 1, message = "{validation.applicationquestionform.maxwords.min}")
    private Integer assessmentMaxWords;

    /* score */
    private Boolean scored;
    private Integer scoreTotal;
    @Valid
    private List<GuidanceRowResource> guidanceRows = new ArrayList<>();

    /* research cat */
    private Boolean researchCategoryQuestion;

    /* scope */
    private Boolean scope;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getGuidanceTitle() {
        return guidanceTitle;
    }

    public void setGuidanceTitle(String guidanceTitle) {
        this.guidanceTitle = guidanceTitle;
    }

    public String getSubTitle2() {
        return subTitle2;
    }

    public void setSubTitle2(String subTitle2) {
        this.subTitle2 = subTitle2;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public Integer getMaxWords() {
        return maxWords;
    }

    public void setMaxWords(Integer maxWords) {
        this.maxWords = maxWords;
    }

    public Boolean getAppendix() {
        return appendix;
    }

    public void setAppendix(Boolean appendix) {
        this.appendix = appendix;
    }

    public Integer getNumberOfUploads() {
        return numberOfUploads;
    }

    public void setNumberOfUploads(Integer numberOfUploads) {
        this.numberOfUploads = numberOfUploads;
    }

    public String getAssessmentGuidance() {
        return assessmentGuidance;
    }

    public void setAssessmentGuidance(String assessmentGuidance) {
        this.assessmentGuidance = assessmentGuidance;
    }

    public Integer getAssessmentMaxWords() {
        return assessmentMaxWords;
    }

    public void setAssessmentMaxWords(Integer assessmentMaxWords) {
        this.assessmentMaxWords = assessmentMaxWords;
    }

    public Boolean getScored() {
        return scored;
    }

    public void setScored(Boolean scored) {
        this.scored = scored;
    }

    public Integer getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(Integer scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public Boolean getWrittenFeedback() {
        return writtenFeedback;
    }

    public void setWrittenFeedback(Boolean writtenFeedback) {
        this.writtenFeedback = writtenFeedback;
    }

    public List<GuidanceRowResource> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<GuidanceRowResource> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public QuestionSetupType getType() {
        return type;
    }

    public void setType(QuestionSetupType type) {
        this.type = type;
    }

    public Boolean getResearchCategoryQuestion() {
        return researchCategoryQuestion;
    }

    public void setResearchCategoryQuestion(Boolean researchCategoryQuestion) {
        this.researchCategoryQuestion = researchCategoryQuestion;
    }

    public Boolean getScope() {
        return scope;
    }

    public void setScope(Boolean scope) {
        this.scope = scope;
    }

    public String getAssessmentGuidanceTitle() {
        return assessmentGuidanceTitle;
    }

    public void setAssessmentGuidanceTitle(String assessmentGuidanceTitle) {
        this.assessmentGuidanceTitle = assessmentGuidanceTitle;
    }

    public Set<FileTypeCategory> getAllowedAppendixResponseFileTypes() {
        return allowedAppendixResponseFileTypes;
    }

    public void setAllowedAppendixResponseFileTypes(Set<FileTypeCategory> allowedAppendixResponseFileTypes) {
        this.allowedAppendixResponseFileTypes = allowedAppendixResponseFileTypes;
    }

    public String getAppendixGuidance() {
        return appendixGuidance;
    }

    public void setAppendixGuidance(String appendixGuidance) {
        this.appendixGuidance = appendixGuidance;
    }

    public Boolean getTemplateDocument() {
        return templateDocument;
    }

    public void setTemplateDocument(Boolean templateDocument) {
        this.templateDocument = templateDocument;
    }

    public Set<FileTypeCategory> getAllowedTemplateResponseFileTypes() {
        return allowedTemplateResponseFileTypes;
    }

    public void setAllowedTemplateResponseFileTypes(Set<FileTypeCategory> allowedTemplateResponseFileTypes) {
        this.allowedTemplateResponseFileTypes = allowedTemplateResponseFileTypes;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getTemplateFilename() {
        return templateFilename;
    }

    public void setTemplateFilename(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public Long getTemplateFormInput() {
        return templateFormInput;
    }

    public void setTemplateFormInput(Long templateFormInput) {
        this.templateFormInput = templateFormInput;
    }

    public static List<FileTypeCategory> getAppendixTypeCategories() {
        return asList(PDF, SPREADSHEET);
    }

    public static List<FileTypeCategory> getTemplateDocumentTypeCategories() {
        return asList(PDF, SPREADSHEET, DOCUMENT);
    }

    public List<MultipleChoiceOptionResource> getChoices() {
        return choices;
    }

    public void setChoices(List<MultipleChoiceOptionResource> choices) {
        this.choices = choices;
    }

    public Boolean getTextArea() {
        return textArea;
    }

    public void setTextArea(Boolean textArea) {
        this.textArea = textArea;
    }

    public Boolean getMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(Boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public Boolean getExpressionOfInterest() {
        return expressionOfInterest;
    }

    public void setExpressionOfInterest(Boolean expressionOfInterest) {
        this.expressionOfInterest = expressionOfInterest;
    }

    @JsonIgnore
    public boolean isGuidanceRequired() {
        return QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION != type
                && QuestionSetupType.KTP_ASSESSMENT != type
                && QuestionSetupType.NORTHERN_IRELAND_DECLARATION != type
                && QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION != type;
    }

    @JsonIgnore
    public boolean isSubTitle2Required() {
        return QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION == type;
    }

    @JsonIgnore
    public boolean isTitleRequired() {
        return QuestionSetupType.KTP_ASSESSMENT != type;
    }

    @JsonIgnore
    public QuestionSetupType getKTPAssessmentQuestionType() {
        return QuestionSetupType.KTP_ASSESSMENT;
    }

    @JsonIgnore
    public QuestionSetupType getScopeQuestionType() {
        return QuestionSetupType.SCOPE;
    }

    @JsonIgnore
    public QuestionSetupType getAssessedQuestionType() {
        return QuestionSetupType.ASSESSED_QUESTION;
    }

    @JsonIgnore
    public QuestionSetupType getLoanQuestionType() {
        return QuestionSetupType.LOAN_BUSINESS_AND_FINANCIAL_INFORMATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CompetitionSetupQuestionResource that = (CompetitionSetupQuestionResource) o;

        return new EqualsBuilder()
                .append(questionId, that.questionId)
                .append(type, that.type)
                .append(number, that.number)
                .append(shortTitle, that.shortTitle)
                .append(title, that.title)
                .append(subTitle, that.subTitle)
                .append(guidanceTitle, that.guidanceTitle)
                .append(guidance, that.guidance)
                .append(subTitle2, that.subTitle2)
                .append(textArea, that.textArea)
                .append(maxWords, that.maxWords)
                .append(multipleChoice, that.multipleChoice)
                .append(choices, that.choices)
                .append(expressionOfInterest, that.expressionOfInterest)
                .append(appendix, that.appendix)
                .append(numberOfUploads, that.numberOfUploads)
                .append(allowedAppendixResponseFileTypes, that.allowedAppendixResponseFileTypes)
                .append(appendixGuidance, that.appendixGuidance)
                .append(templateDocument, that.templateDocument)
                .append(allowedTemplateResponseFileTypes, that.allowedTemplateResponseFileTypes)
                .append(templateTitle, that.templateTitle)
                .append(templateFilename, that.templateFilename)
                .append(templateFormInput, that.templateFormInput)
                .append(writtenFeedback, that.writtenFeedback)
                .append(assessmentGuidanceTitle, that.assessmentGuidanceTitle)
                .append(assessmentGuidance, that.assessmentGuidance)
                .append(assessmentMaxWords, that.assessmentMaxWords)
                .append(scored, that.scored)
                .append(scoreTotal, that.scoreTotal)
                .append(guidanceRows, that.guidanceRows)
                .append(researchCategoryQuestion, that.researchCategoryQuestion)
                .append(scope, that.scope)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(questionId)
                .append(type)
                .append(number)
                .append(shortTitle)
                .append(title)
                .append(subTitle)
                .append(guidanceTitle)
                .append(guidance)
                .append(subTitle2)
                .append(textArea)
                .append(maxWords)
                .append(multipleChoice)
                .append(choices)
                .append(expressionOfInterest)
                .append(appendix)
                .append(numberOfUploads)
                .append(allowedAppendixResponseFileTypes)
                .append(appendixGuidance)
                .append(templateDocument)
                .append(allowedTemplateResponseFileTypes)
                .append(templateTitle)
                .append(templateFilename)
                .append(templateFormInput)
                .append(writtenFeedback)
                .append(assessmentGuidanceTitle)
                .append(assessmentGuidance)
                .append(assessmentMaxWords)
                .append(scored)
                .append(scoreTotal)
                .append(guidanceRows)
                .append(researchCategoryQuestion)
                .append(scope)
                .toHashCode();
    }
}
