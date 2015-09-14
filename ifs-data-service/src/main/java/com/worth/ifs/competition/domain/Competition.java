package com.worth.ifs.competition.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="competition")
    private List<Application> applications;

    @OneToMany(mappedBy="competition")
    private List<Question> questions;

    @OneToMany(mappedBy="competition")
    private List<Section> sections;

    private String name;

    @Column( length = 5000 )
    private String description;
    @DateTimeFormat
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate assessmentStartDate;
    private LocalDate assessmentEndDate;

    public Competition() {
    }
    public Competition(Long id, List<Application> applications, List<Question> questions, List<Section> sections, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.applications = applications;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.description = description;
        this.startDate = startDate.toLocalDate();
        this.endDate = endDate.toLocalDate();
    }
    public Competition(long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate.toLocalDate(); //TO DO change back
        this.endDate = endDate.toLocalDate();
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
    public List<Application> getApplications() {
        return applications;
    }



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

}

