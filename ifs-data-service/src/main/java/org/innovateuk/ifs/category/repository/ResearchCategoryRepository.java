package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResearchCategoryRepository extends CrudRepository<ResearchCategory, Long> {

    ResearchCategory findById(long id);

    List<ResearchCategory> findAll();

    List<ResearchCategory> findAllByOrderByNameAsc();
}
