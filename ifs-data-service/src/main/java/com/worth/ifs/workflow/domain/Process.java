package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by nunoalexandre on 15/09/15.
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
