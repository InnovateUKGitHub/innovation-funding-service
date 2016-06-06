package com.worth.ifs.controller.form;

import com.worth.ifs.user.resource.UserResource;


public class CompSetupInitialDetailsForm {
    private long executiveUserId;

    private int openingDateDay;
    private int openingDateMonth;
    private int openingDateYear;

    private String title;

    private String innovationSector;
    private String innovationArea;
    private String compType;

    private long LeadTechnologistUserId;

    private long pafNumber;
    private String competitionCode;
    private String budgetCode;

    public long getExecutiveUserId() {
        return executiveUserId;
    }

    public void setExecutiveUserId(long executiveUserId) {
        this.executiveUserId = executiveUserId;
    }

    public int getOpeningDateDay() {
        return openingDateDay;
    }

    public void setOpeningDateDay(int openingDateDay) {
        this.openingDateDay = openingDateDay;
    }

    public int getOpeningDateMonth() {
        return openingDateMonth;
    }

    public void setOpeningDateMonth(int openingDateMonth) {
        this.openingDateMonth = openingDateMonth;
    }

    public int getOpeningDateYear() {
        return openingDateYear;
    }

    public void setOpeningDateYear(int openingDateYear) {
        this.openingDateYear = openingDateYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public void setInnovationSector(String innovationSector) {
        this.innovationSector = innovationSector;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(String innovationArea) {
        this.innovationArea = innovationArea;
    }

    public String getCompType() {
        return compType;
    }

    public void setCompType(String compType) {
        this.compType = compType;
    }

    public long getLeadTechnologistUserId() {
        return LeadTechnologistUserId;
    }

    public void setLeadTechnologistUserId(long leadTechnologistUserId) {
        LeadTechnologistUserId = leadTechnologistUserId;
    }

    public long getPafNumber() {
        return pafNumber;
    }

    public void setPafNumber(long pafNumber) {
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
