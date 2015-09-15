package com.worth.ifs.workflow.domain;

import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;

@Entity
@IdClass(ProcessValueId.class)
public class ProcessValue {
    @Id
    @ManyToOne
    @JoinColumn(name="process_role_id")
    private ProcessRole processRole;

    @Id
    @ManyToOne
    @JoinColumn(name="process_id")
    private Process process;

    String value;

    public ProcessValue() {
    }

    public ProcessValue(ProcessRole processRole, Process process, String value) {
        this.processRole = processRole;
        this.process = process;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ProcessRole getProcessRole() {
        return processRole;
    }


}
