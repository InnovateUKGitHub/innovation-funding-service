package org.innovateuk.ifs.project.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.Participant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

/**
 * ProjectUser defines a User's role on a Project and in relation to a particular Organisation.
 */
@MappedSuperclass
public abstract class ProjectParticipant<I extends Invite<Project, I>,  R extends ProjectParticipantRole> extends Participant<Project, I, R> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    private Project project;


    public ProjectParticipant() {
    }

    public ProjectParticipant(User user, Project project) {
        this.user = user;
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Project getProcess() {
        return project;
    }

    public Long getId() {
        return id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUser(Long userId) {
        return this.user.hasId(userId);
    }

    public Project getProject() {
        return project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectParticipant<?, ?> that = (ProjectParticipant<?, ?>) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user, that.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(user)
                .append(project)
                .toHashCode();
    }
}