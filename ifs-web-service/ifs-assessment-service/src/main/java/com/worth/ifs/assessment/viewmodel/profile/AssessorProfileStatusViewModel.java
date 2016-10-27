package com.worth.ifs.assessment.viewmodel.profile;

/**
 * Created by tom on 26/10/2016.
 */
public class AssessorProfileStatusViewModel {
    private final boolean skillsComplete;
    private final boolean affiliationsComplete;
    private final boolean contractComplete;

    public AssessorProfileStatusViewModel(boolean skillsComplete, boolean affiliationsComplete, boolean contractComplete) {
        this.skillsComplete = skillsComplete;
        this.affiliationsComplete = affiliationsComplete;
        this.contractComplete = contractComplete;
    }

    public boolean isSkillsComplete() {
        return skillsComplete;
    }

    public boolean isAffiliationsComplete() {
        return affiliationsComplete;
    }

    public boolean isContractComplete() {
        return contractComplete;
    }

    public boolean isComplete() {
        return skillsComplete && affiliationsComplete && contractComplete;
    }
}
