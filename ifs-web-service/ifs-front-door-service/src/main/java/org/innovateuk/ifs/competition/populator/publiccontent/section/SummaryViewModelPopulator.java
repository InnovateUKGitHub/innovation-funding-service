package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.populator.publiccontent.AbstractPublicContentGroupViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.SummaryViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates a public content summary view model.
 */

@Service
public class SummaryViewModelPopulator extends AbstractPublicContentGroupViewModelPopulator<SummaryViewModel> {

    @Override
    protected SummaryViewModel createInitial() {
        return new SummaryViewModel();
    }

    @Override
    protected void populateSection(SummaryViewModel model, PublicContentItemResource publicContentItemResource, PublicContentSectionResource section, Boolean nonIFS) {
        PublicContentResource publicContentResource = publicContentItemResource.getPublicContentResource();
        model.setDescription(publicContentResource.getSummary());
        model.setFundingType(publicContentItemResource.getFundingType().getDisplayName());
        model.setProjectSize(publicContentResource.getProjectSize());
        super.populateSection(model, publicContentItemResource, section, nonIFS);
    }

    @Override
    public PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
