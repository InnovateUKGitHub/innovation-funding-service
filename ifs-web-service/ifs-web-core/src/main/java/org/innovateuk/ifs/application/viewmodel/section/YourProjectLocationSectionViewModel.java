package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;
import java.util.Optional;

/**
 * Your project location view model.
 */
public class YourProjectLocationSectionViewModel extends AbstractSectionViewModel {
    private boolean complete;
    private boolean readonly;
    private String projectLocationValue;

    public YourProjectLocationSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }


    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public String getProjectLocationValue() {
        return projectLocationValue;
    }

    public void setProjectLocationValue(String projectLocationValue) {
        this.projectLocationValue = projectLocationValue;
    }
}

