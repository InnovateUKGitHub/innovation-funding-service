package org.innovateuk.ifs.project.financereviewer.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;

import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.FINANCE_REVIEWER;

/**
 * A project finance user assigned to a project as a finance reviewer.
 */
@Entity
@DiscriminatorValue("FINANCE_REVIEWER")
public class FinanceReviewer extends ProjectParticipant {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    private Project project;

    public FinanceReviewer() {
        super(null, FINANCE_REVIEWER);
    }

    public FinanceReviewer(User user, Project project) {
        super(user, FINANCE_REVIEWER);
        this.project = project;
    }

    @Override
    public Project getProcess() {
        return project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FinanceReviewer that = (FinanceReviewer) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .toHashCode();
    }

}