package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Ethnicity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface EthnicityRepository extends CrudRepository<Ethnicity, Long> {
    List<Ethnicity> findByActiveTrueOrderByPriorityAsc();

    Ethnicity findOneByDescription(String description);
}
