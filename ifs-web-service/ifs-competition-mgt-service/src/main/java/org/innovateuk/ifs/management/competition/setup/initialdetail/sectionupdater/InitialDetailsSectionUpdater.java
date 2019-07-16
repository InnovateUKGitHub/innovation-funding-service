package org.innovateuk.ifs.management.competition.setup.initialdetail.sectionupdater;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionSpecialSectors;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionUtils;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Competition setup section saver for the initial details section.
 */
@Service
public class InitialDetailsSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    public final static String OPENINGDATE_FIELDNAME = "openingDate";
	private static Log LOG = LogFactory.getLog(InitialDetailsSectionUpdater.class);

	@Autowired
    private CompetitionSetupService competitionSetupService;

	@Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

	@Autowired
	private CompetitionSetupMilestoneService competitionSetupMilestoneService;

	@Autowired
	private CategoryRestService categoryRestService;

	@Autowired
    private UserService userService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

    @Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		InitialDetailsForm initialDetailsForm = (InitialDetailsForm) competitionSetupForm;
        if (!competition.isSetupAndAfterNotifications()) {
            List<Error> errors = saveAssignedUsers(competition, initialDetailsForm);

            if (errors.isEmpty() && !competition.getSetupComplete()) {
                errors = doSetupComplete(competition, initialDetailsForm);
            }

            if (!errors.isEmpty()) {
                return serviceFailure(errors);
            }

            return competitionSetupRestService.updateCompetitionInitialDetails(competition).toServiceResult().andOnSuccess(() -> {
                if (initialDetailsForm.isMarkAsCompleteAction() && applicationFormHasNotBeenInitialised(competition)) {
                    return competitionSetupRestService.initApplicationForm(competition.getId(), initialDetailsForm.getCompetitionTypeId()).toServiceResult();
                } else {
                    return serviceSuccess();
                }
            });
        } else {
            return serviceFailure(new Error("Initial details section is not editable after notifications", BAD_REQUEST));
        }
    }

    private boolean applicationFormHasNotBeenInitialised(CompetitionResource competition) {
        return !competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId());
    }

    private List<Error> doSetupComplete(final CompetitionResource competition, final InitialDetailsForm initialDetailsForm) {
        List<Error> errors = new ArrayList<>();

        competition.setName(initialDetailsForm.getTitle());
        competition.setCompetitionType(initialDetailsForm.getCompetitionTypeId());
        competition.setInnovationSector(initialDetailsForm.getInnovationSectorCategoryId());
        competition.setStateAid(initialDetailsForm.getStateAid());
        competition.setFundingType(initialDetailsForm.getFundingType());

        errors.addAll(attemptOpeningMilestoneSave(initialDetailsForm, competition));
        errors.addAll(attemptAddingInnovationAreasToCompetition(initialDetailsForm, competition));

        return errors;
    }

    private List<Error> attemptOpeningMilestoneSave(InitialDetailsForm initialDetailsForm, CompetitionResource competition) {
        List<Error> errors = new ArrayList<>();

        if (shouldTryToSaveStartDate(initialDetailsForm)) {
            ZonedDateTime startDate = initialDetailsForm.getOpeningDate();
            competition.setStartDate(startDate);

            errors.addAll(saveOpeningDateAsMilestone(startDate, competition.getId(), initialDetailsForm.isMarkAsCompleteAction()));
        }

        return errors;
    }

    private List<Error> attemptAddingInnovationAreasToCompetition(InitialDetailsForm initialDetailsForm, CompetitionResource competition) {
        List<Error> errors = new ArrayList<>();

        List<Long> innovationAreas = initialDetailsForm.getInnovationAreaCategoryIds();

        if (competition.getInnovationSector() != null) {
            List<InnovationAreaResource> allInnovationAreas = categoryRestService.getInnovationAreasExcludingNone().getSuccess();
            List<Long> allInnovationAreasIds = getAllInnovationAreaIdsExcludingNone(allInnovationAreas).collect(Collectors.toList());
            List<Long> newInnovationAreaIds = initialDetailsForm.getInnovationAreaCategoryIds();

            if(CompetitionSpecialSectors.isOpenSector().test(competition.getInnovationSector())
                    && newInnovationAreaIds.contains(CompetitionUtils.ALL_INNOVATION_AREAS)) {
                innovationAreas = allInnovationAreasIds;
            }

            errors.addAll(checkInnovationAreaData(competition, allInnovationAreas, allInnovationAreasIds,
                    newInnovationAreaIds, initialDetailsForm.isMarkAsCompleteAction()));
        }

        competition.setInnovationAreas(innovationAreas.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        return errors;
    }

    private List<Error> checkInnovationAreaData(CompetitionResource competition,
                                                List<InnovationAreaResource> allInnovationAreas,
                                                List<Long> allInnovationAreasIds,
                                                List<Long> innovationAreaIds, boolean isMarkAsComplete) {
        if(CompetitionSpecialSectors.isOpenSector().test(competition.getInnovationSector())) {
            if(!innovationAreaIds.contains(CompetitionUtils.ALL_INNOVATION_AREAS)) {
                boolean foundNotMatchingId = innovationAreaIds.stream().anyMatch(areaId -> !allInnovationAreasIds.contains(areaId));

                if (foundNotMatchingId && isMarkAsComplete) {
                    return buildInnovationError(innovationAreaIds, allInnovationAreas);
                }
            }
        } else {
            List<InnovationAreaResource> children = categoryRestService.getInnovationAreasBySector(competition.getInnovationSector()).getSuccess();
            List<Long> childrenIds = children.stream().map(InnovationAreaResource::getId).collect(Collectors.toList());

            boolean foundNotMatchingId = innovationAreaIds.stream().anyMatch(areaId -> !childrenIds.contains(areaId));

            if (foundNotMatchingId && isMarkAsComplete) {
                return buildInnovationError(innovationAreaIds, children);
            }
        }

        return emptyList();
    }

    private List<Error> buildInnovationError(List<Long> innovationAreaCategoryIds, List<InnovationAreaResource> allInnovationAreas) {
        return asList(fieldError("innovationAreaCategoryIds",
                innovationAreaCategoryIds,
                "competition.setup.innovation.area.must.be.selected",
                singletonList(allInnovationAreas.stream().map(child -> child.getName()).collect(Collectors.joining(", ")))));
    }

    private Stream<Long> getAllInnovationAreaIdsExcludingNone(List<InnovationAreaResource> allInnovationAreas) {
        return allInnovationAreas.stream().map(InnovationAreaResource::getId);
    }

    private List<Error> saveAssignedUsers(final CompetitionResource competition, InitialDetailsForm initialDetailsForm) {
        if (userService.existsAndHasRole(initialDetailsForm.getExecutiveUserId(), COMP_ADMIN)) {
            competition.setExecutive(initialDetailsForm.getExecutiveUserId());
        } else if (initialDetailsForm.getExecutiveUserId() != null) {
            return asList(fieldError("executiveUserId",
                    initialDetailsForm.getExecutiveUserId(),
                    "competition.setup.invalid.comp.exec"));
        }

        if (userService.existsAndHasRole(initialDetailsForm.getInnovationLeadUserId(), INNOVATION_LEAD)) {
            competition.setLeadTechnologist(initialDetailsForm.getInnovationLeadUserId());
        } else if (initialDetailsForm.getInnovationLeadUserId() != null) {
            return asList(fieldError("innovationLeadUserId",
                    initialDetailsForm.getInnovationLeadUserId(),
                    "competition.setup.invalid.comp.technologist"));
        }

        return emptyList();
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

        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        if(milestones.isEmpty()) {
            milestones = competitionSetupMilestoneService.createMilestonesForIFSCompetition(competitionId).getSuccess();
        }
        milestones.sort(Comparator.comparing(MilestoneResource::getType));

		LinkedMap<String, GenericMilestoneRowForm> milestoneEntryMap = new LinkedMap<>();
		milestoneEntryMap.put(MilestoneType.OPEN_DATE.name(), milestoneEntry);

        ServiceResult<Void> result = competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, milestoneEntryMap, competitionId);
        if(result.isFailure()) {
            return result.getErrors();
        }
		return emptyList();
	}

    private ZonedDateTime parseDate(String value) {
        String[] dateParts = value.split("-");
        ZonedDateTime startDate = TimeZoneUtil.fromUkTimeZone(
                Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[0]));

        return startDate;
    }

    private Set<Long> parseInnovationAreaIds(String commaSeparatedIds) {
        List<String> valueList = Arrays.asList(commaSeparatedIds.split("\\s*,\\s*"));
        Set<Long> valueSet = valueList.stream().map(Long::parseLong).collect(Collectors.toSet());

        return valueSet;
    }

    private Set<Long> getAllInnovationAreaIdsExcludingNone() {
        List<InnovationAreaResource> allInnovationAreas = categoryRestService.getInnovationAreasExcludingNone().getSuccess();
        return getAllInnovationAreaIdsExcludingNone(allInnovationAreas).collect(Collectors.toSet());
    }

    private void processInnovationAreas(String commaSeparatedIds, CompetitionResource competitionResource) {
        Set<Long> innovationAreaIds = parseInnovationAreaIds(commaSeparatedIds);

        boolean allInnovationAreaIsSelected = innovationAreaIds.contains(CompetitionUtils.ALL_INNOVATION_AREAS);

        if(allInnovationAreaIsSelected) {
            innovationAreaIds = getAllInnovationAreaIdsExcludingNone();
        }

        competitionResource.setInnovationAreas(innovationAreaIds);
    }

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return InitialDetailsForm.class.equals(clazz);
	}
}