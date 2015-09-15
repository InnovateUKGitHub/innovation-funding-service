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


    public Process(){}

    public Process(ProcessEvent event, ProcessStatus status, Long assigneeId, Long subjectId ) {
        this.event = event;
        this.status = status;
        this.assigneeId = assigneeId;
        this.subjectId = subjectId;
    }

    /** Getters **/


    public Long getAssignee() {
        return assigneeId;
    }
    public Long getSubject() {
        return subjectId;
    }
    public Long getId() {
        return id;
    }
    public ProcessStatus getStatus() {
        return status;
    }
    public ProcessEvent getEvent() {
        return event;
    }

    @JsonIgnore
    public Calendar getVersion() {
        return lastModified;
    }

    /** Setters **/

    public void setAssignee(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
    public void setSubject(Long subjectId) {
        this.subjectId = subjectId;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public void setEvent(ProcessEvent event) {
        this.event = event;
    }
}
