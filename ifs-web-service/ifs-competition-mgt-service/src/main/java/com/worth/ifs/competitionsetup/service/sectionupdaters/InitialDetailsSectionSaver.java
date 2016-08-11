package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.InitialDetailsForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Competition setup section saver for the initial details section.
 */
@Service
public class InitialDetailsSectionSaver implements CompetitionSetupSectionSaver {

	private static Log LOG = LogFactory.getLog(InitialDetailsSectionSaver.class);

	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private CompetitionSetupService competitionSetupService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
		InitialDetailsForm initialDetailsForm = (InitialDetailsForm) competitionSetupForm;

		Boolean isDiffCompType = competition.getCompetitionType() != initialDetailsForm.getCompetitionTypeId();

		competition.setName(initialDetailsForm.getTitle());
		competition.setExecutive(initialDetailsForm.getExecutiveUserId());

		try {
			LocalDateTime startDate = LocalDateTime.of(initialDetailsForm.getOpeningDateYear(),
					initialDetailsForm.getOpeningDateMonth(), initialDetailsForm.getOpeningDateDay(), 0, 0);
			competition.setStartDate(startDate);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			competition.setStartDate(null);
		}
		competition.setCompetitionType(initialDetailsForm.getCompetitionTypeId());
		competition.setLeadTechnologist(initialDetailsForm.getLeadTechnologistUserId());

		competition.setInnovationArea(initialDetailsForm.getInnovationAreaCategoryId());
		competition.setInnovationSector(initialDetailsForm.getInnovationSectorCategoryId());

		competitionService.update(competition);

        if(isDiffCompType) {
            competitionService.initApplicationFormByCompetitionType(competition.getId(), initialDetailsForm.getCompetitionTypeId());
        }
        return new ArrayList<>();
	}
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return InitialDetailsForm.class.equals(clazz);
	}

}
