package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.Role;

/**
 * View model for text area form input.
 */
public class TextAreaInputViewModel extends AbstractFormInputViewModel {

    private AssignButtonsViewModel assignButtonsViewModel;
    private ApplicationResource application;

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

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public Long getApplicationId() {
        return application.getId();
    }

    public String getLastUpdatedText() {
        String userUpdated = isRespondedByCurrentUser() ? "you" : applicantResponse.getResponse().getUpdatedByUserName();
        return " by " + userUpdated;
    }

    public boolean isRespondedByCurrentUser() {
        return applicantResponse.getApplicant().isSameUser(currentApplicant);
    }

/*    public boolean isRespondedByCurrentUser() {

        if (assignButtonsViewModel.getCurrentUser().hasRole(Role.SUPPORT)) {
            return false;
        } else {
            return applicantResponse.getApplicant().isSameUser(currentApplicant);
        }
    }*/

    public boolean isCanMarkAsComplete() {
        return assignButtonsViewModel != null && assignButtonsViewModel.isAssignedToCurrentUser();
    }

}
