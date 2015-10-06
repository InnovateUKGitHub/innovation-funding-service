package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Process defines database relations and a model to use client side and server side.
 * This is used for multiple types of events/processes.
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="event")
    protected String event;

    @Column(name="status")
    protected String status;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastModified;

    private LocalDate startDate;

    private LocalDate endDate;
    @Column(name = "observations")
    private String observations;

    @Column(name="decision_reason")
    private String decisionReason;

    public Process() {
    }


    public Process(String event, String status) {
        this.event = event;
        this.status = status;
    }

    public Process(String event, String status, LocalDate startDate, LocalDate endDate) {
        this(event, status);
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public Process(String event, String status, LocalDate startDate, LocalDate endDate, String observations) {
        this(event, status, startDate, endDate);
        this.observations = observations;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public String getProcessStatus()  {
        return status;
    }

    public void setProcessStatus(String status) {
        this.status = status;
    }

    public String getProcessEvent() {
        return event;
    }

    public void setProcessEvent(String event) {
        this.event = event;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String reason) {
        this.decisionReason = reason;
    }

    @JsonIgnore
    public Calendar getVersion() {
        return lastModified;
    }
}
