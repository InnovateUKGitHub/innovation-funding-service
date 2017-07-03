package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
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
    List<FormInput> findByCompetitionIdAndTypeIn(Long competitionId, List<FormInputType> type);
    FormInput findByQuestionIdAndScopeAndType(Long questionId, FormInputScope scope, FormInputType type);

    //Return only active form inputs for FormInputService.
    List<FormInput> findByCompetitionIdAndActiveTrueOrderByPriorityAsc(Long competitionId);
    List<FormInput> findByCompetitionIdAndScopeAndActiveTrueOrderByPriorityAsc(Long competitionId, FormInputScope scope);
    List<FormInput> findByQuestionIdAndActiveTrueOrderByPriorityAsc(Long questionId);
    List<FormInput> findByQuestionIdAndScopeAndActiveTrueOrderByPriorityAsc(Long questionId, FormInputScope scope);
}
