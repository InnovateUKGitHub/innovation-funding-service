package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationEoiEvidenceResponseRepository extends CrudRepository<ApplicationEoiEvidenceResponse, Long>  {

    Optional<ApplicationEoiEvidenceResponse> findOneByApplicationId(long applicationId);
}
