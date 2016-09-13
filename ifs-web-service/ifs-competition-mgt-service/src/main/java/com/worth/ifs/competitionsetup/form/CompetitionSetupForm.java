package com.worth.ifs.competitionsetup.form;

/**
 * Generic form class to pass and save section data.
 */
public abstract class CompetitionSetupForm {

    private boolean markAsCompleteAction = true;

    public void setMarkAsCompleteAction(boolean markAsCompleteAction) {
        this.markAsCompleteAction = markAsCompleteAction;
    }

    public boolean isMarkAsCompleteAction() {
        return markAsCompleteAction;
    }

}
