package org.innovateuk.ifs.workflow.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.commons.util.AuditableEntity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "outcome_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ProcessOutcome<ProcessType extends Process> extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    protected String outcome;
    protected String description;
    protected String comment;
    @OneToOne(fetch = FetchType.LAZY, targetEntity = Process.class)
    @JoinColumn(name = "process_id", referencedColumnName = "id")
    private ProcessType process;

    protected ProcessOutcome() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessType getProcess() {
        return process;
    }

    public void setProcess(ProcessType process) {
        this.process = process;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProcessOutcome<?> that = (ProcessOutcome<?>) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(outcome, that.outcome)
                .append(description, that.description)
                .append(comment, that.comment)
                .append(process, that.process)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(outcome)
                .append(description)
                .append(comment)
                .append(process)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("outcome", outcome)
                .append("description", description)
                .append("comment", comment)
                .append("process", process)
                .toString();
    }
}
