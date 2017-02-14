package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicSectionContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.EligibilityViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content eligibility view model.
 */

@Service
public class EligibilityViewModelPopulator extends AbstractPublicSectionContentGroupViewModelPopulator<EligibilityViewModel> {

    @Override
    protected EligibilityViewModel createInitial() {
        return new EligibilityViewModel();
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
