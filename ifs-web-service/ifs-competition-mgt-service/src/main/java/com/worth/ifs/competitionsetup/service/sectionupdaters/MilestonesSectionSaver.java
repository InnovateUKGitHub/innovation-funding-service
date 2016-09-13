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
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.Error.fieldError;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import java.util.Optional;

/**
 * Competition setup section saver for the milestones section.
 */
@Service
public class MilestonesSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

    private static Log LOG = LogFactory.getLog(MilestonesSectionSaver.class);

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
    public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> ObjectId) {
        return performAutoSaveField(competitionResource, fieldName, value);
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

    @Override
    protected List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) throws ParseException {
        switch (fieldName) {
            case "milestoneEntries[BRIEFING_EVENT].milestoneType":
                MilestoneResource milestone = new MilestoneResource();

            //return competitionSetupMilestoneService.updateMilestoneForCompetition(parseDate(value));
                //return null;
            break;
                //find matching milestone
                //build case matching
            default:
                return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
        }
        return errors;
    }

    private LocalDateTime parseDate(String value){
        try {
            String[] dateParts = value.split("-");
            return LocalDateTime.of(
                    Integer.parseInt(dateParts[2]),
                    Integer.parseInt(dateParts[1]),
                    Integer.parseInt(dateParts[0]),
                    0, 0, 0);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            //return asList(fieldError
            return null;
        }
    }
}
