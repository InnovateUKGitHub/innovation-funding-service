package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionEoiDocument;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompetitionEoiDocumentRepository extends CrudRepository<CompetitionEoiDocument, Long>  {

    List<CompetitionEoiDocument> findByCompetitionEoiEvidenceConfigId(long competitionEoiEvidenceConfigId);

}
