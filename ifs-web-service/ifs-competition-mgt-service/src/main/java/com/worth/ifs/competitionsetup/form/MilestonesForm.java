package com.worth.ifs.competitionsetup.form;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Form for the milestones competition setup section.
 */
public class MilestonesForm extends CompetitionSetupForm {

    @NotNull(message="Please enter the date")
    private LocalDate openDate;
    @NotNull(message="Please enter the date")
    private LocalDate briefingEvent;
    @NotNull(message="Please enter the date")
    private LocalDate submissionDate;
    @NotNull(message="Please enter the date")
    private LocalDate allocateAssessors;
    @NotNull(message="Please enter the date")
    private LocalDate assessorBriefing;
    @NotNull(message="Please enter the date")
    private LocalDate assessorAccepts;
    @NotNull(message="Please enter the date")
    private LocalDate assessorDeadline;
    @NotNull(message="Please enter the date")
    private LocalDate lineDraw;
    @NotNull(message="Please enter the date")
    private LocalDate assessmentPanel;
    @NotNull(message="Please enter the date")
    private LocalDate panelDate;
    @NotNull(message="Please enter the date")
    private LocalDate fundersPanel;
    @NotNull(message="Please enter the date")
    private LocalDate notifications;
    @NotNull(message="Please enter the date")
    private LocalDate releaseFeedback;


    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getBriefingEvent() {
        return briefingEvent;
    }

    public void setBriefingEvent(LocalDate briefingEvent) {
        this.briefingEvent = briefingEvent;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDate getAllocateAssessors() {
        return allocateAssessors;
    }

    public void setAllocateAssessors(LocalDate allocateAssessors) {
        this.allocateAssessors = allocateAssessors;
    }

    public LocalDate getAssessorBriefing() {
        return assessorBriefing;
    }

    public void setAssessorBriefing(LocalDate assessorBriefing) {
        this.assessorBriefing = assessorBriefing;
    }

    public LocalDate getAssessorAccepts() {
        return assessorAccepts;
    }

    public void setAssessorAccepts(LocalDate assessorAccepts) {
        this.assessorAccepts = assessorAccepts;
    }

    public LocalDate getLineDraw() {
        return lineDraw;
    }

    public void setLineDraw(LocalDate lineDraw) {
        this.lineDraw = lineDraw;
    }

    public LocalDate getAssessmentPanel() {
        return assessmentPanel;
    }

    public void setAssessmentPanel(LocalDate assessmentPanel) {
        this.assessmentPanel = assessmentPanel;
    }

    public LocalDate getFundersPanel() {
        return fundersPanel;
    }

    public void setFundersPanel(LocalDate fundersPanel) {
        this.fundersPanel = fundersPanel;
    }

    public LocalDate getNotifications() {
        return notifications;
    }

    public void setNotifications(LocalDate notifications) {
        this.notifications = notifications;
    }

    public LocalDate getReleaseFeedback() {
        return releaseFeedback;
    }

    public void setReleaseFeedback(LocalDate releaseFeedback) {
        this.releaseFeedback = releaseFeedback;
    }

    public LocalDate getAssessorDeadline() {
        return assessorDeadline;
    }

    public void setAssessorDeadline(LocalDate assessorDeadline) {
        this.assessorDeadline = assessorDeadline;
    }

    public LocalDate getPanelDate() {
        return panelDate;
    }

    public void setPanelDate(LocalDate panelDate) {
        this.panelDate = panelDate;
    }
}
