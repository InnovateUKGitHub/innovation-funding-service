package com.worth.ifs.workflow.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the different types (e.g. application, competition) for which a process can act upon
 */
@Entity
public class ProcessType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String description;

    @OneToMany(mappedBy = "processType")
    private List<Process> processes = new ArrayList<Process>();

    public ProcessType(String description) {
        this.description = description;
    }

    public ProcessType(Long id, String description, List<Process> processes) {
        this.id = id;
        this.description = description;
        this.processes = processes;
    }
}
