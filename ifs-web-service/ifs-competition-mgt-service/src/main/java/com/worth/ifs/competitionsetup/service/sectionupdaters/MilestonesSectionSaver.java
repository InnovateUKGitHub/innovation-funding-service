package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.model.MilestoneEntry;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Competition setup section saver for the milestones section.
 */
@Service
public class MilestonesSectionSaver implements CompetitionSetupSectionSaver {

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.MILESTONES;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;
        LinkedMap<String, MilestoneEntry> milestoneEntries = milestonesForm.getMilestoneEntries();

        List<Error> errors = returnErrorsFoundOnSave(milestoneEntries, competition.getId());
        if(!errors.isEmpty()) {
            competitionSetupMilestoneService.sortMilestones(milestonesForm);
            return errors;
        }

        return Collections.emptyList();
    }

    @Override
    public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> objectId) {
        return Collections.emptyList();
    }

    private List<Error> returnErrorsFoundOnSave(LinkedMap<String, MilestoneEntry> milestoneEntries, Long competitionId){
        List<MilestoneResource> milestones = milestoneService.getAllDatesByCompetitionId(competitionId);

        List<Error> errors = competitionSetupMilestoneService.validateMilestoneDates(milestoneEntries);

        if(!errors.isEmpty()) {
            return errors;
        }

        return competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, milestoneEntries, competitionId);
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) { return MilestonesForm.class.equals(clazz); }
}
