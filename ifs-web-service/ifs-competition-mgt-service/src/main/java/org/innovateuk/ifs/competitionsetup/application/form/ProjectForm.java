package org.innovateuk.ifs.competitionsetup.application.form;

/**
 * Form for the application details section of the competition setup section.
 */
public class ProjectForm extends AbstractQuestionForm {

    private boolean removable;

    @Override
    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

}
