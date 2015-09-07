package com.worth.ifs.repository;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Question;
import com.worth.ifs.domain.Response;
import com.worth.ifs.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "response", path = "response")
public interface ResponseRepository extends PagingAndSortingRepository<Response, Long> {
    List<Response> findByUpdatedBy(@Param("updatedBy") UserApplicationRole updatedBy);
    Response findByApplicationAndQuestion(@Param("application") Application application, @Param("question") Question question);
    Response findByApplicationIdAndQuestionId(@Param("applicationId") Long applicationId, @Param("questionId") Long questionId);
    List<Response> findAll();
}