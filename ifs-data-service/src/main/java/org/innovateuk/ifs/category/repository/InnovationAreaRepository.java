package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InnovationAreaRepository extends CrudRepository<InnovationArea, Long> {

    InnovationArea findByName(String name);

    List<InnovationArea> findAllByOrderByNameAsc();

    List<InnovationArea> findAll();
}
