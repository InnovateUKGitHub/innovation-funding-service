package org.innovateuk.ifs.management.competition.setup.application.form;

/**
 * Form for the application details section of the competition setup section.
 */
public class ProjectForm extends AbstractQuestionForm {

    private boolean removable;

    private Integer numberOfUploads;

    @Override
    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public Integer getNumberOfUploads() {
        return numberOfUploads;
    }

    public void setNumberOfUploads(Integer numberOfUploads) {
        this.numberOfUploads = numberOfUploads;
    }

}
