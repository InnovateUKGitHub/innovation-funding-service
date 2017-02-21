package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.ScopeViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content supporting information view model.
 */

@Service
public class SupportingInformationViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<ScopeViewModel> {

    @Override
    protected ScopeViewModel createInitial() {
        return new ScopeViewModel();
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.SUPPORTING_INFORMATION;
    }
}
