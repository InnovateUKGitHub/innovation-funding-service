package org.innovateuk.ifs.publiccontent.modelpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.modelpopulator.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.publiccontent.viewmodel.section.ScopeViewModel;
import org.springframework.stereotype.Service;


@Service
public class ScopeViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<ScopeViewModel> implements PublicContentViewModelPopulator<ScopeViewModel> {

    @Override
    protected ScopeViewModel createInitial() {
        return new ScopeViewModel();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SCOPE;
    }
}
