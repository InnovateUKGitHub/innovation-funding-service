package com.worth.ifs.project.finance.domain;

import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.Process;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The process of submitting and approving Finance Checks for individual Organisations on Projects
 */
@Entity
public class FinanceCheckProcess extends Process<ProjectUser, PartnerOrganisation, FinanceCheckState> {

    @ManyToOne
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private PartnerOrganisation target;

    // for ORM use
    FinanceCheckProcess() {
    }

    public FinanceCheckProcess(ProjectUser participant, PartnerOrganisation target, ActivityState originalState) {
        this.participant = participant;
        this.target = target;
        this.setActivityState(originalState);
    }

    public FinanceCheckProcess(User internalParticipant, PartnerOrganisation target, ActivityState originalState) {
        this.internalParticipant = internalParticipant;
        this.target = target;
        this.setActivityState(originalState);
    }

    @Override
    public void setParticipant(ProjectUser participant) {
        this.participant = participant;
    }

    @Override
    public ProjectUser getParticipant() {
        return participant;
    }

    @Override
    public PartnerOrganisation getTarget() {
        return target;
    }

    @Override
    public void setTarget(PartnerOrganisation target) {
        this.target = target;
    }

    @Override
    public FinanceCheckState getActivityState() {
        return FinanceCheckState.fromState(activityState.getState());
    }
}
