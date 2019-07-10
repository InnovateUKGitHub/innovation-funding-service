package org.innovateuk.ifs.management.publiccontent.modelpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.viewmodel.section.SummaryViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content summary view model.
 */

@Service
public class SummaryViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<SummaryViewModel> implements PublicContentViewModelPopulator<SummaryViewModel> {

    @Override
    protected SummaryViewModel createInitial() {
        return new SummaryViewModel();
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
