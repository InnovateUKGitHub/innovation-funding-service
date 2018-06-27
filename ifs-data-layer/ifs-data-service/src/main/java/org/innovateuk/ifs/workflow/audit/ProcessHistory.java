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
class ProcessHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="process_id", referencedColumnName = "id")
    private final Process process;

    private final String processStateName;

    ProcessHistory() {
        this.process = null;
        this.processStateName = null;
    }

    ProcessHistory(Process process) {
        if (process == null) throw new NullPointerException("process cannot be null");

        this.process = process;
        this.processStateName = process.getProcessState().getStateName();
    }

    Process getProcess() {
        return process;
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