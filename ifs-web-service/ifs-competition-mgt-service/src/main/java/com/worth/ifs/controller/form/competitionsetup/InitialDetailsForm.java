package com.worth.ifs.controller.form.competitionsetup;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Form for the initial details competition setup section.
 */
public class InitialDetailsForm extends CompetitionSetupForm {

    @NotNull(message = "Please select a competition executive")
    private Long executiveUserId;

    @NotNull(message = "Please enter a opening day")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer openingDateDay;

    @NotNull(message = "Please enter a opening month")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer openingDateMonth;

    @NotNull(message = "Please enter a opening year")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer openingDateYear;

    @NotEmpty(message = "Please enter a title")
    private String title;

    @NotNull(message = "Please select a innovation sector")
    private Long innovationSectorCategoryId;

    @NotNull(message = "Please select a innovation area")
    private Long innovationAreaCategoryId;

    @NotNull(message = "Please select a competition type")
    private Long competitionTypeId;

    @NotNull(message = "Please select a lead technologist")
    private Long LeadTechnologistUserId;

    @NotEmpty(message = "Please enter a PAF number")
    private String pafNumber;
    @NotEmpty(message = "Please generate a competition code")
    private String competitionCode;
    @NotEmpty(message = "Please enter a budget code")
    private String budgetCode;

    public Long getExecutiveUserId() {
        return executiveUserId;
    }

    public void setExecutiveUserId(Long executiveUserId) {
        this.executiveUserId = executiveUserId;
    }

    public Integer getOpeningDateDay() {
        return openingDateDay;
    }

    public void setOpeningDateDay(Integer openingDateDay) {
        this.openingDateDay = openingDateDay;
    }

    public Integer getOpeningDateMonth() {
        return openingDateMonth;
    }

    public void setOpeningDateMonth(Integer openingDateMonth) {
        this.openingDateMonth = openingDateMonth;
    }

    public Integer getOpeningDateYear() {
        return openingDateYear;
    }

    public void setOpeningDateYear(Integer openingDateYear) {
        this.openingDateYear = openingDateYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getInnovationSectorCategoryId() {
        return innovationSectorCategoryId;
    }

    public void setInnovationSectorCategoryId(Long innovationSectorCategoryId) {
        this.innovationSectorCategoryId = innovationSectorCategoryId;
    }

    public Long getInnovationAreaCategoryId() {
        return innovationAreaCategoryId;
    }

    public void setInnovationAreaCategoryId(Long innovationAreaCategoryId) {
        this.innovationAreaCategoryId = innovationAreaCategoryId;
    }

    public Long getCompetitionTypeId() {
        return competitionTypeId;
    }

    public void setCompetitionTypeId(Long competitionTypeId) {
        this.competitionTypeId = competitionTypeId;
    }

    public Long getLeadTechnologistUserId() {
        return LeadTechnologistUserId;
    }

    public void setLeadTechnologistUserId(Long leadTechnologistUserId) {
        LeadTechnologistUserId = leadTechnologistUserId;
    }

    public String getPafNumber() {
        return pafNumber;
    }

    public void setPafNumber(String pafNumber) {
        this.pafNumber = pafNumber;
    }

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public String getBudgetCode() {
        return budgetCode;
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
    }
}
