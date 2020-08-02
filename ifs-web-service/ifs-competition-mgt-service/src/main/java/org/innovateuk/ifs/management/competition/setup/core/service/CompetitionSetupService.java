package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Optional;

/**
 * service for logic around handling the various sections of competition setup.
 */
public interface CompetitionSetupService {

    CompetitionSetupViewModel populateCompetitionSectionModelAttributes(CompetitionResource competitionResource,
                                                                        UserResource user,
                                                                        CompetitionSetupSection section);

    CompetitionSetupSubsectionViewModel populateCompetitionSubsectionModelAttributes(CompetitionResource competitionResource,
                                                                                     CompetitionSetupSection section, CompetitionSetupSubsection subsection,
                                                                                     Optional<Long> objectId);

    CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
                                            CompetitionSetupSection section);

    CompetitionSetupForm getSubsectionFormData(CompetitionResource competitionResource,
                                               CompetitionSetupSection section,
                                               CompetitionSetupSubsection subsection,
                                               Optional<Long> objectId);

    ServiceResult<Void> saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
                                                    CompetitionResource competitionResource, CompetitionSetupSection section);

    ServiceResult<Void> saveCompetitionSetupSubsection(CompetitionSetupForm competitionSetupForm,
                                                       CompetitionResource competitionResource, CompetitionSetupSection section, CompetitionSetupSubsection subsection);

    boolean hasInitialDetailsBeenPreviouslySubmitted(Long competitionId);

    boolean isCompetitionReadyToOpen(CompetitionResource competitionResource);

    ServiceResult<Void> setCompetitionAsReadyToOpen(Long competitionId);

    ServiceResult<Void> setCompetitionAsCompetitionSetup(Long competitionId);

    ServiceResult<Void> deleteCompetition(long competitionId);
}
