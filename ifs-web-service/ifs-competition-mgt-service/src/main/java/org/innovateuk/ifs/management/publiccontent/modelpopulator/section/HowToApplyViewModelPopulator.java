package org.innovateuk.ifs.management.publiccontent.modelpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.viewmodel.section.HowToApplyViewModel;
import org.springframework.stereotype.Service;


@Service
public class HowToApplyViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<HowToApplyViewModel> implements PublicContentViewModelPopulator<HowToApplyViewModel> {

    @Override
    protected HowToApplyViewModel createInitial() {
        return new HowToApplyViewModel();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.HOW_TO_APPLY;
    }
}
