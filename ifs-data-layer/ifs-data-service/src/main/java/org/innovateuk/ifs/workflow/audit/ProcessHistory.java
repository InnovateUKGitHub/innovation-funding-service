package org.innovateuk.ifs.workflow.audit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * Records a {@link Process} state change.
 *
 * @see AuditableEntity
 * @see Process
 */
@Entity
public class ProcessHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name="process_id", referencedColumnName = "id")
    private Process process;

    private String processStateName;

    public ProcessHistory() {
        this.process = null;
        this.processStateName = null;
    }

    public ProcessHistory(Process process) {
        if (process == null) throw new NullPointerException("process cannot be null");

        this.process = process;
        this.processStateName = process.getProcessState().getStateName();
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    String getProcessStateName() {
        return processStateName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProcessHistory that = (ProcessHistory) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(process, that.process)
                .append(processStateName, that.processStateName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(process)
                .append(processStateName)
                .toHashCode();
    }
}