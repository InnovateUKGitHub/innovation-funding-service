package com.worth.ifs.form.repository;

import java.util.List;

import com.worth.ifs.form.domain.FormInput;

import com.worth.ifs.form.resource.FormInputScope;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormInputRepository extends PagingAndSortingRepository<FormInput, Long> {
    List<FormInput> findAll();
    List<FormInput> findByCompetitionId(Long competitionId);
    List<FormInput> findByCompetitionIdAndScope(Long competitionId, FormInputScope scope);
    List<FormInput> findByQuestionId(Long questionId);
    List<FormInput> findByQuestionIdAndScope(Long questionId, FormInputScope scope);
}