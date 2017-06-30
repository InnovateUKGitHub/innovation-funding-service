package org.innovateuk.ifs.publiccontent.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
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

    @Autowired
    private CompetitionService competitionService;

    public PublicContentMenuViewModel populate(Long competitionId, String webBaseUrl) {
        PublicContentResource publicContent = publicContentService.getCompetitionById(competitionId);
        PublicContentMenuViewModel viewModel = new PublicContentMenuViewModel();
        viewModel.setPublishDate(publicContent.getPublishDate());
        viewModel.setSections(publicContent.getContentSections());
        viewModel.setCompetition(competitionService.getById(competitionId));
        viewModel.setInviteOnly(publicContent.getInviteOnly());
        viewModel.setWebBaseUrl(webBaseUrl);
        return viewModel;
    }
}
