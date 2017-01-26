package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicContentMenuPopulator {

    @Autowired
    private PublicContentService publicContentService;

    @Autowired
    private CompetitionService competitionService;

    public ServiceResult<PublicContentMenuViewModel> populate(Long competitionId) {
        return publicContentService.getCompetitionById(competitionId).andOnSuccessReturn(publicContent -> {
            PublicContentMenuViewModel viewModel = new PublicContentMenuViewModel();
            viewModel.setPublishDate(publicContent.getPublishDate());
            viewModel.setSections(publicContent.getContentSections());
            viewModel.setCompetition(competitionService.getById(competitionId));
            return viewModel;
        });
    }
}
