package com.worth.ifs.repository;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by wouter on 29/07/15.
 */
@RepositoryRestResource(collectionResourceRel = "application", path = "application")
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);
    List<Application> findById(@Param("id") Long id);
    List<Application> findAll();
}
