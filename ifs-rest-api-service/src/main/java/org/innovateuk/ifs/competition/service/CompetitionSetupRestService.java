package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;


/**
 * Interface for CRUD operations on {@link CompetitionResource} related data when in setup.
 */
public interface CompetitionSetupRestService {
    RestResult<Void> update(CompetitionResource competition);

    RestResult<Void> updateCompetitionInitialDetails(CompetitionResource competition);

    RestResult<CompetitionResource> create();

    RestResult<Void> markSectionComplete(long competitionId, CompetitionSetupSection section);

    RestResult<Void> markSectionInComplete(long competitionId, CompetitionSetupSection section);

    RestResult<Void> markSubSectionComplete(long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection);

    RestResult<Void> markSubSectionInComplete(long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection);

    RestResult<String> generateCompetitionCode(long competitionId, ZonedDateTime openingDate);

    RestResult<Void> initApplicationForm(long competitionId, long competitionTypeId);

    RestResult<Void> markAsSetup(long competitionId);

    RestResult<Void> returnToSetup(long competitionId);

    RestResult<CompetitionResource> createNonIfs();

    RestResult<Map<CompetitionSetupSection, Optional<Boolean>>> getSectionStatuses(long competitionId);

    RestResult<Map<CompetitionSetupSubsection, Optional<Boolean>>> getSubsectionStatuses(long competitionId);
}
