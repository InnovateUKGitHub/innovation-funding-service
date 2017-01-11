package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.InnovationSector;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InnovationSectorRepository extends CrudRepository<InnovationSector, Long> {

    List<InnovationSector> findAllByOrderByNameAsc();

    List<InnovationSector> findAll();
}
