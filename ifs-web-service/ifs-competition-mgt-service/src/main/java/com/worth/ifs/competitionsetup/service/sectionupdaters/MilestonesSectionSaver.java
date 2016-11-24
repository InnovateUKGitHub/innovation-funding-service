package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.worth.ifs.commons.error.Error.fieldError;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

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
        LinkedMap<String, MilestoneViewModel> milestoneEntries = milestonesForm.getMilestoneEntries();

        List<Error> errors = returnErrorsFoundOnSave(milestoneEntries, competition.getId());
        if(!errors.isEmpty()) {
            competitionSetupMilestoneService.sortMilestones(milestonesForm);
            return errors;
        }

        return Collections.emptyList();
    }

    @Override
    public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> ObjectId) {
        return updateMilestoneWithValueByFieldname(competitionResource, fieldName, value);
    }

    private List<Error> returnErrorsFoundOnSave(LinkedMap<String, MilestoneViewModel> milestoneEntries, Long competitionId){
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competitionId);

        List<Error> errors = competitionSetupMilestoneService.validateMilestoneDates(milestoneEntries);

        if(!errors.isEmpty()) {
            return errors;
        }

        return competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, milestoneEntries, competitionId);
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) { return MilestonesForm.class.equals(clazz); }

    @Override
    protected List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) {
      return  Collections.emptyList();
    }

    private List<Error> updateMilestoneWithValueByFieldname(CompetitionResource competitionResource, String fieldName, String value) {
        List<Error> errors = new ArrayList<>();
        try{
            MilestoneResource milestone = milestoneService.getMilestoneByTypeAndCompetitionId(
                    MilestoneType.valueOf(getMilestoneTypeFromFieldName(fieldName)), competitionResource.getId());

            errors.addAll(validateMilestoneDateOnAutosave(milestone, fieldName, value));

            if(!errors.isEmpty()) {
                return errors;
            }
            milestoneService.updateMilestone(milestone);
        }catch(Exception ex){
            LOG.error(ex.getMessage());
            return makeErrorList();
        }
        return  errors;
    }

    private List<Error> validateMilestoneDateOnAutosave(MilestoneResource milestone, String fieldName, String value) {
        String[] dateParts = value.split("-");
        Integer day = Integer.parseInt(dateParts[0]);
        Integer month = Integer.parseInt(dateParts[1]);
        Integer year = Integer.parseInt(dateParts[2]);

        if(day == null || month == null || year == null || !competitionSetupMilestoneService.isMilestoneDateValid(day, month, year)) {

            return asList(fieldError(fieldName, fieldName.toString(), "error.milestone.invalid"));
        }
        else {
            milestone.setDate(LocalDateTime.of(year, month, day, 0,0));
        }

        return Collections.emptyList();
    }

    private List<Error> makeErrorList()  {
        return asList(fieldError("", null, "error.milestone.autosave.unable"));
    }

    private String getMilestoneTypeFromFieldName(String fieldName) {
        Pattern typePattern = Pattern.compile("\\[(.*?)\\]");
        Matcher typeMatcher = typePattern.matcher(fieldName);
        typeMatcher.find();
        return typeMatcher.group(1);
    }
}
