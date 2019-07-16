package org.innovateuk.ifs.management.publiccontent.modelpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.AbstractPublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentViewModelPopulator;
import org.innovateuk.ifs.management.publiccontent.viewmodel.section.SearchInformationViewModel;
import org.springframework.stereotype.Service;


@Service
public class SearchInformationViewModelPopulator extends AbstractPublicContentViewModelPopulator<SearchInformationViewModel> implements PublicContentViewModelPopulator<SearchInformationViewModel> {

    @Override
    protected SearchInformationViewModel createInitial() {
        return new SearchInformationViewModel();
    }

    @Override
    protected void populateSection(SearchInformationViewModel model, PublicContentResource publicContentResource, PublicContentSectionResource sectionResource) {
        //Nothing specific to add to the view model.
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SEARCH;
    }
}
