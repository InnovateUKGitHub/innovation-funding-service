package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Contract;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ContractRepository extends CrudRepository<Contract, Long> {

    Contract findByCurrentTrue();
}
