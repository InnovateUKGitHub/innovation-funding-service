
package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;

import java.util.List;

public class DefaultProjectCostSection {
    private ApplicantSectionResource applicantResource;
    private ApplicantSectionResource applicantSection;
    private List<AbstractFormInputViewModel> costViews;

    public ApplicantSectionResource getApplicantResource() {
        return applicantResource;
    }

    public void setApplicantResource(ApplicantSectionResource applicantResource) {
        this.applicantResource = applicantResource;
    }

    public ApplicantSectionResource getApplicantSection() {
        return applicantSection;
    }

    public void setApplicantSection(ApplicantSectionResource applicantSection) {
        this.applicantSection = applicantSection;
    }

    public List<AbstractFormInputViewModel> getCostViews() {
        return costViews;
    }

    public void setCostViews(List<AbstractFormInputViewModel> costViews) {
        this.costViews = costViews;
    }
}

