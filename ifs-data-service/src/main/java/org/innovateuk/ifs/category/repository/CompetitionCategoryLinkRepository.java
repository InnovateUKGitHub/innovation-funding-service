package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionCategoryLinkRepository extends CrudRepository<CompetitionCategoryLink, Long> {

    List<CompetitionCategoryLink> findByCompetitionId(Long competitionId);
    List<CompetitionCategoryLink> findAllByCompetitionIdAndCategoryType(Long competitionId, CategoryType type);
    CompetitionCategoryLink findByCompetitionIdAndCategoryType(Long competitionId, CategoryType type);
}