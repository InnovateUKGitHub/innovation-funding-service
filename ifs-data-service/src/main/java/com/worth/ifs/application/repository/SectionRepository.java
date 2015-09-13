package com.worth.ifs.application.repository;

import com.worth.ifs.application.domain.Section;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {
    List<Section> findAll();
    Section findByName(@Param("name") String name);
}