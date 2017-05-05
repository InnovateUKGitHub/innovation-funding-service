package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.YourFinancesSectionViewModel;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class YourFinancesSectionPopulator extends AbstractSectionPopulator<YourFinancesSectionViewModel> {

    @Override
    protected YourFinancesSectionViewModel createNew(ApplicantSectionResource applicantSection) {
        return new YourFinancesSectionViewModel(applicantSection, Collections.emptyList(), getNavigationViewModel(applicantSection));
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FINANCE;
    }
}

