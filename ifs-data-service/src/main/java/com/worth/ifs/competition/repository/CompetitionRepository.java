package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.Competition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionRepository extends PagingAndSortingRepository<Competition, Long> {

    List<Competition> findByName(String name);
    Competition findById(Long id);
    @Override
    List<Competition> findAll();
    List<Competition> findByCodeLike(String code);

    @Query("SELECT c FROM Competition c, Milestone m WHERE c.id = m.competition.id AND CURRENT_TIMESTAMP >= (select m.date from m where m.name = 'OPEN_DATE') " +
            "AND CURRENT_TIMESTAMP <= (select m.date from m where m.name = 'ASSESSOR_DEADLINE') AND c.status = 'COMPETITION_SETUP_FINISHED'")
    List<Competition> findLive();
    @Query("SELECT count(c) FROM Competition c, Milestone m WHERE c.id = m.competition.id AND CURRENT_TIMESTAMP >= (select m.date from m where m.name = 'OPEN_DATE')  " +
            "AND CURRENT_TIMESTAMP <= (select m.date from m where m.name = 'ASSESSOR_DEADLINE') AND c.status = 'COMPETITION_SETUP_FINISHED'")
    Long countLive();

    @Query("SELECT c FROM Competition c, Milestone m WHERE c.id = m.competition.id AND CURRENT_TIMESTAMP >= (select m.date from m where m.name = 'ASSESSOR_DEADLINE')" +
            " AND c.status = 'COMPETITION_SETUP_FINISHED'")
    List<Competition> findProjectSetup();
    @Query("SELECT count(c) FROM Competition c WHERE c.status = 'COMPETITION_SETUP_FINISHED'")
    Long countProjectSetup();

    @Query("SELECT c FROM Competition c, Milestone m WHERE c.id = m.competition.id AND (CURRENT_TIMESTAMP <= (select m.date from m where m.name = 'OPEN_DATE') AND c.status = 'COMPETITION_SETUP_FINISHED') " +
            "OR c.status = 'COMPETITION_SETUP'")
    List<Competition> findUpcoming();
    @Query("SELECT count(c) FROM Competition c, Milestone m WHERE (CURRENT_TIMESTAMP <= (select m.date from m where m.name = 'OPEN_DATE') AND c.status = 'COMPETITION_SETUP_FINISHED') " +
            "OR c.status = ''")
    Long countUpcoming();

}
