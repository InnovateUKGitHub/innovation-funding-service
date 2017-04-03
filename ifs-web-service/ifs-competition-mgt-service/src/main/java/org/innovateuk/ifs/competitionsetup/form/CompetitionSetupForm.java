package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Generic form class to pass and save section data.
 */
public abstract class CompetitionSetupForm extends BaseBindingResultTarget {

    private boolean markAsCompleteAction = true;

    public void setMarkAsCompleteAction(boolean markAsCompleteAction) {
        this.markAsCompleteAction = markAsCompleteAction;
    }

    public boolean isMarkAsCompleteAction() {
        return markAsCompleteAction;
    }
}
