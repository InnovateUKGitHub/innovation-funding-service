package org.innovateuk.ifs.workflow.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.util.AuditableEntity;

import javax.persistence.*;

@Entity
public class ProcessHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="process_id", referencedColumnName = "id")
    private Process process;

    public ProcessHistory() {
        this.process = null;
    }

    public ProcessHistory(Process process) {
        this.process = process;
    }

    public Long getId() {
        return id;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProcessHistory that = (ProcessHistory) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(process, that.process)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(process)
                .toHashCode();
    }
}