package org.innovateuk.ifs.application.forms.questions.applicationdetails.model;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

/**
 * View model for application details.
 */
public class ApplicationDetailsViewModel {

    protected ApplicantQuestionResource applicantResource;
    protected NavigationViewModel navigationViewModel;
    private ApplicationDetailsInputViewModel formInputViewModel;
    private boolean allReadOnly;
    private Long applicantOrganisationId;
    private boolean readOnlyAllApplicantApplicationFinances;

    public ApplicationDetailsViewModel(ApplicantQuestionResource applicantResource,
                                       ApplicationDetailsInputViewModel formInputViewModel,
                                       NavigationViewModel navigationViewModel,
                                       boolean allReadOnly,
                                       Long applicantOrganisationId,
                                       boolean readOnlyAllApplicantApplicationFinances) {
        this.applicantResource = applicantResource;
        this.formInputViewModel = formInputViewModel;
        this.navigationViewModel = navigationViewModel;
        this.allReadOnly = allReadOnly;
        this.applicantOrganisationId = applicantOrganisationId;
        this.readOnlyAllApplicantApplicationFinances = readOnlyAllApplicantApplicationFinances;
    }

    public NavigationViewModel getNavigation() {
        return getNavigationViewModel();
    }

    public NavigationViewModel getNavigationViewModel() {
        return navigationViewModel;
    }

    public Boolean isShowReturnButtons() {
        return Boolean.TRUE;
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

    public CompetitionResource getCompetition() { return applicantResource.getCompetition(); }

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

    public ApplicantResource getCurrentApplicant() { return applicantResource.getCurrentApplicant(); }

    public Long getApplicantOrganisationId() {
        return applicantOrganisationId;
    }

    public void setApplicantOrganisationId(Long applicantOrganisationId) { this.applicantOrganisationId = applicantOrganisationId; }

    public boolean isReadOnlyAllApplicantApplicationFinances() { return readOnlyAllApplicantApplicationFinances; }

    public void  setReadOnlyAllApplicantApplicationFinances(boolean readOnlyAllApplicantApplicationFinances) { this.readOnlyAllApplicantApplicationFinances = readOnlyAllApplicantApplicationFinances; }


}
