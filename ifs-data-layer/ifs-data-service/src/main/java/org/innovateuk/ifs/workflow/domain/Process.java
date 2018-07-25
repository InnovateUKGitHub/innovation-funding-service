package org.innovateuk.ifs.workflow.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryEntityListener;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Process defines database relations and a model to use client side and server side.
 * This is used for multiple types of events/processes.
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "process_type", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(ProcessHistoryEntityListener.class)
public abstract class Process<ParticipantType, TargetType, StatesType extends ProcessState> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String event;

    @LastModifiedDate
    private ZonedDateTime lastModified = ZonedDateTime.now();

    private LocalDate startDate;
    private LocalDate endDate;

    @Version
    private int version = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="internal_participant_id", referencedColumnName = "id")
    protected User internalParticipant;

    public Process() {
    }

    protected Process(String event, StatesType activityState) {
        this.event = event;
        setProcessState(activityState);
    }

    public Process(String event, StatesType activityState, LocalDate startDate, LocalDate endDate) {
        this(event, activityState);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public String getProcessEvent() {
        return event;
    }

    public void setProcessEvent(String event) {
        this.event = event;
    }

    public abstract void setParticipant(ParticipantType participant);

    public abstract ParticipantType getParticipant();

    public abstract void setTarget(TargetType target);

    public abstract TargetType getTarget();

    public User getInternalParticipant() {
        return internalParticipant;
    }

    public void setInternalParticipant(User internalParticipant) {
        this.internalParticipant = internalParticipant;
    }

    public final boolean isInState(StatesType state) {
        return getProcessState() == state;
    }

    public abstract StatesType getProcessState();

    public abstract void setProcessState(StatesType status);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Process<?, ?, ?> process = (Process<?, ?, ?>) o;

        // Note: processOutcomes have been taken out as there is a cycle relationship
        // between Process and ProcessOutcome causing issues if you call Process#equals

        return new EqualsBuilder()
                .append(id, process.id)
                .append(event, process.event)
                .append(startDate, process.startDate)
                .append(endDate, process.endDate)
                .append(version, process.version)
                .append(internalParticipant, process.internalParticipant)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(event)
                .append(startDate)
                .append(endDate)
                .append(version)
                .append(internalParticipant)
                .toHashCode();
    }
}