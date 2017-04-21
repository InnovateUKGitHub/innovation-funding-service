package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Form populator for the initial details competition setup section.
 */
@Service
public class InitialDetailsFormPopulator implements CompetitionSetupFormPopulator {

	@Autowired
	private CategoryFormatter categoryFormatter;

	@Autowired
	private CategoryRestService categoryRestService;

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		InitialDetailsForm competitionSetupForm = new InitialDetailsForm();

		competitionSetupForm.setCompetitionTypeId(competitionResource.getCompetitionType());
		competitionSetupForm.setExecutiveUserId(competitionResource.getExecutive());

		competitionSetupForm.setInnovationSectorCategoryId(competitionResource.getInnovationSector());
		Set<Long> innovationAreaCategoryIds = competitionResource.getInnovationAreas();
		competitionSetupForm.setInnovationAreaCategoryIds(innovationAreaCategoryIds.stream().collect(Collectors.toList()));
		competitionSetupForm.setInnovationAreaNamesFormatted(getFormattedInnovationAreaNames(innovationAreaCategoryIds));
		competitionSetupForm.setLeadTechnologistUserId(competitionResource.getLeadTechnologist());

		if (competitionResource.getStartDate() != null) {
			competitionSetupForm.setOpeningDateDay(competitionResource.getStartDate().getDayOfMonth());
			competitionSetupForm.setOpeningDateMonth(competitionResource.getStartDate().getMonth().getValue());
			competitionSetupForm.setOpeningDateYear(competitionResource.getStartDate().getYear());
		}

		competitionSetupForm.setTitle(competitionResource.getName());

		return competitionSetupForm;
	}

	private String getFormattedInnovationAreaNames(Set<Long> ids) {
		List<InnovationAreaResource> allAreas = categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException();
		return categoryFormatter.format(ids, allAreas);
	}
}
