package org.innovateuk.ifs.competitionsetup.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSectionSaver;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionSetupServiceImpl implements CompetitionSetupService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);
	
	@Autowired
	private CompetitionService competitionService;

    private Map<CompetitionSetupSection, CompetitionSetupFormPopulator> formPopulators;
    private Map<CompetitionSetupSubsection, CompetitionSetupSubsectionFormPopulator> subsectionFormPopulators;

	private Map<CompetitionSetupSection, CompetitionSetupSectionSaver> sectionSavers;
    private Map<CompetitionSetupSubsection, CompetitionSetupSubsectionSaver> subsectionSavers;

	private Map<CompetitionSetupSection, CompetitionSetupSectionModelPopulator> modelPopulators;
	private Map<CompetitionSetupSubsection, CompetitionSetupSubsectionModelPopulator> subsectionModelPopulators;

	
	@Autowired
	public void setCompetitionSetupFormPopulators(Collection<CompetitionSetupFormPopulator> populators) {
		formPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToFill(), Function.identity()));
	}

    @Autowired
    public void setCompetitionSetupSubsectionFormPopulators(Collection<CompetitionSetupSubsectionFormPopulator> populators) {
        subsectionFormPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToFill(), Function.identity()));
    }

	@Autowired
    public void setCompetitionSetupSectionSavers(Collection<CompetitionSetupSectionSaver> savers) {
        sectionSavers = savers.stream().collect(Collectors.toMap(p -> p.sectionToSave(), Function.identity()));
    }

    @Autowired
    public void setCompetitionSetupSubsectionSavers(Collection<CompetitionSetupSubsectionSaver> savers) {
        subsectionSavers = savers.stream().collect(Collectors.toMap(p -> p.subsectionToSave(), Function.identity()));
    }
	
	@Autowired
	public void setCompetitionSetupSectionModelPopulators(Collection<CompetitionSetupSectionModelPopulator> populators) {
		modelPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToPopulateModel(), Function.identity()));
	}

    @Autowired
    public void setCompetitionSetupSubsectionModelPopulators(Collection<CompetitionSetupSubsectionModelPopulator> populators) {
        subsectionModelPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToPopulateModel(), Function.identity()));
    }

	@Override
    public void populateCompetitionSectionModelAttributes(Model model, CompetitionResource competitionResource,
                                                          CompetitionSetupSection section) {

        populateGeneralModelAttributes(model, competitionResource, section);

        CompetitionSetupSectionModelPopulator populator = modelPopulators.get(section);

        if(populator != null) {
            populator.populateModel(model, competitionResource);
        }
    }

    @Override
    public void populateCompetitionSubsectionModelAttributes(Model model, CompetitionResource competitionResource,
                                                             CompetitionSetupSection section, CompetitionSetupSubsection subsection,
                                                             Optional<Long> objectId) {

        checkIfSubsectionIsInSection(section, subsection);
        populateGeneralModelAttributes(model, competitionResource, section);
        CompetitionSetupSubsectionModelPopulator populator = subsectionModelPopulators.get(subsection);

        if(populator != null) {
            populator.populateModel(model, competitionResource, objectId);
        }
    }


	@Override
	public CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
			CompetitionSetupSection section) {
		
		CompetitionSetupFormPopulator populator = formPopulators.get(section);
		if(populator == null) {
			LOG.error("unable to populate form for section " + section);
			throw new IllegalArgumentException();
		}
		
		return populator.populateForm(competitionResource);
	}

	@Override
	public CompetitionSetupForm getSubsectionFormData(CompetitionResource competitionResource,
													 CompetitionSetupSection section,
													 CompetitionSetupSubsection subsection,
													 Optional<Long> objectId) {

        checkIfSubsectionIsInSection(section, subsection);
		CompetitionSetupSubsectionFormPopulator populator = subsectionFormPopulators.get(subsection);
		if(populator == null) {
			LOG.error("unable to populate form for subsection " + subsection);
			throw new IllegalArgumentException();
		}

		return populator.populateForm(competitionResource, objectId);
	}


	@Override
	public ServiceResult<Void> autoSaveCompetitionSetupSection(CompetitionResource competitionResource, CompetitionSetupSection section,
                                                       String fieldName, String value, Optional<Long> objectId) {

		CompetitionSetupSectionSaver saver = sectionSavers.get(section);
		CompetitionSetupFormPopulator populator = formPopulators.get(section);
		if(saver == null || populator == null) {
			LOG.error("unable to save section " + section);
			throw new IllegalArgumentException();
		}

        return saver.autoSaveSectionField(competitionResource, populator.populateForm(competitionResource), fieldName, value, objectId);
	}

    @Override
    public ServiceResult<Void> autoSaveCompetitionSetupSubsection(CompetitionResource competitionResource, CompetitionSetupSection section,
                                                          CompetitionSetupSubsection subsection, String fieldName, String value, Optional<Long> objectId) {

        checkIfSubsectionIsInSection(section, subsection);
        CompetitionSetupSubsectionSaver saver = subsectionSavers.get(subsection);
		CompetitionSetupSubsectionFormPopulator populator = subsectionFormPopulators.get(subsection);
        if(saver == null || populator == null) {
            LOG.error("unable to save subsection " + subsection);
            throw new IllegalArgumentException();
        }

        return saver.autoSaveSectionField(competitionResource, populator.populateForm(competitionResource, objectId), fieldName, value, objectId);
    }

	@Override
	public ServiceResult<Void> saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
														   CompetitionResource competitionResource, CompetitionSetupSection section) {
		
		CompetitionSetupSectionSaver saver = sectionSavers.get(section);
		if(saver == null || !saver.supportsForm(competitionSetupForm.getClass())) {
			LOG.error("unable to save section " + section);
			throw new IllegalArgumentException();
		}
		
		return saver.saveSection(competitionResource, competitionSetupForm).andOnSuccess(() -> {
			if (competitionSetupForm.isMarkAsCompleteAction()) {
				return competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), section);
			}
			return serviceSuccess();
		});
	}

    @Override
    public ServiceResult<Void> saveCompetitionSetupSubsection(CompetitionSetupForm competitionSetupForm,
                                                      CompetitionResource competitionResource, CompetitionSetupSection section, CompetitionSetupSubsection subsection) {

        checkIfSubsectionIsInSection(section, subsection);

        CompetitionSetupSubsectionSaver saver = subsectionSavers.get(subsection);
        if(saver == null || !saver.supportsForm(competitionSetupForm.getClass())) {
            LOG.error("unable to save subsection " + subsection);
            throw new IllegalArgumentException();
        }

        return saver.saveSection(competitionResource, competitionSetupForm);
    }

	@Override
	public boolean isCompetitionReadyToOpen(CompetitionResource competitionResource) {
		if (competitionResource.getCompetitionStatus() != CompetitionStatus.COMPETITION_SETUP) {
			return false;
		}
		Optional<CompetitionSetupSection> notDoneSection = getRequiredSectionsForReadyToOpen().stream().filter(section ->
				(!competitionResource.getSectionSetupStatus().containsKey(section) ||
						!competitionResource.getSectionSetupStatus().get(section))).findFirst();

		return !notDoneSection.isPresent();
	}

	@Override
	public ServiceResult<Void> setCompetitionAsReadyToOpen(Long competitionId) {
		CompetitionResource competitionResource = competitionService.getById(competitionId);
		if (competitionResource.getCompetitionStatus() == CompetitionStatus.READY_TO_OPEN) {
            return serviceFailure(new Error("competition.setup.is.already.ready.to.open", HttpStatus.BAD_REQUEST));
		}

		if (isCompetitionReadyToOpen(competitionResource)) {
			return competitionService.markAsSetup(competitionId);
		} else {
			LOG.error("Requesting to set a competition (id:" + competitionId + ") as Read to Open, But the competition is not ready to open yet. " +
					"Please check all the madatory sections are done");
			throw new IllegalArgumentException();
		}
	}

	@Override
	public ServiceResult<Void> setCompetitionAsCompetitionSetup(Long competitionId) {
		return competitionService.returnToSetup(competitionId);
	}

	private List<CompetitionSetupSection> getRequiredSectionsForReadyToOpen() {
		List<CompetitionSetupSection> requiredSections = new ArrayList<>();
		requiredSections.add(CompetitionSetupSection.INITIAL_DETAILS);
		requiredSections.add(CompetitionSetupSection.ADDITIONAL_INFO);
		requiredSections.add(CompetitionSetupSection.ELIGIBILITY);
		requiredSections.add(CompetitionSetupSection.MILESTONES);
		requiredSections.add(CompetitionSetupSection.APPLICATION_FORM);
		requiredSections.add(CompetitionSetupSection.CONTENT);
		return requiredSections;
	}

	private void populateGeneralModelAttributes(Model model, CompetitionResource competitionResource, CompetitionSetupSection section) {
		boolean editable = (!competitionResource.getSectionSetupStatus().containsKey(section)
				|| !competitionResource.getSectionSetupStatus().get(section))
				&& !section.preventEdit(competitionResource);
		model.addAttribute("editable", editable);

		model.addAttribute("competition", competitionResource);
		model.addAttribute("currentSection", section);

		if (section.hasDisplayableSetupFragment()) {
			model.addAttribute("currentSectionFragment", "section-" + section.getPath());
		}

		model.addAttribute("allSections", CompetitionSetupSection.values());
        model.addAttribute("isInitialComplete", isInitialComplete(competitionResource));

		populateCompetitionStateModelAttributes(model, competitionResource, section);
	}

	private boolean isInitialComplete(CompetitionResource competitionResource) {
		return competitionResource.getSectionSetupStatus().containsKey(CompetitionSetupSection.INITIAL_DETAILS) || competitionResource.getSetupComplete();
	}

	private void populateCompetitionStateModelAttributes(Model model, CompetitionResource competitionResource, CompetitionSetupSection section) {
		model.addAttribute("preventEdit", section.preventEdit(competitionResource));
		model.addAttribute("isSetupAndLive", competitionResource.isSetupAndLive());
		model.addAttribute("setupComplete", competitionResource.getSetupComplete());
	}


	private void checkIfSubsectionIsInSection(CompetitionSetupSection section, CompetitionSetupSubsection subsection) {
        if(!section.getSubsections().contains(subsection)) {
            LOG.error("Subsection(" + subsection + ") not found on section " + section);
            throw new IllegalArgumentException();
        }
    }

}
