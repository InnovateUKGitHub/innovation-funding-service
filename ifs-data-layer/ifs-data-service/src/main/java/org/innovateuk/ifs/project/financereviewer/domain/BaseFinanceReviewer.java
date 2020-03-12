package org.innovateuk.ifs.project.financereviewer.domain;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public abstract class BaseFinanceReviewer extends ProjectParticipant {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    private Project project;

    public BaseFinanceReviewer(User user, ProjectParticipantRole projectParticipantRole) {
        super(user, projectParticipantRole);
    }

    public BaseFinanceReviewer(User user, ProjectParticipantRole projectParticipantRole, Project project) {
        super(user, projectParticipantRole);
        this.project = project;
    }

    @Override
    public Project getProcess() {
        return project;
    }
}
