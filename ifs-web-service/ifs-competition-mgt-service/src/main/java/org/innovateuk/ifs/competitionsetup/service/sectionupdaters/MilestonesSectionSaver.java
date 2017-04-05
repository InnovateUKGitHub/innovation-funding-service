package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.MilestoneService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneTime;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

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
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;
        LinkedMap<String, MilestoneRowForm> milestoneEntries = updateMilestoneTimeForRequiredMilestones(milestonesForm.getMilestoneEntries());

        List<Error> errors = returnErrorsFoundOnSave(milestoneEntries, competition);
        if(!errors.isEmpty()) {
            competitionSetupMilestoneService.sortMilestones(milestonesForm);
            return serviceFailure(errors);
        }

        return serviceSuccess();
    }

    private List<Error> returnErrorsFoundOnSave(LinkedMap<String, MilestoneRowForm> milestoneEntries, CompetitionResource competition) {
        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competition.getId());
        Map<String, MilestoneRowForm> filteredMilestoneEntries = milestoneEntries;

        //If competition is already set up only allow to save of future milestones.
        if (TRUE.equals(competition.getSetupComplete())) {
            List<MilestoneType> futureTypes = milestones.stream()
                    .filter(milestoneResource -> milestoneResource.getDate() == null || LocalDateTime.now().isBefore(milestoneResource.getDate()))
                    .map(milestoneResource -> milestoneResource.getType())
                    .collect(Collectors.toList());

            filteredMilestoneEntries = CollectionFunctions.simpleFilter(milestoneEntries, (name, form) -> futureTypes.contains(form.getMilestoneType()));
        }

        List<Error> errors = competitionSetupMilestoneService.validateMilestoneDates(filteredMilestoneEntries);
        if(!errors.isEmpty()) {
            return errors;
        }

        ServiceResult<Void> result = competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, filteredMilestoneEntries, competition.getId());
        if(result.isFailure()) {
            return result.getErrors();
        }

        return Collections.emptyList();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) { return MilestonesForm.class.equals(clazz); }

    private LinkedMap<String,MilestoneRowForm> updateMilestoneTimeForRequiredMilestones(LinkedMap<String, MilestoneRowForm> milestoneEntries) {
	    milestoneEntries.forEach((s, milestoneRowForm) -> {
	        if(milestoneRowForm.isMiddayTime()) {
                milestoneRowForm.setTime(MilestoneTime.TWELVE_PM);
                milestoneEntries.put(s, milestoneRowForm);
            }
        });

	    return milestoneEntries;
    }

    protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> questionId) {
        List<Error> errors = updateMilestoneWithValueByFieldname(competitionResource, fieldName, value);
        if (!errors.isEmpty()) {
            return serviceFailure(errors);
        }
        return serviceSuccess();
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
        Integer day = null, month = null, year = null, hour = 0;
        LocalDateTime currentDate = milestone.getDate();

	    if(isTimeField(fieldName)) {
            if(null != currentDate) {
                day = currentDate.getDayOfMonth();
                month = currentDate.getMonthValue();
                year = currentDate.getYear();
                hour = MilestoneTime.valueOf(value).getHour();
            }
        } else {
            String[] dateParts = value.split("-");
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            year = Integer.parseInt(dateParts[2]);

            if(null != currentDate) {
                hour = milestone.getDate().getHour();
            }
        }

        if(!competitionSetupMilestoneService.isMilestoneDateValid(day, month, year)) {
            return asList(fieldError(fieldName, fieldName.toString(), "error.milestone.invalid"));
        }
        else {
            milestone.setDate(LocalDateTime.of(year, month, day, hour, 0));
        }

        return Collections.emptyList();
    }

    private boolean isTimeField(String fieldName) {
	    return fieldName.endsWith(".time");
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
