package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.file.resource.FileTypeCategories;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.file.resource.FileTypeCategories.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategories.SPREADSHEET;

@FieldRequiredIf(required = "assessmentGuidanceTitle", argument = "writtenFeedback", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "assessmentMaxWords", argument = "writtenFeedback", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "scoreTotal", argument = "scored", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "allowedFileTypes", argument = "appendix", predicate = true, message = "{validation.field.must.not.be.blank}")
@FieldRequiredIf(required = "fileUploadGuidance", argument = "appendix", predicate = true, message = "{validation.field.must.not.be.blank}")
public class CompetitionSetupQuestionResource {
    /* This exists for ZDD purposes. We need a way to recognize on the data-service
     * to see if we need to save the new fileUploadGuidance as it will be sent as null
     * by the old ifs-competition-mgt-service. This should be removed as part of IFS-xxxx.
     */
    private boolean updated;

    private Long questionId;

    private CompetitionSetupQuestionType type;

    private String number;
    @NotBlank
    private String shortTitle;
    private Boolean shortTitleEditable;
    @NotBlank
    private String title;
    private String subTitle;

    @NotBlank
    private String guidanceTitle;

    @NotBlank
    private String guidance;

    @Min(value = 1, message = "{validation.applicationquestionform.maxwords.min}")
    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Integer maxWords;

    private Boolean appendix;
    private List<String> allowedFileTypes;
    private String fileUploadGuidance;

    private String assessmentGuidanceTitle;
    private String assessmentGuidance;
    @Min(value = 1, message = "{validation.applicationquestionform.maxwords.min}")
    private Integer assessmentMaxWords;

    private Boolean scored;
    private Integer scoreTotal;
    private Boolean writtenFeedback;

    @Valid
    private List<GuidanceRowResource> guidanceRows = new ArrayList<>();

    private Boolean researchCategoryQuestion;
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

    public Boolean getShortTitleEditable() {
        return shortTitleEditable;
    }

    public void setShortTitleEditable(Boolean shortTitleEditable) {
        this.shortTitleEditable = shortTitleEditable;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public CompetitionSetupQuestionType getType() {
        return type;
    }

    public void setType(CompetitionSetupQuestionType type) {
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

    public List<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(List<String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public static List<FileTypeCategories> getSupportedTypeCategories(){
        return asList(PDF, SPREADSHEET);
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
                .append(shortTitleEditable, that.shortTitleEditable)
                .append(title, that.title)
                .append(subTitle, that.subTitle)
                .append(guidanceTitle, that.guidanceTitle)
                .append(guidance, that.guidance)
                .append(maxWords, that.maxWords)
                .append(appendix, that.appendix)
                .append(assessmentGuidanceTitle, that.assessmentGuidanceTitle)
                .append(assessmentGuidance, that.assessmentGuidance)
                .append(assessmentMaxWords, that.assessmentMaxWords)
                .append(scored, that.scored)
                .append(scoreTotal, that.scoreTotal)
                .append(writtenFeedback, that.writtenFeedback)
                .append(guidanceRows, that.guidanceRows)
                .append(researchCategoryQuestion, that.researchCategoryQuestion)
                .append(scope, that.scope)
                .append(allowedFileTypes, that.allowedFileTypes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(questionId)
                .append(type)
                .append(number)
                .append(shortTitle)
                .append(shortTitleEditable)
                .append(title)
                .append(subTitle)
                .append(guidanceTitle)
                .append(guidance)
                .append(maxWords)
                .append(appendix)
                .append(assessmentGuidanceTitle)
                .append(assessmentGuidance)
                .append(assessmentMaxWords)
                .append(scored)
                .append(scoreTotal)
                .append(writtenFeedback)
                .append(guidanceRows)
                .append(researchCategoryQuestion)
                .append(scope)
                .append(allowedFileTypes)
                .toHashCode();
    }

    public String getFileUploadGuidance() {
        return fileUploadGuidance;
    }

    public void setFileUploadGuidance(String fileUploadGuidance) {
        this.fileUploadGuidance = fileUploadGuidance;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }
}
