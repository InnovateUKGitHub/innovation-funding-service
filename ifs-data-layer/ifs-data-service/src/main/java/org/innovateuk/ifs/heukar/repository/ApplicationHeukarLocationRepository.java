package org.innovateuk.ifs.heukar.repository;

import org.innovateuk.ifs.heukar.domain.ApplicationHeukarLocation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApplicationHeukarLocationRepository extends CrudRepository<ApplicationHeukarLocation, Long> {

    List<ApplicationHeukarLocation> findByApplicationId(long applicationId);

    void deleteAllByApplicationId(long application);

}
