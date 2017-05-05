package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.AbstractSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSectionPopulator<M extends AbstractSectionViewModel> extends BaseModelPopulator {

    @Autowired
    private ApplicationNavigationPopulator navigationPopulator;

    public M populate(ApplicantSectionResource section, ApplicationForm form) {
        return createNew(section);
    }

    protected abstract M createNew(ApplicantSectionResource section);

    public abstract SectionType getSectionType();

    protected NavigationViewModel getNavigationViewModel(ApplicantSectionResource applicantSection) {
        return navigationPopulator.addNavigation(applicantSection.getSection(),
                applicantSection.getApplication().getId(),
                SectionType.sectionsNotRequiredForOrganisationType(applicantSection.getCurrentApplicant().getOrganisation().getOrganisationType()));

    }
}
