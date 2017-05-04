package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

public class TextAreaInputViewModel extends AbstractFormInputViewModel {

    private AssignButtonsViewModel assignButtonsViewModel;

    @Override
    protected FormInputType formInputType() {
        return FormInputType.TEXTAREA;
    }

    public AssignButtonsViewModel getAssignButtonsViewModel() {
        return assignButtonsViewModel;
    }

    public void setAssignButtonsViewModel(AssignButtonsViewModel assignButtonsViewModel) {
        this.assignButtonsViewModel = assignButtonsViewModel;
    }

    /* View logic methods. */
    public String getLastUpdatedText() {
        String userUpdated = isRespondedByCurrentUser() ? "you" : applicantResponse.getResponse().getUpdatedByUserName();
        return " by " + userUpdated;
    }

    public boolean isRespondedByCurrentUser() {
        return applicantResponse.getApplicant().isSameUser(currentApplicant);
    }

}
