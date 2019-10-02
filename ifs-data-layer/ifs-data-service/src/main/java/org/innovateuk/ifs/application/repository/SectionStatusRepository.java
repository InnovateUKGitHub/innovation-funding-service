package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.SectionStatus;
import org.springframework.data.repository.CrudRepository;

public interface SectionStatusRepository extends CrudRepository<SectionStatus, Long> {
    SectionStatus getByApplicationIdAndSectionIdAndOrganisationIsNull(long applicationId, long sectionId);
    SectionStatus getByApplicationIdAndSectionIdAndOrganisationId(long applicationId, long sectionId, long organisationId);

}