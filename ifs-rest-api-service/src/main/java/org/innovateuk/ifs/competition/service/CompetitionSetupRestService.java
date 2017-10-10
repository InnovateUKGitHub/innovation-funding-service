package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

import java.time.ZonedDateTime;
import java.util.Map;


/**
 * Interface for CRUD operations on {@link CompetitionResource} related data.
 */
public interface CompetitionSetupRestService {
    RestResult<Void> update(CompetitionResource competition);

    RestResult<Void> updateCompetitionInitialDetails(CompetitionResource competition);

    RestResult<CompetitionResource> create();

    RestResult<Void> markSectionComplete(long competitionId, CompetitionSetupSection section);

    RestResult<Void> markSectionInComplete(long competitionId, CompetitionSetupSection section);

    RestResult<String> generateCompetitionCode(long competitionId, ZonedDateTime openingDate);

    RestResult<Void> initApplicationForm(long competitionId, long competitionTypeId);

    RestResult<Void> markAsSetup(long competitionId);

    RestResult<Void> returnToSetup(long competitionId);

    RestResult<CompetitionResource> createNonIfs();

    RestResult<Map<CompetitionSetupSection, Boolean>> getSectionStatuses(long competitionId);
}
