package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.RejectionReason;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface RejectionReasonRepository extends CrudRepository<RejectionReason, Long> {

    @Override
    List<RejectionReason> findAll();

    List<RejectionReason> findByActiveTrueOrderByPriorityAsc();

}