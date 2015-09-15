package com.worth.ifs.workflow.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for reoccurring processes, which does not involve specific types to which these processes relate to.
 */
@Entity
public class ProcessEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String description;

    @OneToMany(mappedBy = "processEvent")
    private List<Process> processes = new ArrayList<Process>();

    public ProcessEvent(String description) {
        this.description = description;
    }

    public ProcessEvent(Long id, String description, List<Process> processes) {
        this.id = id;
        this.description = description;
        this.processes = processes;
    }
}
