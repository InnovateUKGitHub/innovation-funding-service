package com.worth.ifs.repository;

import com.worth.ifs.domain.Question;
import com.worth.ifs.domain.Section;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "question", path = "question")
public interface SectionRepository extends PagingAndSortingRepository<Section, Long> {
    List<Section> findAll();
    Section findByName(@Param("name") String name);
}