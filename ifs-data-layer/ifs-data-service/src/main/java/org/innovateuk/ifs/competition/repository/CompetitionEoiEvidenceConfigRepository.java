package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompetitionEoiEvidenceConfigRepository extends CrudRepository<CompetitionEoiEvidenceConfig, Long>  {

    Optional<CompetitionEoiEvidenceConfig> findOneByCompetitionId(long competitionId);
}
