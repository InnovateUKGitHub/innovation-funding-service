package org.innovateuk.ifs.horizon.repository;

import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HorizonWorkProgrammeRepository extends CrudRepository<HorizonWorkProgramme, Long> {

    String ROOT = "SELECT h FROM HorizonWorkProgramme h " +
            "WHERE h.parentWorkProgramme IS NULL";

    String PARENT = "SELECT h FROM HorizonWorkProgramme h " +
            "WHERE h.parentWorkProgramme.id = :parentId";

    @Query(ROOT)
    List<HorizonWorkProgramme> findRootWorkPorgrammes();

    @Query(PARENT)
    List<HorizonWorkProgramme> findByParentId(Long parentId);
}
