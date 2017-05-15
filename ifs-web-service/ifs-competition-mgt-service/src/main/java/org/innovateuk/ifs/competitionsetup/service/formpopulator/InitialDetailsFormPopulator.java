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
import static java.util.Arrays.asList;

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

	private final static Long ALL_INNOVATION_AREAS = -1L;

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        List<InnovationAreaResource> allInnovationAreas = categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException();
	    InitialDetailsForm competitionSetupForm = new InitialDetailsForm();

		competitionSetupForm.setCompetitionTypeId(competitionResource.getCompetitionType());
		competitionSetupForm.setExecutiveUserId(competitionResource.getExecutive());

		competitionSetupForm.setInnovationSectorCategoryId(competitionResource.getInnovationSector());
		Set<Long> innovationAreaCategoryIds = competitionResource.getInnovationAreas();
		competitionSetupForm.setInnovationAreaCategoryIds(setInnovationAreas(innovationAreaCategoryIds, allInnovationAreas));
		competitionSetupForm.setInnovationAreaNamesFormatted(getFormattedInnovationAreaNames(innovationAreaCategoryIds, allInnovationAreas));
		competitionSetupForm.setLeadTechnologistUserId(competitionResource.getLeadTechnologist());

		if (competitionResource.getStartDate() != null) {
			competitionSetupForm.setOpeningDateDay(competitionResource.getStartDate().getDayOfMonth());
			competitionSetupForm.setOpeningDateMonth(competitionResource.getStartDate().getMonth().getValue());
			competitionSetupForm.setOpeningDateYear(competitionResource.getStartDate().getYear());
		}

		competitionSetupForm.setTitle(competitionResource.getName());

		return competitionSetupForm;
	}

    private List<Long> setInnovationAreas(Set<Long> innovationAreaCategoryIds, List<InnovationAreaResource> allInnovationAreas) {
	    if(innovationAreaCategoryIds.size() == allInnovationAreas.size()) {
	        return asList(ALL_INNOVATION_AREAS);
        }

        return innovationAreaCategoryIds.stream().collect(Collectors.toList());
    }

    private String getFormattedInnovationAreaNames(Set<Long> ids, List<InnovationAreaResource> allInnovationAreas) {
        if(ids.size() == allInnovationAreas.size()) {
            return "All";
        }
	    return categoryFormatter.format(ids, allInnovationAreas);
	}
}
