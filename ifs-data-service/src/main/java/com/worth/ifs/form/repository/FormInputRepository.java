package com.worth.ifs.form.repository;

import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.resource.FormInputScope;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface FormInputRepository extends PagingAndSortingRepository<FormInput, Long> {
    List<FormInput> findAll();
    List<FormInput> findByCompetitionId(Long competitionId);
    List<FormInput> findByCompetitionIdOrderByPriorityAsc(Long competitionId);
    List<FormInput> findByCompetitionIdAndScopeOrderByPriorityAsc(Long competitionId, FormInputScope scope);
    List<FormInput> findByQuestionIdOrderByPriorityAsc(Long questionId);
    List<FormInput> findByQuestionIdAndScopeOrderByPriorityAsc(Long questionId, FormInputScope scope);
    FormInput findByQuestionIdAndScopeAndFormInputType_Title(Long questionId, FormInputScope scope, String title);
}