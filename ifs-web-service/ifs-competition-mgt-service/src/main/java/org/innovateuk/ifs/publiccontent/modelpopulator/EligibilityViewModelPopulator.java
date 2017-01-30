package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.viewmodel.EligibilityViewModel;
import org.springframework.stereotype.Service;


@Service
public class EligibilityViewModelPopulator extends AbstractPublicContentViewModelPopulator<EligibilityViewModel> implements PublicContentViewModelPopulator<EligibilityViewModel> {

    @Override
    protected EligibilityViewModel createInitial() {
        return new EligibilityViewModel();
    }

    @Override
    protected void populateSection(EligibilityViewModel model, PublicContentResource publicContentResource) {
        //Nothing specific to add to the view model.
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
