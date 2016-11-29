package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.InitialDetailsForm;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import com.worth.ifs.controller.ValidationHandler;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.Error.fieldError;
import static java.util.Collections.singletonList;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

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
    private MilestoneService milestoneService;

	@Autowired
	private CompetitionSetupMilestoneService competitionSetupMilestoneService;

	@Autowired
	private CategoryService categoryService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
		InitialDetailsForm initialDetailsForm = (InitialDetailsForm) competitionSetupForm;

		competition.setName(initialDetailsForm.getTitle());
		competition.setExecutive(initialDetailsForm.getExecutiveUserId());

		try {
			LocalDateTime startDate = LocalDateTime.of(initialDetailsForm.getOpeningDateYear(),
					initialDetailsForm.getOpeningDateMonth(), initialDetailsForm.getOpeningDateDay(), 0, 0);
			competition.setStartDate(startDate);

            List<Error> errors = saveOpeningDateAsMilestone(startDate, competition.getId());
            if(!errors.isEmpty()) {
                return errors;
            }

		} catch (Exception e) {
			LOG.error(e.getMessage());

            return asList(fieldError(OPENINGDATE_FIELDNAME, null, "competition.setup.opening.date.not.able.to.save"));
		}

		competition.setCompetitionType(initialDetailsForm.getCompetitionTypeId());
		competition.setLeadTechnologist(initialDetailsForm.getLeadTechnologistUserId());

		competition.setInnovationSector(initialDetailsForm.getInnovationSectorCategoryId());

		List<CategoryResource> children = categoryService.getCategoryByParentId(competition.getInnovationSector());
		List<CategoryResource> matchingChild =
				children.stream().filter(child -> child.getId().equals(initialDetailsForm.getInnovationAreaCategoryId())).collect(Collectors.toList());
		if (matchingChild.isEmpty()) {
			return asList(fieldError("innovationAreaCategoryId",
					initialDetailsForm.getInnovationAreaCategoryId(),
					"competition.setup.innovation.area.must.be.selected",
                    singletonList(children.stream().map(child -> child.getName()).collect(Collectors.joining(", ")))));
		}
		competition.setInnovationArea(initialDetailsForm.getInnovationAreaCategoryId());

		competitionService.update(competition);
        return competitionService.initApplicationFormByCompetitionType(competition.getId(), initialDetailsForm.getCompetitionTypeId()).getErrors();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> objectId) {
		return performAutoSaveField(competitionResource, fieldName, value);
	}

    @Override
	protected List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) throws ParseException {
        switch (fieldName) {
            case "title":
                competitionResource.setName(value);
                break;
            case "competitionTypeId":
                competitionResource.setCompetitionType(Long.parseLong(value));
                break;
            case "innovationSectorCategoryId":
                competitionResource.setInnovationSector(Long.parseLong(value));
                break;
            case "innovationAreaCategoryId":
                competitionResource.setInnovationArea(Long.parseLong(value));
                break;
            case "leadTechnologistUserId":
                competitionResource.setLeadTechnologist(Long.parseLong(value));
                break;
            case "executiveUserId":
                competitionResource.setExecutive(Long.parseLong(value));
                break;
            case "openingDate":
                try {
                    String[] dateParts = value.split("-");
                    LocalDateTime startDate = LocalDateTime.of(
                            Integer.parseInt(dateParts[2]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[0]),
                            0, 0, 0);
                    competitionResource.setStartDate(startDate);

                    errors.addAll(saveOpeningDateAsMilestone(startDate, competitionResource.getId()));
                    if(!errors.isEmpty()) {
                        return errors;
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    return asList(fieldError(OPENINGDATE_FIELDNAME, null, "competition.setup.opening.date.not.able.to.save"));
                }
                break;
            default:
                return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
        }

        return errors;
    }

	private List<Error> validateOpeningDate(LocalDateTime openingDate) {
	    if(openingDate.getYear() > 9999) {
            return asList(fieldError(OPENINGDATE_FIELDNAME, openingDate.toString(), "validation.initialdetailsform.openingdateyear.range"));
        }

        if (openingDate.isBefore(LocalDateTime.now())) {
            return asList(fieldError(OPENINGDATE_FIELDNAME, openingDate.toString(), "competition.setup.opening.date.not.in.future"));
        }

        return Collections.emptyList();
    }

	private List<Error> saveOpeningDateAsMilestone(LocalDateTime openingDate, Long competitionId) {
        List<Error> errors = validateOpeningDate(openingDate);
        if(!errors.isEmpty()) {
            return errors;
        }

	    MilestoneViewModel milestoneEntry = new MilestoneViewModel(MilestoneType.OPEN_DATE, openingDate);


        List<MilestoneResource> milestones = milestoneService.getAllMilestonesByCompetitionId(competitionId);
        if(milestones.isEmpty()) {
            milestones = competitionSetupMilestoneService.createMilestonesForCompetition(competitionId);
        }
        milestones.sort((c1, c2) -> c1.getType().compareTo(c2.getType()));

		LinkedMap<String, MilestoneViewModel> milestoneEntryMap = new LinkedMap<>();
		milestoneEntryMap.put(MilestoneType.OPEN_DATE.name(), milestoneEntry);

		return competitionSetupMilestoneService.updateMilestonesForCompetition(milestones, milestoneEntryMap, competitionId);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return InitialDetailsForm.class.equals(clazz);
	}
}
