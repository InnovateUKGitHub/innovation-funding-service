package org.innovateuk.ifs.competitionsetup.repository;

import org.innovateuk.ifs.competitionsetup.domain.SiteTermsAndConditions;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for SiteTermsAndConditions
 */
public interface SiteTermsAndConditionsRepository extends CrudRepository<SiteTermsAndConditions, Long> {

    SiteTermsAndConditions findTopByOrderByVersionDesc();
}
