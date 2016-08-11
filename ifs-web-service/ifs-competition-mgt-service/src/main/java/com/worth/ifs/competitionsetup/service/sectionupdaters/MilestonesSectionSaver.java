package com.worth.ifs.competitionsetup.service.sectionupdaters;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

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
        List<MilestonesFormEntry> milestoneEntries = milestonesForm.getMilestonesFormEntryList();
        List<MilestoneResource> milestones = milestoneService.getAllDatesByCompetitionId(competition.getId());
        milestones.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));

        return competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, milestoneEntries, competition.getId());
    }



    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) { return MilestonesForm.class.equals(clazz); }
}
