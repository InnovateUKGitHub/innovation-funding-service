package com.worth.ifs.competition.resource;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;

public class CompetitionResource{
    private Long id;
    private List<Long> applications = new ArrayList<>();
    private List<Long> questions = new ArrayList<>();
    private List<Long> sections = new ArrayList<>();
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate assessmentStartDate;
    private LocalDate assessmentEndDate;
    private Integer maxResearchRatio;

    public CompetitionResource() {
    }
    public CompetitionResource(Long id, List<Long> applications, List<Long> questions, List<Long> sections, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.applications = applications;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.description = description;
        this.startDate = startDate.toLocalDate();
        this.endDate = endDate.toLocalDate();
    }
    public CompetitionResource(long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate.toLocalDate(); //TO DO change back
        this.endDate = endDate.toLocalDate();
    }


    public List<Long> getSections() {
        return sections;
    }

    public String getDescription() {
        return description;
    }


    public void addApplication(Application... apps){
        if(applications == null){
            applications = new ArrayList<>();
        }
        this.applications.addAll(Arrays.asList(apps).stream().map(Application::getId).collect(Collectors.toList()));
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getAssessmentEndDate() {
        return assessmentEndDate;
    }

    public LocalDate getAssessmentStartDate() {
        return assessmentStartDate;
    }


    public void setSections(List<Long> sections) {
        this.sections = sections;
    }

    public void setQuestions(List<Long> questions) {
        this.questions = questions;
    }

    @JsonIgnore
    public long getDaysLeft(){
        return getDaysBetween(LocalDate.now(), this.endDate);
    }
    @JsonIgnore
    public long getAssessmentDaysLeft(){
        return getDaysBetween(LocalDate.now(), this.assessmentEndDate);
    }
    @JsonIgnore
    public long getTotalDays(){
        return getDaysBetween(this.startDate, this.endDate);
    }
    @JsonIgnore
    public long getAssessmentTotalDays() {
        return getDaysBetween(this.assessmentStartDate, this.assessmentEndDate);
    }
    @JsonIgnore
    public long getStartDateToEndDatePercentage() {
        return getDaysLeftPercentage(getDaysLeft(), getTotalDays());
    }
    @JsonIgnore
    public long getAssessmentDaysLeftPercentage() {
        return getDaysLeftPercentage(getAssessmentDaysLeft(), getAssessmentTotalDays());
    }
    @JsonIgnore
    public List<Long> getApplications() {
        return applications;
    }
    @JsonIgnore
    public List<Long> getQuestions(){return questions;}



    /* Keep it D.R.Y */

    private long getDaysBetween(LocalDate dateA, LocalDate dateB) {
        return ChronoUnit.DAYS.between(dateA, dateB);

    }

    private long getDaysLeftPercentage(long daysLeft, long totalDays ) {
        if(daysLeft <= 0){
            return 100;
        }
        double deadlineProgress = 100-( ( (double)daysLeft/(double)totalDays )* 100);
        long startDateToEndDatePercentage = (long) deadlineProgress;
        return startDateToEndDatePercentage;
    }

    public void setAssessmentEndDate(LocalDate assessmentEndDate) {
        this.assessmentEndDate = assessmentEndDate;
    }

    public void setAssessmentStartDate(LocalDate assessmentStartDate){
        this.assessmentStartDate = assessmentStartDate;
    }

    public Integer getMaxResearchRatio() {
        return maxResearchRatio;
    }

    public void setMaxResearchRatio(Integer maxResearchRatio) {
        this.maxResearchRatio = maxResearchRatio;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApplications(List<Long> applications) {
        this.applications = applications;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
