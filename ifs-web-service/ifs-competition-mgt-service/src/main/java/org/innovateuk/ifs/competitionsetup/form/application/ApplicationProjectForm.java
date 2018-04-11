package org.innovateuk.ifs.competitionsetup.form.application;

/**
 * Form for the application details section of the competition setup section.
 */
public class ApplicationProjectForm extends AbstractApplicationQuestionForm {

    private boolean removable;

    @Override
    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

}
