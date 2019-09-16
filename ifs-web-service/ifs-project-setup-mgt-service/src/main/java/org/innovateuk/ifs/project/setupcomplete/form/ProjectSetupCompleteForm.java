package org.innovateuk.ifs.project.setupcomplete.form;

public class ProjectSetupCompleteForm {

    private Boolean successful;
    private boolean successfulConfirmation;
    private boolean unsuccessfulConfirmation;

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessfulConfirmation() {
        return successfulConfirmation;
    }

    public void setSuccessfulConfirmation(boolean successfulConfirmation) {
        this.successfulConfirmation = successfulConfirmation;
    }

    public boolean isUnsuccessfulConfirmation() {
        return unsuccessfulConfirmation;
    }

    public void setUnsuccessfulConfirmation(boolean unsuccessfulConfirmation) {
        this.unsuccessfulConfirmation = unsuccessfulConfirmation;
    }
}
