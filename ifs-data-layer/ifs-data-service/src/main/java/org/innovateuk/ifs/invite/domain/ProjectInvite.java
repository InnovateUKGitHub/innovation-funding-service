package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;

@MappedSuperclass
public abstract class ProjectInvite<T extends ProjectInvite<T>> extends Invite<Project, T> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Project project;

    public ProjectInvite() {
    }

    public ProjectInvite(final String name, final String email, final String hash, final Project project, final InviteStatus status) {
        super(name, email, hash, status);
        this.project = project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    @Override
    public Project getTarget() {
        return project;
    }

    @Override
    public void setTarget(final Project project) {
        this.project = project;
    }
}