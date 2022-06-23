package org.innovateuk.ifs.horizon.repository;

import org.innovateuk.ifs.horizon.domain.CompetitionHorizonWorkProgramme;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompetitionHorizonWorkProgrammeRepository extends CrudRepository<CompetitionHorizonWorkProgramme, Long> {

    List<CompetitionHorizonWorkProgramme> findByCompetitionId(long competitionId);
    void deleteAllByCompetitionId(long competitionId);
}
