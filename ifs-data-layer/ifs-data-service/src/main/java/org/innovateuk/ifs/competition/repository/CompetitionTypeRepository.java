package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionTypeRepository extends CrudRepository<CompetitionType, Long> {

    CompetitionType findByName(String name);

    List<CompetitionType> findAllByOrderByNameAsc();
}
