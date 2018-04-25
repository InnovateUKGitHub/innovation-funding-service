package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for GrantTermsAndConditions
 */
public interface GrantTermsAndConditionsRepository extends CrudRepository<GrantTermsAndConditions, Long> {

    String DEFAULT_TEMPLATE_NAME = "default-terms-and-conditions";

    GrantTermsAndConditions findOneByTemplate(String templateName);
}
