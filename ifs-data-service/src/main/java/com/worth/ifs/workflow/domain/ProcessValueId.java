package com.worth.ifs.workflow.domain;

import java.io.Serializable;

public class ProcessValueId implements Serializable {
    private Long processRole;
    private Long process;

    public ProcessValueId() {

    }

    public ProcessValueId(Long processRole, Long process) {
        this.processRole = processRole;
        this.process = process;
    }

    public Long getProcessRole() {
        return processRole;
    }

    public Long getProcess() {
        return process;
    }

    public boolean equals(Object object) {
        if (object instanceof ProcessValueId) {
            ProcessValueId pv = (ProcessValueId)object;
            return processRole.equals(pv.processRole) && process.equals(pv.process);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return processRole.hashCode() + process.hashCode();
    }
}
