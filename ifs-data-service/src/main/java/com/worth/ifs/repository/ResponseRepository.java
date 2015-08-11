package com.worth.ifs.repository;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Response;
import com.worth.ifs.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "response", path = "response")
public interface ResponseRepository extends PagingAndSortingRepository<Response, Long> {
    List<Response> findByUserApplicationRole(@Param("userApplicationRole") UserApplicationRole userApplicationRole);

    List<Response> findAll();
}