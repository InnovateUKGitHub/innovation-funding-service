package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Process defines database relations and a model to use client side and server side.
 * This is used for multiple types of events/processes.
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "process_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Process {
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
    protected List<ProcessOutcome> processOutcomes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="processRole", referencedColumnName = "id")
    ProcessRole processRole;

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

    public ProcessRole getProcessRole() {
        return processRole;
    }
}
