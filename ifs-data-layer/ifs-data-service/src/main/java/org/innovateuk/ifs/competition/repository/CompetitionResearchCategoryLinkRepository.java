package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionResearchCategoryLink;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompetitionResearchCategoryLinkRepository extends CrudRepository<CompetitionResearchCategoryLink, Long> {

    List<CompetitionResearchCategoryLink> findAllByCompetitionId(Long competitionId);
}
