package com.worth.ifs.workflow.domain;

import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The process that is taken place where a certain action should be taken place.
 * A process can be assigned to a certain user.
 */
@Entity
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy="process")
    private List<ProcessValue> processValues = new ArrayList<ProcessValue>();

    @ManyToOne
    @JoinColumn(name="processEventId", referencedColumnName="id")
    private ProcessEvent processEvent;


    @ManyToOne
    @JoinColumn(name="processTypeId", referencedColumnName="id")
    private ProcessType processType;

    public Process(LocalDate startDate, LocalDate endDate, ProcessEvent processEvent, ProcessType processType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.processEvent = processEvent;
        this.processType = processType;
    }

    public Process(List<ProcessValue> processValues) {
        this.processValues = processValues;
    }

    public List<ProcessValue> getProcessValues() {
        return processValues;
    }

    public void setProcessValues(List<ProcessValue> processValues) {
        this.processValues = processValues;
    }

    public ProcessEvent getProcessEvent() {
        return processEvent;
    }

    public ProcessType getProcessType() {
        return processType;
    }
}
