package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;
import java.util.Optional;

/**
 * Generic ViewModel for common fields in SectionViewModels
 */
public abstract class AbstractApplicationFormViewModel<R extends AbstractApplicantResource> {

    protected R applicantResource;
    protected NavigationViewModel navigationViewModel;
    private List<AbstractFormInputViewModel> formInputViewModels;
    private boolean allReadOnly;
    private Long applicantOrganisationId;


    private boolean readOnlyAllApplicantApplicationFinances;

    public AbstractApplicationFormViewModel(R applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        this.applicantResource = applicantResource;
        this.formInputViewModels = formInputViewModels;
        this.navigationViewModel = navigationViewModel;
        this.allReadOnly = allReadOnly;
        if (applicantOrganisationId.isPresent()) {
            this.applicantOrganisationId = applicantOrganisationId.get();
        }
        this.readOnlyAllApplicantApplicationFinances = readOnlyAllApplicantApplicationFinances;
    }

    public List<AbstractFormInputViewModel> getFormInputViewModels() {
        return formInputViewModels;
    }

    public NavigationViewModel getNavigationViewModel() {
        return navigationViewModel;
    }

    public abstract String getTitle();

    public boolean isAllReadOnly() {
        return allReadOnly;
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

    public R getApplicantResource() {
        return applicantResource;
    }

    public boolean isQuestion() {
        return applicantResource instanceof ApplicantQuestionResource;
    }

    public boolean isSection() {
        return applicantResource instanceof ApplicantSectionResource;
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
