package com.worth.ifs.competition.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Competition defines database relations and a model to use client side and server side.
 */
@Entity
public class Competition {


    public CompetitionResource.Status getCompetitionStatus() {
        LocalDateTime today = LocalDateTime.now();
        if(getStartDate().isAfter(today)){
            return CompetitionResource.Status.NOT_STARTED;
        }else if(getEndDate().isAfter(today)){
            return CompetitionResource.Status.OPEN;
        }else if(getAssessmentEndDate().isAfter(today)){
            return CompetitionResource.Status.IN_ASSESSMENT;
        }

        return CompetitionResource.Status.FUNDERS_PANEL;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="competition")
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy="competition")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy="competition")
    @OrderBy("priority ASC")
    private List<Section> sections = new ArrayList<>();

    private String name;

    @Column( length = 5000 )
    private String description;
    @DateTimeFormat
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime assessmentStartDate;
    private LocalDateTime assessmentEndDate;
    private LocalDateTime assessorFeedbackDate;
    private Integer maxResearchRatio;
    private Integer academicGrantPercentage;

    public Competition() {
    	// no-arg constructor
    }
    public Competition(Long id, List<Application> applications, List<Question> questions, List<Section> sections, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.applications = applications;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public Competition(long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public List<Section> getSections() {
        return sections;
    }

    public String getDescription() {
        return description;
    }


    public void addApplication(Application... apps){
        if(applications == null){
            applications = new ArrayList<>();
        }
        this.applications.addAll(Arrays.asList(apps));
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getAssessmentEndDate() {
        return assessmentEndDate;
    }

    public LocalDateTime getAssessmentStartDate() {
        return assessmentStartDate;
    }


    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    @JsonIgnore
    public long getDaysLeft(){
        return getDaysBetween(LocalDateTime.now(), this.endDate);
    }
    @JsonIgnore
    public long getAssessmentDaysLeft(){
        return getDaysBetween(LocalDateTime.now(), this.assessmentEndDate);
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
    public List<Application> getApplications() {
        return applications;
    }
    @JsonIgnore
    public List<Question> getQuestions(){return questions;}

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getAssessorFeedbackDate() {
        return assessorFeedbackDate;
    }

    public void setAssessorFeedbackDate(LocalDateTime assessorFeedbackDate) {
        this.assessorFeedbackDate = assessorFeedbackDate;
    }

    private long getDaysBetween(LocalDateTime dateA, LocalDateTime dateB) {
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

    public void setAssessmentEndDate(LocalDateTime assessmentEndDate) {
        this.assessmentEndDate = assessmentEndDate;
    }

    public void setAssessmentStartDate(LocalDateTime assessmentStartDate){
        this.assessmentStartDate = assessmentStartDate;
    }

    public Integer getMaxResearchRatio() {
        return maxResearchRatio;
    }

    public void setMaxResearchRatio(Integer maxResearchRatio) {
        this.maxResearchRatio = maxResearchRatio;
    }

    public Integer getAcademicGrantPercentage() {
        return academicGrantPercentage;
    }

    public void setAcademicGrantPercentage(Integer academicGrantPercentage) {
        this.academicGrantPercentage = academicGrantPercentage;
    }

    public void setId(Long id) {
        this.id = id;
    }


}

