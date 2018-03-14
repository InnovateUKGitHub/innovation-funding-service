package org.innovateuk.ifs.publiccontent.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populates a public content menu view model.
 */
@Component
public class PublicContentMenuPopulator {

    @Autowired
    private PublicContentService publicContentService;

    public PublicContentMenuViewModel populate(CompetitionResource competition, String webBaseUrl) {
        PublicContentResource publicContent = publicContentService.getCompetitionById(competition.getId());
        PublicContentMenuViewModel viewModel = new PublicContentMenuViewModel();
        viewModel.setPublishDate(publicContent.getPublishDate());
        viewModel.setSections(publicContent.getContentSections());
        viewModel.setCompetition(competition);
        viewModel.setInviteOnly(publicContent.getInviteOnly());
        viewModel.setWebBaseUrl(webBaseUrl);
        return viewModel;
    }
}
