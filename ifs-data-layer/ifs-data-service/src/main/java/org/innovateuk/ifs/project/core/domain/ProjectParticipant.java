package org.innovateuk.ifs.project.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DiscriminatorOptions;
import org.innovateuk.ifs.invite.domain.Participant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

/**
 * A defines a User's role on a Project
 */
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
//@MappedSuperclass
@Entity
@Table(name = "project_user")

/*
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Entity
@DiscriminatorOptions(force = true)
 */

public abstract class ProjectParticipant extends Participant<Project, ProjectParticipantRole> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role")
    private ProjectParticipantRole role;

    protected ProjectParticipant() {
    }

    public ProjectParticipant(User user, ProjectParticipantRole role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUser(Long userId) {
        return this.user.hasId(userId);
    }

    @Override
    public ProjectParticipantRole getRole() {
        return role;
    }

    public Project getProject() {
        return getProcess();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectParticipant that = (ProjectParticipant) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user, that.user)
                .append(role, that.role)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(user)
                .append(role)
                .toHashCode();
    }
}