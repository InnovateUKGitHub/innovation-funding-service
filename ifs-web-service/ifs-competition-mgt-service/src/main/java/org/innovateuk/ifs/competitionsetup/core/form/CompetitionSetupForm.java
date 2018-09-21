package org.innovateuk.ifs.competitionsetup.core.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Generic form class to pass and save section data.
 */
public abstract class CompetitionSetupForm extends BaseBindingResultTarget {

    private boolean markAsCompleteAction = true;
    private boolean isAutoSaveAction;

    public void setMarkAsCompleteAction(boolean markAsCompleteAction) {
        this.markAsCompleteAction = markAsCompleteAction;
    }

    public boolean isMarkAsCompleteAction() {
        return markAsCompleteAction;
    }

    public boolean isAutoSaveAction() {
        return isAutoSaveAction;
    }

    public void setAutoSaveAction(boolean autoSaveAction) {
        isAutoSaveAction = autoSaveAction;
    }
}
