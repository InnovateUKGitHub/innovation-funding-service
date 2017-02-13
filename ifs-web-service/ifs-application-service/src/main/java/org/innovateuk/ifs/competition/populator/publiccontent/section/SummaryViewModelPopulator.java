package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.PublicSectionContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.SummaryViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content summary view model.
 */

@Service
public class SummaryViewModelPopulator extends PublicSectionContentGroupViewModelPopulator<SummaryViewModel> {

    @Override
    protected SummaryViewModel createInitial() {
        return new SummaryViewModel();
    }

    @Override
    protected void populateSection(SummaryViewModel model, PublicContentResource publicContentResource, PublicContentSectionResource section) {
        model.setDescription(publicContentResource.getSummary());
        model.setFundingType(publicContentResource.getFundingType().getDisplayName());
        model.setProjectSize(publicContentResource.getProjectSize());

        super.populateSection(model, publicContentResource, section);
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
