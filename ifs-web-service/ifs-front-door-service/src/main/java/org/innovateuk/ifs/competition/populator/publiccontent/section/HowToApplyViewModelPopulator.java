package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.HowToApplyViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content how to apply view model.
 */

@Service
public class HowToApplyViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<HowToApplyViewModel> {

    @Override
    protected HowToApplyViewModel createInitial() {
        return new HowToApplyViewModel();
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.HOW_TO_APPLY;
    }
}
