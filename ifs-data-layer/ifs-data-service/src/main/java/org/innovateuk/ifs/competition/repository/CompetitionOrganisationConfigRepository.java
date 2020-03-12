package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;

import java.util.List;

public interface CompetitionOrganisationConfigRepository {

    List<CompetitionOrganisationConfig> findByCompetitionId(Long competitionId);

    List<CompetitionOrganisationConfig> findAllInternationalApplicationAllowedIsTrue();
}
