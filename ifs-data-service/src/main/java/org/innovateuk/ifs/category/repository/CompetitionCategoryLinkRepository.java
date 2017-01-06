package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CompetitionCategoryLinkRepository extends CrudRepository<CompetitionCategoryLink, Long> {

    List<CompetitionCategoryLink> findByCompetitionId(Long competitionId);
    List<CompetitionCategoryLink> findAllByCompetitionIdAndCategoryType(Long competitionId, CategoryType type);
    CompetitionCategoryLink findByCompetitionIdAndCategory_Type(Long competitionId, CategoryType type);
}