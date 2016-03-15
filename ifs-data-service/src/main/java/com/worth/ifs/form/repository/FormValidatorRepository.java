package com.worth.ifs.form.repository;

import com.worth.ifs.form.domain.FormValidator;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormValidatorRepository extends PagingAndSortingRepository<FormValidator, Long> {
    List<FormValidator> findAll();
    FormValidator findById(@Param("id") Long id);
}