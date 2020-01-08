package org.innovateuk.ifs.application.forms.questions.applicationdetails.model;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import static java.lang.Boolean.TRUE;

/**
 * View model for application details.
 */
public class ApplicationDetailsViewModel {

    private ApplicantQuestionResource applicantResource;
    private NavigationViewModel navigationViewModel;
    private ApplicationDetailsInputViewModel formInputViewModel;
    private boolean allReadOnly;

    public ApplicationDetailsViewModel(ApplicantQuestionResource applicantResource,
                                       ApplicationDetailsInputViewModel formInputViewModel,
                                       NavigationViewModel navigationViewModel) {
        this.applicantResource = applicantResource;
        this.formInputViewModel = formInputViewModel;
        this.navigationViewModel = navigationViewModel;
        this.allReadOnly = formInputViewModel.isReadonly();
    }

    public NavigationViewModel getNavigation() {
        return getNavigationViewModel();
    }

    public NavigationViewModel getNavigationViewModel() {
        return navigationViewModel;
    }

    public Boolean isShowReturnButtons() {
        return TRUE;
    }

    public String getTitle() {
        return applicantResource.getQuestion().getShortName();
    }

    public ApplicationDetailsInputViewModel getFormInputViewModel() {
        return formInputViewModel;
    }

    public boolean isAllReadOnly() {
        return allReadOnly;
    }

    public void setAllReadOnly(boolean allReadOnly) {
        this.allReadOnly = allReadOnly;
    }

    public Boolean getApplicationIsClosed() {
        return !getCompetition().isOpen() || !getApplication().isOpen();
    }

    public Boolean getApplicationIsReadOnly() {
        return !getCompetition().isOpen() || !getApplication().isOpen();
    }

    public ApplicationResource getApplication() {
        return applicantResource.getApplication();
    }

    public CompetitionResource getCompetition() {
        return applicantResource.getCompetition();
    }

    public ApplicantQuestionResource getApplicantResource() {
        return applicantResource;
    }

    public boolean isQuestion() {
        return true;
    }

    public boolean isSection() {
        return false;
    }

    public boolean isLeadApplicant() {
        return applicantResource.getCurrentApplicant().isLead();
    }

    public ApplicantResource getCurrentApplicant() {
        return applicantResource.getCurrentApplicant();
    }
}
