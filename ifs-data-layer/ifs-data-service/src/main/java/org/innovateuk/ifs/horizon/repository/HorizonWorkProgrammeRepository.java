package org.innovateuk.ifs.horizon.repository;

import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HorizonWorkProgrammeRepository extends CrudRepository<ApplicationHorizonWorkProgramme, Long> {

    List<ApplicationHorizonWorkProgramme> findByApplicationId(long applicationId);
    void deleteAllByApplicationId(long application);
}
