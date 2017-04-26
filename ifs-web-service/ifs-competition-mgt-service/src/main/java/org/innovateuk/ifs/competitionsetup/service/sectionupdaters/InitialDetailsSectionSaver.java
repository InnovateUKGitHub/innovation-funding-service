package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Competition setup section saver for the initial details section.
 */
@Service
public class InitialDetailsSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

	private static Log LOG = LogFactory.getLog(InitialDetailsSectionSaver.class);
    public final static String OPENINGDATE_FIELDNAME = "openingDate";

	@Autowired
	private CompetitionService competitionService;

    @Autowired
    private MilestoneRestService milestoneRestService;

	@Autowired
	private CompetitionSetupMilestoneService competitionSetupMilestoneService;

	@Autowired
	private CategoryRestService categoryRestService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

		InitialDetailsForm initialDetailsForm = (InitialDetailsForm) competitionSetupForm;
        if (!competition.isSetupAndAfterNotifications()) {
            competition.setExecutive(initialDetailsForm.getExecutiveUserId());
            competition.setLeadTechnologist(initialDetailsForm.getLeadTechnologistUserId());

            if (!Boolean.TRUE.equals(competition.getSetupComplete())) {

                competition.setName(initialDetailsForm.getTitle());

                if (shouldTryToSaveStartDate(initialDetailsForm)) {
                    ZonedDateTime startDate = initialDetailsForm.getOpeningDate();
                    competition.setStartDate(startDate);

                    List<Error> errors = saveOpeningDateAsMilestone(startDate, competition.getId(), initialDetailsForm.isMarkAsCompleteAction());
                    if (!errors.isEmpty()) {
                        return serviceFailure(errors);
                    }
                }


                competition.setCompetitionType(initialDetailsForm.getCompetitionTypeId());
                competition.setInnovationSector(initialDetailsForm.getInnovationSectorCategoryId());

                if (competition.getInnovationSector() != null) {
                    List<InnovationAreaResource> children = categoryRestService.getInnovationAreasBySector(competition.getInnovationSector()).getSuccessObjectOrThrowException();
                    List<CategoryResource> matchingChildren =
                            children.stream().filter(child -> initialDetailsForm.getInnovationAreaCategoryIds().contains(child.getId())).collect(Collectors.toList());

                    if (matchingChildren.isEmpty() && initialDetailsForm.isMarkAsCompleteAction()) {
                        return serviceFailure(asList(fieldError("innovationAreaCategoryIds",
                                initialDetailsForm.getInnovationAreaCategoryIds(),
                                "competition.setup.innovation.area.must.be.selected",
                                singletonList(children.stream().map(child -> child.getName()).collect(Collectors.joining(", "))))));
                    }
                }
                competition.setInnovationAreas(initialDetailsForm.getInnovationAreaCategoryIds()
                                                                        .stream()
                                                                        .filter(Objects::nonNull)
                                                                        .collect(Collectors.toSet()));
            }
            return competitionService.update(competition).andOnSuccess(() -> {
                if (initialDetailsForm.isMarkAsCompleteAction() && Boolean.FALSE.equals(competition.getSetupComplete())) {
                    return competitionService.initApplicationFormByCompetitionType(competition.getId(), initialDetailsForm.getCompetitionTypeId());
                } else {
                    return serviceSuccess();
                }
            });
        } else {
            return serviceFailure(new Error("Initial details section is not editable after notifications", BAD_REQUEST));
        }
   }

   private boolean shouldTryToSaveStartDate(InitialDetailsForm initialDetailsForm) {
       return initialDetailsForm.isMarkAsCompleteAction() ||
               (initialDetailsForm.getOpeningDateYear() != null &&
               initialDetailsForm.getOpeningDateMonth() != null &&
               initialDetailsForm.getOpeningDateDay() != null);
   }

	private List<Error> validateOpeningDate(ZonedDateTime openingDate) {
	    if(openingDate.getYear() > 9999) {
            return asList(fieldError(OPENINGDATE_FIELDNAME, openingDate.toString(), "validation.initialdetailsform.openingdateyear.range"));
        }

        if (openingDate.isBefore(ZonedDateTime.now())) {
            return asList(fieldError(OPENINGDATE_FIELDNAME, openingDate.toString(), "competition.setup.opening.date.not.in.future"));
        }

        return Collections.emptyList();
    }

	private List<Error> saveOpeningDateAsMilestone(ZonedDateTime openingDate, Long competitionId, boolean isMarkAsCompleteAction) {
		if (isMarkAsCompleteAction) {
			List<Error> errors = validateOpeningDate(openingDate);
			if (!errors.isEmpty()) {
				return errors;
			}
		}

	    MilestoneRowForm milestoneEntry = new MilestoneRowForm(MilestoneType.OPEN_DATE, openingDate);


        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccessObjectOrThrowException();
        if(milestones.isEmpty()) {
            milestones = competitionSetupMilestoneService.createMilestonesForCompetition(competitionId).getSuccessObjectOrThrowException();
        }
        milestones.sort(Comparator.comparing(MilestoneResource::getType));

		LinkedMap<String, MilestoneRowForm> milestoneEntryMap = new LinkedMap<>();
		milestoneEntryMap.put(MilestoneType.OPEN_DATE.name(), milestoneEntry);

        ServiceResult<Void> result = competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, milestoneEntryMap, competitionId);
        if(result.isFailure()) {
            return result.getErrors();
        }
		return emptyList();
	}

    @Override
    protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> questionId) {
        if("openingDate".equals(fieldName)) {
            try {
                String[] dateParts = value.split("-");
                ZonedDateTime startDate = TimeZoneUtil.fromUkTimeZone(
                        Integer.parseInt(dateParts[2]),
                        Integer.parseInt(dateParts[1]),
                        Integer.parseInt(dateParts[0]));
                competitionResource.setStartDate(startDate);


                List<Error> errors = saveOpeningDateAsMilestone(startDate, competitionResource.getId(), false);
                if(!errors.isEmpty()) {
                    return serviceFailure(errors);
                } else {
                    return competitionService.update(competitionResource);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
                return serviceFailure(fieldError(OPENINGDATE_FIELDNAME, null, "competition.setup.opening.date.not.able.to.save"));
            }
        } else if( fieldName.equals("autosaveInnovationAreaIds")) {
            processInnovationAreas(value, competitionResource);
            return competitionService.update(competitionResource);
        }
        return super.handleIrregularAutosaveCase(competitionResource, fieldName, value, questionId);
    }

    private void processInnovationAreas(String inputValue, CompetitionResource competitionResource) {
        List<String> valueList = Arrays.asList(inputValue.split("\\s*,\\s*"));
        Set<Long> valueSet = valueList.stream().map(Long::parseLong).collect(Collectors.toSet());
        competitionResource.setInnovationAreas(valueSet);

    }

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return InitialDetailsForm.class.equals(clazz);
	}
}
