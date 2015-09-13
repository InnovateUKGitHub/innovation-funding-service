package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Application;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);
    Application findById(@Param("id") Long id);
    List<Application> findAll();
}
