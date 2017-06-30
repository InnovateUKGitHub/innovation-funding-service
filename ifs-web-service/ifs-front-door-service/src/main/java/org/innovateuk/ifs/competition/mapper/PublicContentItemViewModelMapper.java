package org.innovateuk.ifs.competition.mapper;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.viewmodel.PublicContentItemViewModel;
import org.springframework.stereotype.Component;

@Component
public class PublicContentItemViewModelMapper {

    public PublicContentItemViewModel mapToViewModel(PublicContentItemResource publicContentItemResource) {
        PublicContentItemViewModel publicContentItemViewModel = new PublicContentItemViewModel();

        publicContentItemViewModel.setCompetitionTitle(publicContentItemResource.getCompetitionTitle());
        publicContentItemViewModel.setCompetitionCloseDate(publicContentItemResource.getCompetitionCloseDate());
        publicContentItemViewModel.setCompetitionOpenDate(publicContentItemResource.getCompetitionOpenDate());

        PublicContentResource publicContentResource = publicContentItemResource.getPublicContentResource();
        publicContentItemViewModel.setEligibilitySummary(publicContentResource.getEligibilitySummary());
        publicContentItemViewModel.setShortDescription(publicContentResource.getShortDescription());
        publicContentItemViewModel.setCompetitionId(publicContentResource.getCompetitionId());

        return publicContentItemViewModel;
    }

}
