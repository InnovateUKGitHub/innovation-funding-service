package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResponseRepository extends PagingAndSortingRepository<Response, Long> {
    List<Response> findByUpdatedBy(@Param("updatedBy") ProcessRole updatedBy);
    Response findByApplicationAndQuestion(@Param("application") Application application, @Param("question") Question question);
    Response findByApplicationIdAndQuestionId(@Param("applicationId") Long applicationId, @Param("questionId") Long questionId);
    List<Response> findAll();
}