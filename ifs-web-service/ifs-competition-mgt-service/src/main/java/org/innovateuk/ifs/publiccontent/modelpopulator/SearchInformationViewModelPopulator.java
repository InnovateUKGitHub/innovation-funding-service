package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.viewmodel.SearchInformationViewModel;
import org.springframework.stereotype.Service;


@Service
public class SearchInformationViewModelPopulator extends AbstractPublicContentViewModelPopulator<SearchInformationViewModel> implements PublicContentViewModelPopulator<SearchInformationViewModel> {

    @Override
    protected SearchInformationViewModel createInitial() {
        return new SearchInformationViewModel();
    }

    @Override
    protected void populateSection(SearchInformationViewModel model, PublicContentResource publicContentResource) {
        //Nothing specific to add to the view model.
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SEARCH;
    }
}
