package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

/**
 * Process defines database relations and a model to use client side and server side.
 * This is used for multiple types of events/processes.
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "process_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Process<ParticipantType, TargetType> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected String event;
    protected String status;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastModified;

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy="process")
    @OrderColumn(name = "process_index")
    @Cascade(CascadeType.ALL)
    protected List<ProcessOutcome> processOutcomes;

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

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
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

    public List<ProcessOutcome> getProcessOutcomes() {
        return processOutcomes;
    }

    @JsonIgnore
    public Calendar getVersion() {
        return lastModified;
    }

    public abstract void setParticipant(ParticipantType participant);

    public abstract ParticipantType getParticipant();

    public abstract void setTarget(TargetType target);

    public abstract TargetType getTarget();
}
