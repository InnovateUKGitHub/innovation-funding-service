package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicSectionContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.ScopeViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content eligibility view model.
 */

@Service
public class ScopeViewModelPopulator extends AbstractPublicSectionContentGroupViewModelPopulator<ScopeViewModel> {

    @Override
    protected ScopeViewModel createInitial() {
        return new ScopeViewModel();
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.SCOPE;
    }
}
