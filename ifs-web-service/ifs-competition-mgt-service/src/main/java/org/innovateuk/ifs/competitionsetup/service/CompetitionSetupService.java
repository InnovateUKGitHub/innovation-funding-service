package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.ui.Model;

import java.util.Optional;

/**
 * service for logic around handling the various sections of competition setup.
 */
public interface CompetitionSetupService {

	void populateCompetitionSectionModelAttributes(Model model, CompetitionResource competitionResource,
			CompetitionSetupSection section);

    void populateCompetitionSubsectionModelAttributes(Model model, CompetitionResource competitionResource,
                                                      CompetitionSetupSection section, CompetitionSetupSubsection subsection,
                                                      Optional<Long> objectId);

    CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
                                            CompetitionSetupSection section);

	CompetitionSetupForm getSubsectionFormData(CompetitionResource competitionResource,
											CompetitionSetupSection section,
											CompetitionSetupSubsection subsection,
                                               Optional<Long> objectId);

	ServiceResult<Void> autoSaveCompetitionSetupSection(CompetitionResource competitionResource, CompetitionSetupSection section,
                                                String fieldName, String value, Optional<Long> objectId);

	ServiceResult<Void> autoSaveCompetitionSetupSubsection(CompetitionResource competitionResource, CompetitionSetupSection section,
												   CompetitionSetupSubsection subsection, String fieldName, String value, Optional<Long> objectId);

    ServiceResult<Void> saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
													CompetitionResource competitionResource, CompetitionSetupSection section);

	ServiceResult<Void> saveCompetitionSetupSubsection(CompetitionSetupForm competitionSetupForm,
											   CompetitionResource competitionResource, CompetitionSetupSection section, CompetitionSetupSubsection subsection);

	boolean isCompetitionReadyToOpen(CompetitionResource competitionResource);

	void setCompetitionAsReadyToOpen(Long competitionId);

	void setCompetitionAsCompetitionSetup(Long competitionId);

}
