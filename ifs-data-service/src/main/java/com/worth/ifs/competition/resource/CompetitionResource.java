package com.worth.ifs.competition.resource;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Relation(value="competition", collectionRelation="competitions")
public class CompetitionResource{
    private Long id;
    private List<Long> applications = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate assessmentStartDate;
    private LocalDate assessmentEndDate;

    public CompetitionResource() {
    }
    public CompetitionResource(Long id, List<Application> applications, List<Question> questions, List<Section> sections, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.applications = applications.stream().map(Application::getId).collect(Collectors.toList());
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


    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications.stream().map(Application::getId).collect(Collectors.toList());
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
    public List<Question> getQuestions(){return questions;}



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
}
