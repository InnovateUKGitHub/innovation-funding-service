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

    @Query("SELECT c FROM Competition c WHERE c.id IN (SELECT c1.id FROM Competition c1 " +
                "WHERE CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c1.id)" +
                    "AND CURRENT_TIMESTAMP <= (SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' AND m.competition.id = c1.id))")
    List<Competition> findLive();

    @Query("SELECT COUNT(c) FROM Competition c WHERE c.id IN (SELECT c1.id FROM Competition c1 " +
                "WHERE CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c1.id) " +
                    "AND CURRENT_TIMESTAMP <= (SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' AND m.competition.id = c1.id))")
    Long countLive();

    @Query("SELECT c FROM Competition c WHERE c.id IN (SELECT c1.id FROM Competition c1 " +
                "WHERE CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' and m.competition.id = c1.id)) " +
                    "AND c.status = 'COMPETITION_SETUP_FINISHED'")
    List<Competition> findProjectSetup();

    @Query("SELECT COUNT(c) FROM Competition c WHERE c.id IN (SELECT c1.id FROM Competition c1 " +
                "WHERE CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' and m.competition.id = c1.id)) " +
                    "AND c.status = 'COMPETITION_SETUP_FINISHED'")
    Long countProjectSetup();

    @Query("SELECT c FROM Competition c WHERE c.id IN (SELECT c1.id FROM Competition c1 " +
                "WHERE CURRENT_TIMESTAMP <= (SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c1.id)) " +
                    "OR c.status = 'COMPETITION_SETUP'")
    List<Competition> findUpcoming();

    @Query("SELECT COUNT(c) FROM Competition c WHERE c.id IN (SELECT c1.id FROM Competition c1 " +
                "WHERE CURRENT_TIMESTAMP <= (SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c1.id)) " +
                    "OR c.status = 'COMPETITION_SETUP'")
    Long countUpcoming();

}
