package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by nunoalexandre on 15/09/15.
 */

@Entity
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "assigneeId")
    private Long assigneeId;

    @Column(name = "subjectId")
    private Long subjectId;

    @Enumerated(EnumType.STRING)
    private ProcessEvent event;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastModified;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(name = "observations")
    private String observations;


    public Process() {
    }

    public Process(ProcessEvent event, ProcessStatus status, Long assigneeId, Long subjectId) {
        this.event = event;
        this.status = status;
        this.assigneeId = assigneeId;
        this.subjectId = subjectId;
    }


    public Process(ProcessEvent event, ProcessStatus status, Long assigneeId, Long subjectId, LocalDate startDate, LocalDate endDate) {
        this(event, status, assigneeId, subjectId);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Process(ProcessEvent event, ProcessStatus status, Long assigneeId, Long subjectId, LocalDate startDate, LocalDate endDate, String observations) {
        this(event, status, assigneeId, subjectId, startDate, endDate);
        this.observations = observations;
    }


    /**
     * Getters
     **/
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Setters
     **/
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getAssignee() {
        return assigneeId;
    }

    public void setAssignee(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getSubject() {
        return subjectId;
    }

    public void setSubject(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getId() {
        return id;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public ProcessEvent getEvent() {
        return event;
    }

    public void setEvent(ProcessEvent event) {
        this.event = event;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @JsonIgnore
    public Calendar getVersion() {
        return lastModified;
    }
}
