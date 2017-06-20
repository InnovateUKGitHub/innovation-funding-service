package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.SupportingInformationViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content supporting information view model.
 */

@Service
public class SupportingInformationViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<SupportingInformationViewModel> {

    @Override
    protected SupportingInformationViewModel createInitial() {
        return new SupportingInformationViewModel();
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.SUPPORTING_INFORMATION;
    }
}
