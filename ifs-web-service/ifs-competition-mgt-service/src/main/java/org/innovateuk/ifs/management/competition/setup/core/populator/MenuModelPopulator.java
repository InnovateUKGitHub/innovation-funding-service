package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.MenuViewModel;
import org.innovateuk.ifs.management.publiccontent.service.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapKeyAndValue;

/**
 * populates the model for the competition setup menu page section.
 */
@Service
public class MenuModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	protected PublicContentService publicContentService;

	@Autowired
	private CompetitionSetupRestService competitionSetupRestService;

	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.HOME;
	}

	@Override
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		PublicContentResource publicContent = publicContentService.getCompetitionById(competitionResource.getId());
		Map<CompetitionSetupSection, Optional<Boolean>> statuses = competitionSetupRestService.getSectionStatuses(competitionResource.getId()).getSuccess();
		Map<CompetitionSetupSection, Boolean> statusesWithValues = simpleMapKeyAndValue(statuses, key -> key, status -> status.orElse(false));
		return new MenuViewModel(generalViewModel, publicContent.getPublishDate(), isPublicContentPublished(publicContent), statusesWithValues);
	}

	private boolean isPublicContentPublished(PublicContentResource publicContent) {
		return null != publicContent.getPublishDate();
	}
}
