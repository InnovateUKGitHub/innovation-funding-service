package com.worth.ifs.controller.form;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form class to pass and save for the first section.
 */
public class CompetitionSetupInitialDetailsForm extends CompetitionSetupForm {

    @NotEmpty(message = "Please select a competition executive")
    private Long executiveUserId;

    @NotEmpty(message = "Please enter a opening date day")
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a opening date day")
    @Size(max=2, message="Your opening date day cannot have more than 2 numbers")
    private Integer openingDateDay;

    @NotEmpty(message = "Please enter a opening date month")
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a opening date month")
    @Size(max=2, message="Your opening date month cannot have more than 2 numbers")
    private Integer openingDateMonth;

    @NotEmpty(message = "Please enter a opening date year")
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a opening date year")
    @Size.List ({
            @Size(min=4, message="Your opening date year should have at least 4 characters"),
            @Size(max=4, message="Your opening date year cannot have more than 4 characters"),
    })
    private Integer openingDateYear;

    private String title;

    @NotEmpty(message = "Please select a innovation sector")
    private Long innovationSectorCategoryId;

    @NotEmpty(message = "Please select a innovation area")
    private Long innovationAreaCategoryId;

    @NotEmpty(message = "Please select a competition type")
    private Long competitionTypeId;

    @NotEmpty(message = "Please select a lead technologist")
    private Long LeadTechnologistUserId;

    private Long pafNumber;
    private String competitionCode;
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

    public Long getPafNumber() {
        return pafNumber;
    }

    public void setPafNumber(Long pafNumber) {
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
