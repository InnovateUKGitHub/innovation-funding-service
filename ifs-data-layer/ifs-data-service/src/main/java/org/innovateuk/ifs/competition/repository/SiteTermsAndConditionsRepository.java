package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for SiteTermsAndConditions
 */
public interface SiteTermsAndConditionsRepository extends CrudRepository<SiteTermsAndConditions, Long> {

    SiteTermsAndConditions findTopByOrderByVersionDesc();
}
