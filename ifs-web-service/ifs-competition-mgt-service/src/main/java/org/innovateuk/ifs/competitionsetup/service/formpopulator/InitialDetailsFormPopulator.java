package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.utils.CompetitionUtils;
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

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        final List<InnovationAreaResource> allInnovationAreas = categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException();
	    InitialDetailsForm competitionSetupForm = new InitialDetailsForm();

		competitionSetupForm.setCompetitionTypeId(competitionResource.getCompetitionType());
		competitionSetupForm.setExecutiveUserId(competitionResource.getExecutive());

		competitionSetupForm.setInnovationSectorCategoryId(competitionResource.getInnovationSector());
		Set<Long> innovationAreaCategoryIds = competitionResource.getInnovationAreas();
		competitionSetupForm.setInnovationAreaCategoryIds(setInnovationAreas(innovationAreaCategoryIds, allInnovationAreas));
		competitionSetupForm.setInnovationAreaNamesFormatted(getFormattedInnovationAreaNames(innovationAreaCategoryIds, allInnovationAreas));
		competitionSetupForm.setInnovationLeadUserId(competitionResource.getLeadTechnologist());

		if (competitionResource.getStartDate() != null) {
			competitionSetupForm.setOpeningDateDay(competitionResource.getStartDate().getDayOfMonth());
			competitionSetupForm.setOpeningDateMonth(competitionResource.getStartDate().getMonth().getValue());
			competitionSetupForm.setOpeningDateYear(competitionResource.getStartDate().getYear());
		}

		competitionSetupForm.setTitle(competitionResource.getName());

		return competitionSetupForm;
	}

    private List<Long> setInnovationAreas(Set<Long> innovationAreaCategoryIds, List<InnovationAreaResource> allInnovationAreas) {
	    if(innovationAreasAreMatching(innovationAreaCategoryIds, allInnovationAreas)) {
	        return asList(CompetitionUtils.ALL_INNOVATION_AREAS);
        }

        return innovationAreaCategoryIds.stream().collect(Collectors.toList());
    }

    private String getFormattedInnovationAreaNames(Set<Long> ids, List<InnovationAreaResource> allInnovationAreas) {
        if(innovationAreasAreMatching(ids, allInnovationAreas)) {
            return "All";
        }
	    return categoryFormatter.format(ids, allInnovationAreas);
	}

    private boolean innovationAreasAreMatching(Set<Long> innovationAreaCategoryIds, List<InnovationAreaResource> allInnovationAreas) {
        return allInnovationAreas.stream().allMatch(innovationAreaResource -> innovationAreaCategoryIds.contains(innovationAreaResource.getId()))
                && innovationAreaCategoryIds.size() == allInnovationAreas.size();
    }
}
