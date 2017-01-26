package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;
import org.innovateuk.ifs.publiccontent.viewmodel.SearchViewModel;
import org.springframework.stereotype.Service;


@Service
public class PublicContentSearchModelPopulator extends AbstractPublicContentViewModelPopulator<SearchViewModel> implements PublicContentViewModelPopulator<SearchViewModel> {

    @Override
    protected SearchViewModel createInitial() {
        return new SearchViewModel();
    }

    @Override
    protected void populateSection(SearchViewModel model, PublicContentResource publicContentResource) {
        //Nothing specific to add to the view model.
    }

    @Override
    protected PublicContentSection getType() {
        return PublicContentSection.SEARCH;
    }
}
