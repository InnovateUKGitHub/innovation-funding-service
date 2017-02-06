package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.viewmodel.SummaryViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content summary view model.
 */

@Service
public class SummaryViewModelPopulator extends AbstractPublicContentViewModelPopulator<SummaryViewModel> implements PublicContentViewModelPopulator<SummaryViewModel> {

    @Override
    protected SummaryViewModel createInitial() {
        return new SummaryViewModel();
    }

    @Override
    protected void populateSection(SummaryViewModel model, PublicContentResource publicContentResource) {
        //Nothing specific to add to the view model.
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
