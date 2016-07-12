package com.worth.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Form for the milestones competition setup section.
 */
public class MilestonesForm extends CompetitionSetupForm {

    @NotNull(message="Please enter the open date")
    @Range(min=1, max=31, message= "Please enter a open day")
    private Integer openDateDay;
    @NotNull(message="Please enter the open date")
    @Range(min=1, max=12, message= "Please enter a open month")
    private Integer openDateMonth;
    @NotNull(message="Please enter the open date")
    @Range(min=1900, max=9000, message= "Please enter a open year")
    private Integer openDateYear;


    @NotNull(message="Please enter a briefing event")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer briefingEventDay;
    @NotNull(message="Please enter the briefing date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer briefingEventMonth;
    @NotNull(message="Please enter the briefing date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer briefingEventYear;

    @NotNull(message="Please enter the submission date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer submissionDateDay;
    @NotNull(message="Please enter the submission date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer submissionDateMonth;
    @NotNull(message="Please enter the submission date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer submissionDateYear;


    @NotNull(message="Please enter the alocate accessors date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer allocateAssessorsDay;
    @NotNull(message="Please enter the alocate accessors date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer allocateAssessorsMonth;
    @NotNull(message="Please enter the alocate accessors date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer allocateAssessorsYear;

    @NotNull(message="Please enter the assessor briefing date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer assessorBriefingDay;
    @NotNull(message="Please enter the assessor briefing date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer assessorBriefingMonth;
    @NotNull(message="Please enter the assessor briefing date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer assessorBriefingYear;

    @NotNull(message="Please enter the assessor accepts date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer assessorAcceptsDay;
    @NotNull(message="Please enter the assessor accepts date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer assessorAcceptsMonth;
    @NotNull(message="Please enter the assessor accepts date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer assessorAcceptsYear;

    @NotNull(message="Please enter the assessor deadline date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer assessorDeadlineDay;
    @NotNull(message="Please enter the assessor deadline date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer assessorDeadlineMonth;
    @NotNull(message="Please enter the assessor deadline date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer assessorDeadlineYear;


    @NotNull(message="Please enter the line draw date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer lineDrawDay;
    @NotNull(message="Please enter the line draw date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer lineDrawMonth;
    @NotNull(message="Please enter the line draw date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer lineDrawYear;

    @NotNull(message="Please enter the assesment panel date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer assessmentPanelDay;
    @NotNull(message="Please enter the assesment panel date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer assessmentPanelMonth;
    @NotNull(message="Please enter the assesment panel date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer assessmentPanelYear;

    @NotNull(message="Please enter the panel date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer panelDateDay;
    @NotNull(message="Please enter the panel date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer panelDateMonth;
    @NotNull(message="Please enter the panel date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer panelDateYear;

    @NotNull(message="Please enter the funders date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer fundersPanelDay;
    @NotNull(message="Please enter the funders date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer fundersPanelMonth;
    @NotNull(message="Please enter the funders date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer fundersPanelYear;

    @NotNull(message="Please enter the notifications date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer notificationsDay;
    @NotNull(message="Please enter the notifications date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer notificationsMonth;
    @NotNull(message="Please enter the notifications date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer notificationsYear;

    @NotNull(message="Please enter the release feedback date")
    @Range(min=1, max=31, message= "Please enter a opening day")
    private Integer releaseFeedbackDay;
    @NotNull(message="Please enter the release feedback date")
    @Range(min=1, max=12, message= "Please enter a opening month")
    private Integer releaseFeedbackMonth;
    @NotNull(message="Please enter the release feedback date")
    @Range(min=1900, max=9000, message= "Please enter a opening year")
    private Integer releaseFeedbackYear;


    public Integer getAssessorAcceptsMonth() {
        return assessorAcceptsMonth;
    }

    public void setAssessorAcceptsMonth(Integer assessorAcceptsMonth) {
        this.assessorAcceptsMonth = assessorAcceptsMonth;
    }

    public Integer getOpenDateDay() {
        return openDateDay;
    }

    public void setOpenDateDay(Integer openDateDay) {
        this.openDateDay = openDateDay;
    }

    public Integer getOpenDateMonth() {
        return openDateMonth;
    }

    public void setOpenDateMonth(Integer openDateMonth) {
        this.openDateMonth = openDateMonth;
    }

    public Integer getOpenDateYear() {
        return openDateYear;
    }

    public void setOpenDateYear(Integer openDateYear) {
        this.openDateYear = openDateYear;
    }

    public Integer getBriefingEventDay() {
        return briefingEventDay;
    }

    public void setBriefingEventDay(Integer briefingEventDay) {
        this.briefingEventDay = briefingEventDay;
    }

    public Integer getBriefingEventMonth() {
        return briefingEventMonth;
    }

    public void setBriefingEventMonth(Integer briefingEventMonth) {
        this.briefingEventMonth = briefingEventMonth;
    }

    public Integer getBriefingEventYear() {
        return briefingEventYear;
    }

    public void setBriefingEventYear(Integer briefingEventYear) {
        this.briefingEventYear = briefingEventYear;
    }

    public Integer getSubmissionDateDay() {
        return submissionDateDay;
    }

    public void setSubmissionDateDay(Integer submissionDateDay) {
        this.submissionDateDay = submissionDateDay;
    }

    public Integer getSubmissionDateMonth() {
        return submissionDateMonth;
    }

    public void setSubmissionDateMonth(Integer submissionDateMonth) {
        this.submissionDateMonth = submissionDateMonth;
    }

    public Integer getSubmissionDateYear() {
        return submissionDateYear;
    }

    public void setSubmissionDateYear(Integer submissionDateYear) {
        this.submissionDateYear = submissionDateYear;
    }

    public Integer getAllocateAssessorsDay() {
        return allocateAssessorsDay;
    }

    public void setAllocateAssessorsDay(Integer allocateAssessorsDay) {
        this.allocateAssessorsDay = allocateAssessorsDay;
    }

    public Integer getAllocateAssessorsMonth() {
        return allocateAssessorsMonth;
    }

    public void setAllocateAssessorsMonth(Integer allocateAssessorsMonth) {
        this.allocateAssessorsMonth = allocateAssessorsMonth;
    }

    public Integer getAllocateAssessorsYear() {
        return allocateAssessorsYear;
    }

    public void setAllocateAssessorsYear(Integer allocateAssessorsYear) {
        this.allocateAssessorsYear = allocateAssessorsYear;
    }

    public Integer getAssessorBriefingDay() {
        return assessorBriefingDay;
    }

    public void setAssessorBriefingDay(Integer assessorBriefingDay) {
        this.assessorBriefingDay = assessorBriefingDay;
    }

    public Integer getAssessorBriefingMonth() {
        return assessorBriefingMonth;
    }

    public void setAssessorBriefingMonth(Integer assessorBriefingMonth) {
        this.assessorBriefingMonth = assessorBriefingMonth;
    }

    public Integer getAssessorBriefingYear() {
        return assessorBriefingYear;
    }

    public void setAssessorBriefingYear(Integer assessorBriefingYear) {
        this.assessorBriefingYear = assessorBriefingYear;
    }

    public Integer getAssessorAcceptsDay() {
        return assessorAcceptsDay;
    }

    public void setAssessorAcceptsDay(Integer assessorAcceptDay) {
        this.assessorAcceptsDay = assessorAcceptDay;
    }

    public Integer getAssessorAcceptsYear() {
        return assessorAcceptsYear;
    }

    public void setAssessorAcceptsYear(Integer assessorAcceptsYear) {
        this.assessorAcceptsYear = assessorAcceptsYear;
    }

    public Integer getAssessorDeadlineDay() {
        return assessorDeadlineDay;
    }

    public void setAssessorDeadlineDay(Integer assessorDeadlineDay) {
        this.assessorDeadlineDay = assessorDeadlineDay;
    }

    public Integer getAssessorDeadlineMonth() {
        return assessorDeadlineMonth;
    }

    public void setAssessorDeadlineMonth(Integer assessorDeadlineMonth) {
        this.assessorDeadlineMonth = assessorDeadlineMonth;
    }

    public Integer getAssessorDeadlineYear() {
        return assessorDeadlineYear;
    }

    public void setAssessorDeadlineYear(Integer assessorDeadlineYear) {
        this.assessorDeadlineYear = assessorDeadlineYear;
    }

    public Integer getLineDrawDay() {
        return lineDrawDay;
    }

    public void setLineDrawDay(Integer lineDrawDay) {
        this.lineDrawDay = lineDrawDay;
    }

    public Integer getLineDrawMonth() {
        return lineDrawMonth;
    }

    public void setLineDrawMonth(Integer lineDrawMonth) {
        this.lineDrawMonth = lineDrawMonth;
    }

    public Integer getLineDrawYear() {
        return lineDrawYear;
    }

    public void setLineDrawYear(Integer lineDrawYear) {
        this.lineDrawYear = lineDrawYear;
    }

    public Integer getAssessmentPanelDay() {
        return assessmentPanelDay;
    }

    public void setAssessmentPanelDay(Integer assessmentPanelDay) {
        this.assessmentPanelDay = assessmentPanelDay;
    }

    public Integer getAssessmentPanelMonth() {
        return assessmentPanelMonth;
    }

    public void setAssessmentPanelMonth(Integer assessmentPanelMonth) {
        this.assessmentPanelMonth = assessmentPanelMonth;
    }

    public Integer getAssessmentPanelYear() {
        return assessmentPanelYear;
    }

    public void setAssessmentPanelYear(Integer assessmentPanelYear) {
        this.assessmentPanelYear = assessmentPanelYear;
    }

    public Integer getPanelDateDay() {
        return panelDateDay;
    }

    public void setPanelDateDay(Integer panelDateDay) {
        this.panelDateDay = panelDateDay;
    }

    public Integer getPanelDateMonth() {
        return panelDateMonth;
    }

    public void setPanelDateMonth(Integer panelDateMonth) {
        this.panelDateMonth = panelDateMonth;
    }

    public Integer getPanelDateYear() {
        return panelDateYear;
    }

    public void setPanelDateYear(Integer panelDateYear) {
        this.panelDateYear = panelDateYear;
    }

    public Integer getFundersPanelDay() {
        return fundersPanelDay;
    }

    public void setFundersPanelDay(Integer fundersPanelDay) {
        this.fundersPanelDay = fundersPanelDay;
    }

    public Integer getFundersPanelMonth() {
        return fundersPanelMonth;
    }

    public void setFundersPanelMonth(Integer fundersPanelMonth) {
        this.fundersPanelMonth = fundersPanelMonth;
    }

    public Integer getFundersPanelYear() {
        return fundersPanelYear;
    }

    public void setFundersPanelYear(Integer fundersPanelYear) {
        this.fundersPanelYear = fundersPanelYear;
    }

    public Integer getNotificationsDay() {
        return notificationsDay;
    }

    public void setNotificationsDay(Integer notificationsDay) {
        this.notificationsDay = notificationsDay;
    }

    public Integer getNotificationsMonth() {
        return notificationsMonth;
    }

    public void setNotificationsMonth(Integer notificationsMonth) {
        this.notificationsMonth = notificationsMonth;
    }

    public Integer getNotificationsYear() {
        return notificationsYear;
    }

    public void setNotificationsYear(Integer notificationsYear) {
        this.notificationsYear = notificationsYear;
    }

    public Integer getReleaseFeedbackDay() {
        return releaseFeedbackDay;
    }

    public void setReleaseFeedbackDay(Integer releaseFeedbackDay) {
        this.releaseFeedbackDay = releaseFeedbackDay;
    }

    public Integer getReleaseFeedbackMonth() {
        return releaseFeedbackMonth;
    }

    public void setReleaseFeedbackMonth(Integer releaseFeedbackMonth) {
        this.releaseFeedbackMonth = releaseFeedbackMonth;
    }

    public Integer getReleaseFeedbackYear() {
        return releaseFeedbackYear;
    }

    public void setReleaseFeedbackYear(Integer releaseFeedbackYear) {
        this.releaseFeedbackYear = releaseFeedbackYear;
    }
}
