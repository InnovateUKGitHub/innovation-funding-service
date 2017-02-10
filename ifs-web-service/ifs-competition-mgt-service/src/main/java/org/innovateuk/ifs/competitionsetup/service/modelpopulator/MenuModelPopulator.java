package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * populates the model for the competition setup menu page section.
 */
@Service
public class MenuModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	protected PublicContentService publicContentService;
	
	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.HOME;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {

		PublicContentResource publicContent = publicContentService.getCompetitionById(competitionResource.getId());
		model.addAttribute("publishDate", publicContent.getPublishDate());
		model.addAttribute("isPublicContentPublished", isPublicContentPublished(publicContent));
	}

	private boolean isPublicContentPublished(PublicContentResource publicContent) {
		return null != publicContent.getPublishDate();
	}

}
